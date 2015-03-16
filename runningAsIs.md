[![](http://opensource.flaptor.com/clusterfest/images/logo.png)](http://opensource.flaptor.com/clusterfest)


_For introduction to Clusterfest concepts please read [Clusterfest at a glance](atAGlance.md)_

# Running Clusterfest as is #
## Configuring the server ##

Configuration of the server is done in the **clustering.properties** config file.

Here you can config:

  * **clustering.nodes**: the list of nodes in the form address:port or address:port:installDir
  * **clustering.modules**: the list of modules in the form moduleName:moduleClass.
  * Any extra module configuration can be added here.

**Default modules will be fine, just add your nodes**

## Running the server ##

**start.sh**, **status.sh**, **stop.sh** scripts are provided to start and stop the server. Start.sh starts the clusterfest webapp in port 47050.

To see if it's running, check on your web browser
```
http://localhost:47050/clustering
```

## Adding clustering code to the node ##

The server will call remote methods on your nodes. For that to happen you must have a **NodeListener** created somewhere in your node code. You create it specifying a port where it will listen to (which must match the corresponding server node configuration).

```
NodeListener nodeListener = new NodeListener(port);
```

This will read a file called clustering.node.properties for configuration. There you can set:

  * **clustering.node.type**: The node type is a String that allows for further node discrimination, for example for identification in the node list home page (see [screenshot](http://opensource.flaptor.com/clusterfest/screenshots.html)), or for enabling different behaviors within a module
  * **clustering.node.listeners**: a list of pairs context:className meaning the class to be instantiated and listen at that context

**Again: Default module listeners will be fine, just set the type of your node and the port you will listen at**

[home](home.md)