package com.coravy.couch4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coravy.couch4j.exceptions.Couch4JException;

/**
 * @author Stefan Saasen
 */
@RunWith(Parameterized.class)
public abstract class Couch4jBase {

    protected final static Logger logger = LoggerFactory.getLogger(Couch4jBase.class);
    
    protected CouchDB server;

    public Couch4jBase(CouchDB server) {
        this.server = server;
    }

    @Parameterized.Parameters
    public static Collection<CouchDB[]> testDatabases() {
        Collection<CouchDB[]> toTest = Arrays.asList(new CouchDB[][] { { CouchDB.localServerInstance() },
                { new CouchDB("localhost", 59810) } });

        Collection<CouchDB[]> instancesRunning = new ArrayList<CouchDB[]>();
        for (CouchDB[] param : toTest) {
            CouchDB server = param[0];
            try {
                server.getDatabase("couch4j");
                instancesRunning.add(param);
            } catch (Couch4JException ce) {
                logger.warn("Ignoring {} - connection failed.", server);
            }
        }
        return instancesRunning;
    }

}
