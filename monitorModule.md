[![](http://opensource.flaptor.com/clusterfest/images/logo.png)](http://opensource.flaptor.com/clusterfest)

# Monitor Module #

The Monitor module helps easily monitor nodes and detect problems in those nodes. It works in two phases:

  * the server requests properties to the nodes (properties, logs and systemproperties)
  * the server runs a checker to determine the state of the node

This state can be visually seen as a dashboard in the clusterfest webapp. Also you can check these properties and past states for each node.

## Checkers ##

Checkers are associated with node types in the `clustering.properties` file. For type `NODE_TYPE`

```
clustering.monitor.checker.NODE_TYPE=somePackage.NodeTypeCheckerClassName
```

`somePackage.NodeTypeCheckerClassName` must be an implementation of the `NodeChecker` interface and have an empty constructor.

[modules](modules.md) [home](home.md)