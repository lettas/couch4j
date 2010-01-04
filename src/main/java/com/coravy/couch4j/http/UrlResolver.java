package com.coravy.couch4j.http;

import java.util.Map;

/**
 * 
 * 
 * @author Stefan Saasen
 */
interface UrlResolver {

    String baseUrl();

    String urlForPath(String path);

    String urlForPath(String path, Map<String, String> params);

}
