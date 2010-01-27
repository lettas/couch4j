package org.couch4j.http;

import org.couch4j.Database;

import net.sf.json.JsonConfig;

interface JsonAwareDatabase extends Database {
    JsonConfig getConfig();
}
