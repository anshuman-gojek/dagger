package io.odpf.dagger.functions.udfs;

/**
 * Not taking care of metrics registration for aggregate functions for now.
 * Since function registration for aggregation functions can not be done inside the open method.
 *
 * ISSUE : https://issues.apache.org/jira/browse/FLINK-15040
 */
public enum Udf {
    END_OF_MONTH("EndOfMonth"),
    HISTOGRAM_BUCKET("HistogramBucket");

    public String getValue() {
        return value;
    }

    private String value;

    Udf(String value) {
        this.value = value;
    }
}
