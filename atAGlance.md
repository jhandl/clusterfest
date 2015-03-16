[![](http://opensource.flaptor.com/clusterfest/images/logo.png)](http://opensource.flaptor.com/clusterfest)

# Clusterfest at a glance #

Clusterfest helps you manage a multimachine installation in a centralized manner. These are the basic concepts to understand clusterfest.

## Nodes ##
Say your application is a distributed application, so you will have several computers run your application code. We will call these computers **nodes**:

![http://clusterfest.googlecode.com/files/nodes.png](http://clusterfest.googlecode.com/files/nodes.png)

## Cluster Manager ##

Clusterfest provides a **cluster manager**, which communicates to all the nodes in your application:

![http://clusterfest.googlecode.com/files/nodesWithClusterManager.png](http://clusterfest.googlecode.com/files/nodesWithClusterManager.png)

## Node Listener ##

For this to work, nodes must add a little piece of code called **node listener**, that listens for connections from clusterfest. These connections are done via **XMLRPC**

![http://clusterfest.googlecode.com/files/nodeListener.png](http://clusterfest.googlecode.com/files/nodeListener.png)

## Webapp ##

So how do you use clusterfest? Clusterfest provides a **webapp** to interact with the cluster manager via web. This is done by executing the **http clusterfest server**:

![http://clusterfest.googlecode.com/files/httpClusterfestServer.png](http://clusterfest.googlecode.com/files/httpClusterfestServer.png)

[Continue with the ClusterManager internals](clusterManagerInternals.md)

[home](home.md)