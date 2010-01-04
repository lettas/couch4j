package com.coravy.couch4j;

import net.sf.json.JSONObject;

public interface JsonExportable {
    String toJson();
    JSONObject toJSONObject();
}
