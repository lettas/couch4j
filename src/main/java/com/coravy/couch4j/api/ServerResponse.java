package com.coravy.couch4j.api;


/**
 * @author Stefan Saasen
 */
public interface ServerResponse extends JsonExportable {
	public abstract String getId();

	public abstract String getRev();
}
