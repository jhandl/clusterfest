[![](http://opensource.flaptor.com/clusterfest/images/logo.png)](http://opensource.flaptor.com/clusterfest)

# Adding a Web Interface #

Most modules will have a web interface. To keep it closely integrated to the general Clusterfest web app, the [WebModule](http://code.google.com/p/clusterfest/source/browse/trunk/src/com/flaptor/clusterfest/WebModule.java) API is provided. You can see how this was implemented in the [Monitor module](http://code.google.com/p/clusterfest/source/browse/trunk/src/com/flaptor/clusterfest/monitoring/MonitorModule.java).

There are two ways of adding HTML code to the main page:

  * for the whole module:
```
String getModuleHTML();
```
  * for each node in the node list
```
String getNodeHTML(Node node, int nodeNum);
```

You can then register actions for the module with this method:
```
List<String> getActions();
```
If you return a list containing `"TheAction"` and there is a request of `index.jsp?action=TheAction` the following method will be called in your module
```
String action(String action,  HttpServletRequest request);
```

There are also two ways of adding new pages to the webapp:
  * adding JSPs to the web-clustering folder (recommended)
  * adding handlers to the webserver. For this, the following method will be called when registering the webmodule:
```
void setup(WebServer server);
```

## Pages ##

Pages are rendered through velocity. If you want to add more pages and want to keep the style and coherence with the rest of the clustering webapp, you should include this at the top of your velocity files

```
#set($pageTitle = "your page title")
#parse("include.top.vm")
```

and this at the bottom

```
#parse("include.bottom.vm")
```

Here is an example of [monitor node page](http://code.google.com/p/clusterfest/source/browse/trunk/websrc/web-clusterfest/monitorNode.vm).

[home](home.md)