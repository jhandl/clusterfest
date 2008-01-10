package com.flaptor.clustering.monitoring.monitor;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.flaptor.clustering.ClusterableServer;
import com.flaptor.clustering.Node;
import com.flaptor.clustering.NodeUnreachableException;
import com.flaptor.clustering.modules.ModuleNode;
import com.flaptor.clustering.modules.NodeContainerModule;
import com.flaptor.clustering.monitoring.nodes.Monitoreable;
import com.flaptor.util.ClassUtil;
import com.flaptor.util.Config;
import com.flaptor.util.Pair;
import com.flaptor.util.remote.XmlrpcClient;
import com.flaptor.util.remote.XmlrpcSerialization;

public class Monitor extends NodeContainerModule {
    public final static String MODULE_CONTEXT = "monitor";
	
	private static final Logger logger = Logger.getLogger(com.flaptor.util.Execute.whoAmI());

	public Monitor() {
		Config config = Config.getConfig("clustering.properties");
		new Timer().scheduleAtFixedRate(new TimerTask(){
			public void run() {
				updateNodes();
			}
		}, 0, config.getInt("clustering.monitor.refreshInterval"));
	}

	@Override
	protected ModuleNode createModuleNode(Node node) {
		MonitorNode mnode = new MonitorNode(node);
		try {
			mnode.setChecker(getCheckerForType(node.getType()));
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
		updateNode(mnode);
		return mnode;
	}

    private void updateNodes() {
    	//update states of all the monitored nodes
    	synchronized (nodes) {
	    	for (ModuleNode node : nodes) {
				updateNode((MonitorNode)node);
	    	}
    	}
    }
    
	@Override
	public boolean nodeBelongs(Node node) throws NodeUnreachableException {
		try {
			boolean ret = getMonitoreableProxy(node.getXmlrpcClient()).ping();
			return ret;
		} catch (NoSuchMethodException e) {
			return false;
		} catch (Exception e) {
			throw new NodeUnreachableException(e);
		}
	}

	public boolean updateNode(ModuleNode node) {
		try {
			((MonitorNode)node).updateState();
			return true;
		} catch (NodeUnreachableException e) {
			logger.warn(e);
			return false;
		}
	}

	public Pair<String, Long> retrieveLog(MonitorNode node, String logName) {
		if (!node.getLogs().containsKey(logName)) {
			try {
				node.updateLog(logName);
			} catch (NodeUnreachableException e) {
				logger.warn(e);
			}
		}
		return node.retrieveLog(logName);
	}

	public void setChecker(MonitorNode node, NodeChecker checker) {
		node.setChecker(checker);
	}

	
	public NodeChecker getChecker(String checkerClassName) {
		
		try {
			return (NodeChecker) ClassUtil.instance(checkerClassName);
		} catch (Throwable t) {
			logger.error(t);
			return null;
		}
	}

	public NodeChecker getCheckerForType(String type) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Config config = Config.getConfig("clustering.properties");
		try {
			String clazz = config.getString("clustering.monitor.checker."+type);
			return getChecker(clazz);
		}catch (IllegalStateException e) {
			logger.error(e);
			return null;
		}
    }
	/**
	 * adds a monitoreable implementation to a clusterable server
	 * @param clusterableServer
	 * @param m
	 */
	public static void addMonitorServer(ClusterableServer clusterableServer, Monitoreable m) {
		clusterableServer.addModuleServer(Monitor.class.getName(), XmlrpcSerialization.handler(m));
	}
	/**
	 * @param client
	 * @return a proxy for monitoreable xmlrpc calls
	 */
	public static Monitoreable getMonitoreableProxy(XmlrpcClient client) {
		return (Monitoreable)XmlrpcClient.proxy(Monitor.class.getName(), Monitoreable.class, client);
	}
}
