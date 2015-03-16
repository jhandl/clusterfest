[![](http://opensource.flaptor.com/clusterfest/images/logo.png)](http://opensource.flaptor.com/clusterfest)

_For introduction on how clusterfest works please read [ClusterManager internals](clusterManagerInternals.md)_

# Adding a module #

A module provides some functionality over some or all the nodes of the clustering framework. A typical module contains server and node side code.

You can check all this with the Monitor module. See the [server-side code](http://code.google.com/p/clusterfest/source/browse/trunk/src/com/flaptor/clusterfest/monitoring/MonitorModule.java) and the [node-side code](http://code.google.com/p/clusterfest/source/browse/trunk/src/com/flaptor/clusterfest/monitoring/node/MonitoreableImplementation.java)

## Creating the module server-side code ##

To create a module you must implement the interface [Module](http://code.google.com/p/clusterfest/source/browse/trunk/src/com/flaptor/clusterfest/Module.java). This interface is very little and is meant to notify modules when nodes are added or removed from the cluster. From there on, you can create any type of modules.

An implementation that will serve most needs is [AbstractModule](http://code.google.com/p/clusterfest/source/browse/trunk/src/com/flaptor/clusterfest/AbstractModule.java), an abstract class that already has most of the functionality for registering nodes. You will have to implement these methods:

```
abstract public boolean nodeBelongs(Node node) throws NodeUnreachableException;
```
to determine if a node belongs to this module (maybe not all nodes belong). If the node is unreachable you can throw a NodeUnreachableException and leave that decision for later. `Cluster` periodically goes through the node list and calls this method to re-evaluate to which modules each node belongs to.

```
abstract protected ModuleNode createModuleNode(Node node)
```
if the node belongs, it will ask you to create a `ModuleNode` for that node, which will be registered in the module. This is where you can store information about the node.

```
abstract public boolean updateNode(ModuleNode node);
```
it will be called periodically, here you can update information about the node.

## RPC interface ##

You probably want some RPC from the server to the node. You must define an interface for this RPC (like [Monitoreable](http://code.google.com/p/clusterfest/source/browse/trunk/src/com/flaptor/clusterfest/monitoring/node/Monitoreable.java)). Then you must implement it in the node and use it in the server.

Say `ServiceInterface` is the interface for RPC and `node` is the `Node` registered in clusterfest.

Node side, you must create an implementation for that service interface. If `ServiceImplementation` is an implementation of `ServiceInterface`:

```
NodeListener nodeListener = new NodeListener(port);
nodeListener.addModuleListener("ServiceInterfaceContext", new ServiceImplementation())
```

For using that service it you should add this code in the server:

```
ServiceInterface s = (ServiceInterface)XmlrpcClient.proxy("ServiceInterfaceContext", ServiceInterface.class, node.getXmlrpcClient());
```

## Interface ##

When your module is fully functional you can [add a web interface](addingWebInterface.md) to the clusterfest webapp.

[home](home.md)