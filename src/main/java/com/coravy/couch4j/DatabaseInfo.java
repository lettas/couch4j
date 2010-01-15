package com.coravy.couch4j;

import java.util.Date;

/**
 * @author Stefan Saasen
 */
public interface DatabaseInfo {
    String getName();

    int getDocCount();

    int getDocDelCount();

    int getUpdateSeq();

    int getPurgeSeq();

    boolean isCompactRunning();

    int getDiskSize();

    Date getInstanceStartTime();

    int getDiskFormatVersion();
}
