/*
 * Created on Mar 20, 2012
 */
package com.law.network.service;

import com.law.network.domain.HostMachine;

/**
 * Services for host machines.
 * 
 * @author Lloyd
 */
public interface HostService {
	
	/**
	 * Get information about the local host machine.
	 * 
	 * <p> The returned object will contain null values if an exception is
	 * thrown while retrieving the information.
	 * 
	 * @return A host machine object.
	 */
	HostMachine getLocalHost();
	
}
