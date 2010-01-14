package com.coravy.couch4j.http;

import static com.coravy.core.collections.CollectionUtils.map;

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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import com.coravy.core.annotations.Immutable;
import com.coravy.couch4j.Database;
import com.coravy.couch4j.Database.ChangeEvent;
import com.coravy.couch4j.Database.ChangeListener;
import com.coravy.couch4j.exceptions.Couch4JException;

/**
 * @author Stefan Saasen
 */
final class DatabaseChangeNotificationService {

    private final static Logger logger = Logger.getLogger(DatabaseChangeNotificationService.class.getName());

    private ExecutorService executor;
    private volatile boolean receivingChangeNotifications;
    private final CopyOnWriteArrayList<ChangeListener> listener = new CopyOnWriteArrayList<ChangeListener>();

    private final HttpClient client;
    private final UrlResolver urlResolver;
    private final Database database;

    DatabaseChangeNotificationService(final HttpClient c, final UrlResolver urlResolver, Database database) {
        HttpClientParams params = new HttpClientParams();
        params.setConnectionManagerClass(org.apache.commons.httpclient.MultiThreadedHttpConnectionManager.class);
        this.client = new HttpClient(params);
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
                GetMethod method = new GetMethod(urlResolver.urlForPath("_changes", map("feed", "continuous", "style",
                        "all_docs", "since", String.valueOf(updateSeq), "heartbeat", "5000")));
                try {
                    // int statusCode = client.executeMethod(method);
                    client.executeMethod(method);
                    // Read the response body.
                    Reader in = new InputStreamReader(method.getResponseBodyAsStream(), method.getRequestCharSet());

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
                    method.releaseConnection();
                }
            }

            public void run() {
                int connectionsInPool = ((MultiThreadedHttpConnectionManager) client.getHttpConnectionManager())
                        .getConnectionsInPool();
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("[" + Thread.currentThread().getName()
                            + "] Start receiving changes... [connectionsInPool: " + connectionsInPool + "]");
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
