[![](http://opensource.flaptor.com/clusterfest/images/logo.png)](http://opensource.flaptor.com/clusterfest)


# Internals of the ClusterManager #

To be able to add new functionality, you have to understand the ClusterManager. At all times the ClusterManager has a representation of the cluster in its memory. This is a list of **node descriptors**,each holding the information of its corresponding node:

![http://clusterfest.googlecode.com/files/nodeDescriptors.png](http://clusterfest.googlecode.com/files/nodeDescriptors.png)

These node descriptors know the host and port where the corresponding node listener is listening, what type of node it is, and other information. They also know if that node is reachable or not. Suppose that Node 1 is not responding, the node descriptor 1 will be marked as **unreachable**:

![http://clusterfest.googlecode.com/files/nodeUnreachable.png](http://clusterfest.googlecode.com/files/nodeUnreachable.png)

The cluster manager maintains a representation of the cluster and knows when nodes are fallen, but that alone is not very useful. Functionality in clusterfest is added through **modules**,

When it starts, the cluster manager reads from a **clustering.properties** config file what modules it has to activate.

![http://clusterfest.googlecode.com/files/modules.png](http://clusterfest.googlecode.com/files/modules.png)

Modules can access the node descriptor list of the cluster manager and all of it properties. However, some modules will not be interested in all the nodes, but only some of them. So modules will maintain another list of nodes: the ones that are of interest to the module. This is a list of **module node descriptors**.

In the following example, the module is interested in 2 of the nodes, so the module node descriptor list will have 2 elements, pointing to 2 node descriptors, which in turn point to 2 of your application nodes.

![http://clusterfest.googlecode.com/files/moduleNodeDescriptors.png](http://clusterfest.googlecode.com/files/moduleNodeDescriptors.png)

If the module wants to perform some actions on the application nodes, it has to communicate with the actual node. This communication is done via xml-rpc, sending RPC requests to the  node listener in the node.

![http://clusterfest.googlecode.com/files/moduleCommunications.png](http://clusterfest.googlecode.com/files/moduleCommunications.png)

For this communication to be meaningful the node listener has to understand the RPC requests. By default, the node listener understands the cluster manager but not the modules. That's why we need to add **module listeners** to the node listener. These listeners will understand the RPC requests from the module and perform actions over the nodes.

When you create the node listener object in your application code in the nodes, you must also add the module listeners for the modules you will be using.

![http://clusterfest.googlecode.com/files/moduleListener.png](http://clusterfest.googlecode.com/files/moduleListener.png)

## Summary ##

  * The cluster manager is the base of clusterfest: it controls the node list and the modules.
  * Modules add functionality. You can use existing modules or create your own
  * For everything to run you have to add code to your application and configure the cluster manager

[home](home.md)