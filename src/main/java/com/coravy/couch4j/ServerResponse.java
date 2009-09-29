package com.coravy.couch4j;

/**
 * @author Stefan Saasen
 */
public abstract class ServerResponse {
	public abstract String getId();

	public abstract String getRev();

	/**
	 * @return JSON string representation of the viewresult
	 */
	@Override
	public abstract String toString();
}
