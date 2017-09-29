This project illustrates some setup and practices when using Spring boot and Spring Data Neo4j with a causal cluster.

This includes:
* The use of multiple URIs for initial connection on core members of the cluster
* The read only transactions that allow routing requests to replica servers
* Setup of detection of stale connections through connection testing
* Application level retry mechanisms (see comments in UserService)

More detail is available in the [documentation](http://neo4j.com/docs/ogm-manual/current/reference/#reference:ha).

There are REST 2 endpoints:
* `GET /users` : retrieves users using a read only transaction, hitting replica servers if they are available instead of core servers.
* `POST /users` : creates a user. Retry the operation if the luster in not available.

To run the example :
- start a cluster (see the script `start-neo-cluster.sh` : it starts a cluster with docker)
- run the spring boot application as usual