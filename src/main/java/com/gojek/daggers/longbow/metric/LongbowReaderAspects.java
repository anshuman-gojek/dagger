package com.gojek.daggers.longbow.metric;

import com.gojek.daggers.utils.stats.AspectType;
import com.gojek.daggers.utils.stats.Aspects;

import static com.gojek.daggers.utils.stats.AspectType.Histogram;
import static com.gojek.daggers.utils.stats.AspectType.Metric;

public enum LongbowReaderAspects implements Aspects {
    TIMEOUTS_ON_READER("timeoutsOnReader", Metric),
    CLOSE_CONNECTION_ON_READER("closeConnectionOnReader", Metric),
    SUCCESS_ON_READ_DOCUMENT("successOnReadDocument", Metric),
    SUCCESS_ON_READ_DOCUMENT_RESPONSE_TIME("successOnReadDocumentResponseTime", Histogram),
    DOCUMENTS_READ_PER_SCAN("documentsReadPerScan", Histogram),
    FAILED_ON_READ_DOCUMENT("failedOnReadDocument", Metric),
    FAILED_ON_READ_DOCUMENT_RESPONSE_TIME("failedOnReadDocumentResponseTime", Histogram),
    FAILED_TO_READ_LAST_RECORD("failedToReadLastRecord", Metric);

    private String value;
    private AspectType aspectType;

    LongbowReaderAspects(String value, AspectType aspectType) {
        this.value = value;
        this.aspectType = aspectType;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public AspectType getAspectType() {
        return aspectType;
    }
}
