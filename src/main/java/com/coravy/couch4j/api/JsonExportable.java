package com.coravy.couch4j.api;

import net.sf.json.JSONObject;

public interface JsonExportable {
    String toJson();
    JSONObject toJSONObject();
}
