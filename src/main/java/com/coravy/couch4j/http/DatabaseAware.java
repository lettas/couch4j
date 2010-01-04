package com.coravy.couch4j.http;

import com.coravy.couch4j.api.Database;

interface DatabaseAware<T> {
    Database<T> getDatabase();
}
