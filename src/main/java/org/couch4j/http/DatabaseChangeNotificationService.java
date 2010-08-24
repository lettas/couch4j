/*
 * The MIT License
 *
 * Copyright (c) 2009, 2010 Stefan Saasen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.couch4j.http;

import static org.couch4j.util.CollectionUtils.map;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.couch4j.api.Database;
import org.couch4j.api.Database.ChangeEvent;
import org.couch4j.api.Database.ChangeListener;
import org.couch4j.annotations.Immutable;
import org.couch4j.exceptions.Couch4JException;

/**
 * @author Stefan Saasen
 */
final class DatabaseChangeNotificationService {

    private final static Logger logger = Logger.getLogger(DatabaseChangeNotificationService.class.getName());

    private ExecutorService executor;
    private volatile boolean receivingChangeNotifications;
    private final CopyOnWriteArrayList<ChangeListener> listener = new CopyOnWriteArrayList<ChangeListener>();

    private final HttpClient client;
    private final UrlBuilder urlResolver;
    private final Database database;

    DatabaseChangeNotificationService(final HttpClient c, final UrlBuilder urlResolver, Database database) {
        // HttpClientParams params = new HttpClientParams();
        // params.setConnectionManagerClass(org.apache.commons.httpclient.MultiThreadedHttpConnectionManager.class);
        this.client = c;
        this.urlResolver = urlResolver;
        this.database = database;
    }

    void addChangeListener(ChangeListener l) {
        receiveChangeNotifications();
        listener.add(l);
    }

    private void dispatchEvent(ChangeEvent e) {
        for (ChangeListener l : listener) {
            l.onChange(e);
        }
    }

    private void receiveChangeNotifications() {
        if (receivingChangeNotifications) {
            return;
        }
        logger.info("[" + Thread.currentThread().getName() + "] Start receiveChangeNotifications()...");

        receivingChangeNotifications = true;
        executor = Executors.newSingleThreadExecutor();
        Runnable r = new Runnable() {
            private void streamChanges(int updateSeq) {
                if (!receivingChangeNotifications) {
                    return;
                }
                // Do we need the "heartbeat" param?
                HttpGet method = new HttpGet(urlResolver.urlForPath("_changes", map("feed", "continuous", "style",
                        "all_docs", "since", String.valueOf(updateSeq), "heartbeat", "5000")));

                HttpResponse response = null;
                HttpEntity entity = null;
                try {
                    // int statusCode = client.executeMethod(method);
                    response = client.execute(method);
                    entity = response.getEntity();
                    // Read the response body.
                    Reader in = new InputStreamReader(entity.getContent(), EntityUtils.getContentCharSet(entity));

                    Scanner s = new Scanner(in).useDelimiter("\n");
                    String line;
                    while (s.hasNext() && null != (line = s.next())) {
                        // dispatch change event
                        if (line.length() > 1 && JSONUtils.mayBeJSON(line)) {
                            JSONObject json = JSONObject.fromObject(line);
                            if (json.has("seq")) {
                                if (logger.isLoggable(Level.FINE)) {
                                    logger.fine("Dispatch new change event: " + line);
                                }
                                dispatchEvent(new DatabaseChangeEvent(json));
                            } else if (json.has("last_seq")) {
                                if (logger.isLoggable(Level.FINE)) {
                                    logger.fine("CouchDB server closed _changes connection. Reconnecting...");
                                }
                                streamChanges(json.getInt("last_seq"));
                            }
                        }
                    }
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("[" + Thread.currentThread().getName() + "] Stop receiving changes...");
                    }
                } catch (IOException e) {
                    throw new Couch4JException(e);
                } finally {
                    if (null != entity) {
                        try {
                            entity.consumeContent();
                        } catch (IOException e) {
                            // swallow
                        }
                    }
                }
            }

            public void run() {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("[" + Thread.currentThread().getName() + "] Start receiving changes... ");
                }
                // Start with current udpate seq
                int updateSeq = database.getDatabaseInfo().getUpdateSeq();
                streamChanges(updateSeq);
            }
        };
        executor.submit(r);
    }

    void removeChangeListener(ChangeListener l) {
        listener.remove(l);
        if (listener.isEmpty()) {
            // unsubscribe
            receivingChangeNotifications = false;
            if (null != executor) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Shutdown executor service...");
                }
                executor.shutdown();
            }
        }
    }

    @Immutable
    private static class DatabaseChangeEvent implements ChangeEvent {

        private final String id;
        private final String seq;
        private final List<String> changeRevs;
        private final boolean deleted;

        @SuppressWarnings("unchecked")
        DatabaseChangeEvent(JSONObject json) {
            this.id = json.has("id") ? json.getString("id") : null;
            this.seq = json.has("seq") ? json.getString("seq") : null;
            this.deleted = json.has("deleted") ? json.getBoolean("deleted") : false;
            changeRevs = new ArrayList<String>();

            if (json.containsKey("changes")) {
                for (Iterator<JSONObject> iterator = json.getJSONArray("changes").iterator(); iterator.hasNext();) {
                    JSONObject rev = iterator.next();
                    changeRevs.add(rev.getString("rev"));
                }
            }
        }

        public List<String> changeRevs() {
            return Collections.unmodifiableList(changeRevs);
        }

        public String getId() {
            return id;
        }

        public String getSeq() {
            return seq;
        }

        public boolean isDeleted() {
            return deleted;
        }

        @Override
        public String toString() {
            return "DatabaseChangeEvent [changeRevs=" + changeRevs + ", deleted=" + deleted + ", id=" + id + ", seq="
                    + seq + "]";
        }

    }

}
