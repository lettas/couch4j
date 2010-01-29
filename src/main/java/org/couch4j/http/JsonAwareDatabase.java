package org.couch4j.http;

import net.sf.json.JsonConfig;

import org.couch4j.Database;

interface JsonAwareDatabase extends Database {
    JsonConfig getConfig();
}
