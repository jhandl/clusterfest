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

package com.flaptor.clustering.controlling.nodes;

import com.flaptor.clustering.controlling.controller.ControllerNodeState;

/**
 * interface for controlling nodes through rpc
 *
 * @author martinmassera
 */
public interface Controllable {

	/**
	 * pinging method for determining if the node is controllable
	 * @return true
	 */
	public boolean ping() throws Exception;
	
	/**
	 * @return the state of the node
	 */
	public ControllerNodeState getState() throws Exception;
	
	/**
	 * pause the service
	 */
	public void pause() throws Exception;

	/**
	 * resume the service
	 */
	public void resume() throws Exception;
	
	/**
	 * stop and exit the process gently
	 */
	public void stop() throws Exception;
}