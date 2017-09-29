#!/bin/bash
# Usage: ./cluster.sh --cores={number|optional|default3} --replicas={number|optional|default0} --erase={boolean|optional|default false}
# Use at your own risk!

function erase() {
    if $ERASE
    then
        for i in $(seq 1 $NUMBER_OF_CORES); do 
            docker rm -f core${i}; 
        done

        for i in $(seq 1 $NUMBER_OF_REPLICAS); do 
            docker rm -f replica${i}; 
        done
    fi
}

function create_cores() {
    for i in $(seq 1 $NUMBER_OF_CORES); do
        run $i "core";
    done
}

function create_replicas() {
    if [ "$NUMBER_OF_REPLICAS" -gt "0" ];
    then
        for i in $(seq 1 $NUMBER_OF_REPLICAS); do
            run $[i+10] "replica";
        done
    fi
}

function run() {
    INSTANCE=$1
    TYPE=$2
    INSTANCE_TYPE=CORE
    NAME="$TYPE$INSTANCE"
    if [ "$TYPE" = "replica" ];
        then
            INSTANCE_TYPE=READ_REPLICA;
            N=$[INSTANCE-10];
            NAME="$TYPE$N";
    fi  
    URL=$(sed -nE 's|(tcp://)([0-9a-z\.]+)(\:[0-9]+)?|\2|p' <<< $DOCKER_HOST)
    BOLT_PORT=$[7687+$INSTANCE-1]
    HTTP_PORT=$[7474+$INSTANCE-1]

    docker run --name=$NAME --detach --network=cluster \
            --publish=$HTTP_PORT:$HTTP_PORT --publish=$BOLT_PORT:$BOLT_PORT \
            --env=NEO4J_dbms_mode=$INSTANCE_TYPE \
            --env=NEO4J_causalClustering_expectedCoreClusterSize=$NUMBER_OF_CORES \
            --env=NEO4J_causalClustering_initialDiscoveryMembers=core1:5000,core2:5000,core3:5000 \
            --env=NEO4J_AUTH=none \
            --env=NEO4J_dbms_connectors_default__advertised__address=$URL \
            --env=NEO4J_dbms_connector_bolt_listen__address=:$BOLT_PORT \
            --env=NEO4J_dbms_connector_http_listen__address=:$HTTP_PORT \
            neo4j:3.2.1-enterprise
}


while [ "$#" -gt 0 ]; do
  case "$1" in
    -c) cores="$2"; shift 2;;
    -r) replicas="$2"; shift 2;;
    -e) erase="$2"; shift 2;;

    --cores=*) cores="${1#*=}"; shift 1;;
    --replicas=*) replicas="${1#*=}"; shift 1;;
    --erase=*) erase="${1#*=}"; shift 1;;
    --cores|--replicas|--erase) echo "$1 requires an argument" >&2; exit 1;;

    -*) echo "unknown option: $1" >&2; exit 1;;
    *) handle_argument "$1"; shift 1;;
  esac
done

NUMBER_OF_CORES=${cores:-3}
NUMBER_OF_REPLICAS=${replicas:-0}
ERASE=${erase:-false}


docker network create --driver=bridge cluster

erase
create_cores
create_replicas