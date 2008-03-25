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



/**
 * Basic implementation of a node descriptor for modules. Maintains a reference to the original NodeDescriptor
 * @author Martin Massera
 */
public abstract class ModuleNodeDescriptor {
    
    private NodeDescriptor nodeDescriptor;

    protected ModuleNodeDescriptor(NodeDescriptor nodeDescriptor) {
        this.nodeDescriptor = nodeDescriptor;
    }

    public NodeDescriptor getNodeDescriptor() {
        return nodeDescriptor;
    }
    
}