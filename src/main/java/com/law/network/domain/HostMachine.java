/*
 * Created on Mar 18, 2012
 */
package com.law.network.domain;

public class HostMachine {
	
	private String hostName;
	private String hostAddress;
	
	public HostMachine() {
	}
	
	public HostMachine(String hostName, String hostAddress) {
		this.hostName = hostName;
		this.hostAddress = hostAddress;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public String getHostAddress() {
		return hostAddress;
	}
	
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	@Override
	public String toString() {
		return hostAddress + " (\"" + hostName + "\")";
	}
	
}
