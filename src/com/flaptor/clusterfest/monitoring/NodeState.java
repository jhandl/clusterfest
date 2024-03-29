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

package com.flaptor.clusterfest.monitoring;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The state of a node in the monitoring module
 *
 * @author Martin Massera
 */
public class NodeState implements Serializable{

    private static final long serialVersionUID = 1L;    
    
    public static final NodeChecker.Result UNCHECKED_RESULT = new NodeChecker.Result(NodeChecker.Sanity.UNKNOWN, Arrays.asList(new String[]{"Node not checked"})); 
    public static final NodeChecker.Result UNREACHABLE_RESULT = new NodeChecker.Result(NodeChecker.Sanity.UNREACHABLE, Arrays.asList(new String[]{"Node unreachable"})); 
    
    private NodeChecker.Result sanity;
    private Map<String, Object> properties;
    private SystemProperties systemProperties;
    private long timestamp;
    private long stateId;

    public static NodeState createUncheckedState() {
        NodeState state = new NodeState(null, null);
        state.sanity = UNCHECKED_RESULT; 
        return state;
    }

    public static NodeState createUnreachableState() {
        NodeState state = new NodeState(null, null);
        state.sanity = UNREACHABLE_RESULT; 
        return state;
    }
    private static String getStack(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.getBuffer().toString();
    }
    public static NodeState createNodeErrorState(Throwable t) {
        NodeState state = new NodeState(null, null);
        state.sanity = new NodeChecker.Result(NodeChecker.Sanity.ERROR, Arrays.asList(new String[]{"NODE throwing exception", t.getMessage(), getStack(t)}));
        return state;
    }

    public static NodeState createClusterManagerErrorState(Throwable t) {
        NodeState state = new NodeState(null, null);
        state.sanity = new NodeChecker.Result(NodeChecker.Sanity.ERROR, Arrays.asList(new String[]{"CLUSTER MANAGER throwing exception", t.getMessage(), getStack(t)}));
        return state;
    }
        
    public NodeState(Map<String, Object> properties, SystemProperties systemProperties) {
        // should be updated later by a NodeChecker
        this.sanity = UNCHECKED_RESULT; 
        
        this.properties = properties;
        this.systemProperties = systemProperties;
        this.timestamp = System.currentTimeMillis();
    }
    
    public NodeChecker.Result getSanity() {
        return sanity;
    }
    public Map<String, Object> getProperties() {
        return properties;
    }
    public SystemProperties getSystemProperties() {
        return systemProperties;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void updateSanity(NodeChecker checker, MonitorNodeDescriptor node) {
        if (checker != null) {
        	this.sanity = checker.checkNode(node, this);
        } else {
            this.sanity = UNCHECKED_RESULT;
        }
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }
}