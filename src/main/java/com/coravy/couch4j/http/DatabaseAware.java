package com.coravy.couch4j.http;

import com.coravy.couch4j.Database;

interface DatabaseAware {
    Database getDatabase();
}
