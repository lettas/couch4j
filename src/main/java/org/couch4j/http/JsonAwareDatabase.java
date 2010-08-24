package org.couch4j.http;

import net.sf.json.JsonConfig;

import org.couch4j.api.Database;

interface JsonAwareDatabase extends Database {
    JsonConfig getConfig();
}
