/*
 * Created on Mar 20, 2012
 */
package com.law.network.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.law.network.domain.HostMachine;

/**
 * Services for host machines.
 * 
 * @author Lloyd
 */
public class HostServiceImpl implements HostService {
	
	public HostMachine getLocalHost() {
		
		String hostName = null;
		String hostAddress = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
			hostAddress = addr.getHostAddress();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return new HostMachine(hostName, hostAddress);
	}
	
}
