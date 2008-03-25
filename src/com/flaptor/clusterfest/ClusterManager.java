/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/

package com.flaptor.clusterfest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.flaptor.util.ClassUtil;
import com.flaptor.util.Config;
import com.flaptor.util.Pair;

/**
 * Manages a cluster. Contains the representation of a cluster as a list of nodes. 
 * Here you can register nodes and all the different modules of the framework (monitoring, control, etc) 
 * will get access to them. Also contains the list of modules that access the nodes.  
 *
 * @author Martin Massera
 */
public class ClusterManager {
	
    private static final Logger logger = Logger.getLogger(com.flaptor.util.Execute.whoAmI());
	
    private static class InitializiationOnDemandHolder {
        private static ClusterManager instance = new ClusterManager();    
    }
    
    private Map<String, Module> modules = new HashMap<String, Module>();
    private List<WebModule> webModules = new ArrayList<WebModule>();
    private Map<String, WebModule> moduleActionMap = new HashMap<String, WebModule>();
    private Map<String, WebModule> moduleSelectNodeActionMap = new HashMap<String, WebModule>();
    
    private ClusterManager() {
    	//after initializing everything, register all nodes
    	Config config = Config.getConfig("clustering.properties");
    	String[] hosts = config.getStringArray("clustering.nodes");
    	String[] moduleDefs = config.getStringArray("clustering.modules");
        
    	for (String moduleDef : moduleDefs) {
    		String [] defs = moduleDef.split(":");
    		Module module = (Module)ClassUtil.instance(defs[1]); 
    		modules.put(defs[0], module);
    		if (module instanceof WebModule) addWebModule((WebModule)module);
    	}
    	
    	for (int i = 0; i < hosts.length; i++) {
            String[] host = hosts[i].split(":");
            String hostName = host[0];
            int port = Integer.parseInt(host[1].trim());
            String installDir = (host.length > 2) ? host[2] : null;
            registerNode(hostName, port, installDir);
        }
        
		new Timer().scheduleAtFixedRate(new TimerTask(){
			public void run() {
				updateNodes();
			}
		}, 0, config.getInt("clustering.checkNodesInterval"));
    }
    
    
	private void addWebModule(WebModule wm) {
        for (String action : wm.getActions()) {
            moduleActionMap.put(action, wm);
        }
        for (Pair<String,String> action : wm.getSelectedNodesActions()) {
            moduleSelectNodeActionMap.put(action.first(), wm);
        }
		webModules.add(wm);
	}
	
    /**
     * @return the singleton instance of Cluster
     */
    public static ClusterManager getInstance() {
        return InitializiationOnDemandHolder.instance;
    }

	private List<NodeDescriptor> nodes = new ArrayList<NodeDescriptor>();

	/**
	 * persists the node list to the config file on disk
	 * @throws IOException 
	 */
	public void persistNodeList() throws IOException {
        String nodeList = "";
        for (NodeDescriptor node : nodes) {
            if (nodeList.length() > 0) nodeList += ",\\\n\t";
            nodeList += node.getHost() +":"+node.getPort()+":"+node.getInstallDir();
        }
        Config config = Config.getConfig("clustering.properties");
        config.set("clustering.nodes", nodeList);
        config.saveToDisk();
    }

	/**
	 * Registers a node in Clusterfest and notifies it to all its modules
	 * @param host the host where the node is
	 * @param port the port where that node is listening for clusterfest 
	 * @param installDir the directory where it is installed in that host (not the baseDir, should be something like baseDir/searcher)
	 * @return the registered node
	 */
	public NodeDescriptor registerNode(String host, int port, String installDir) {
		synchronized (nodes) {
			NodeDescriptor node = new NodeDescriptor(host, port, installDir);
	        nodes.add(node);
	       	updateAllInfo(node);
			return node;
		}
	}

	/**
	 * removes a node from the cluster and all the modules of the clustering framework
	 */
	public void unregisterNode(NodeDescriptor node) {
		synchronized (nodes) {
			nodes.remove(node);
			for (Module module: modules.values()) {
				module.nodeUnregistered(node);
			}
		}
	}

	/**
	 * updates all info of all nodes
	 */
	public void updateAllInfoAllNodes() {
		synchronized (nodes) {
			ExecutorService threadPool = Executors.newFixedThreadPool(nodes.size());
			for(final NodeDescriptor node: nodes) {
				threadPool.submit(new Runnable() {
					public void run() {
						updateAllInfo(node);
					}
				});
			}
			try {
			    threadPool.shutdown();
			    if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) logger.error("nodes took too long to update");
			} catch (InterruptedException e) {logger.error("unexpected interruption while waiting", e);}
		}
	}

	/**
	 * updates the info of the node related to the cluster 
	 * and registers in the modules it belongs if it hasnt already been registered 
	 * @param node
	 * @return true if the state was updated correctly
	 */
	public boolean updateAllInfo(NodeDescriptor node) {
		boolean noErrors = true;
		try {
			node.updateInfo();
		} catch (NodeUnreachableException e) {
			logger.warn("tried to update info for unreachable node at " + node.getHost() + ":" + node.getPort() + " - " +  e);
			noErrors = false;
		}
		for (Module module: modules.values()) {
			try {
			    module.notifyNode(node);
			} catch (NodeUnreachableException e) {
				logger.warn("tried to update info for unreachable node at " + node.getHost() + ":" + node.getPort(), e);
				noErrors = false;
			}
		}
		return noErrors;
	}

    public List<String> getNodeTypes() {
        List<String> nodeTypes = new ArrayList<String>();
        for (NodeDescriptor node: nodes) {
            String type = node.getType(); 
            if (type != null && !nodeTypes.contains(type)) nodeTypes.add(type);
        }
        return nodeTypes;
    }

    public List<NodeDescriptor> getNodeForType(String type) {
        List<NodeDescriptor> nodes = new ArrayList<NodeDescriptor>();
        for (NodeDescriptor node: nodes) {
            String ntype = node.getType(); 
            if (type.equals(ntype)) nodes.add(node);
        }
        return nodes;
    }
    
    
	/**
	 * @return the list of registered nodes
	 */
	public List<NodeDescriptor> getNodes() {
		return nodes;
	}
	
	public Module getModule(String moduleName) {
		return modules.get(moduleName);
	}
	
	public List<WebModule> getWebModules() {
		return webModules;
	}
	
    public WebModule getModuleForAction(String action) {
        return moduleActionMap.get(action);
    }
    public WebModule getModuleForSelectNodeAction(String action) {
        return moduleSelectNodeActionMap.get(action);
    }
	
	/**
	 * check to see if nodes that weren't alive came to live
	 */
	private void updateNodes() {
		synchronized (nodes) {
			for (NodeDescriptor node: nodes) {
				updateAllInfo(node);
			}
		}
	}	
}
