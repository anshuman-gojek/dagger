package com.gojek.daggers.postProcessors.external.http;

import com.gojek.daggers.metrics.telemetry.TelemetrySubscriber;
import com.gojek.daggers.postProcessors.common.ColumnNameManager;
import com.gojek.daggers.postProcessors.external.common.StreamDecorator;
import com.gojek.de.stencil.StencilClient;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.concurrent.TimeUnit;

public class HttpStreamDecorator implements StreamDecorator {
    private final ColumnNameManager columnNameManager;
    private TelemetrySubscriber telemetrySubscriber;
    private boolean telemetryEnabled;
    private long shutDownPeriod;
    private HttpSourceConfig httpSourceConfig;
    private StencilClient stencilClient;

    public HttpStreamDecorator(HttpSourceConfig httpSourceConfig, StencilClient stencilClient, ColumnNameManager columnNameManager,
                               TelemetrySubscriber telemetrySubscriber, boolean telemetryEnabled, long shutDownPeriod) {
        this.httpSourceConfig = httpSourceConfig;
        this.stencilClient = stencilClient;
        this.columnNameManager = columnNameManager;
        this.telemetrySubscriber = telemetrySubscriber;
        this.telemetryEnabled = telemetryEnabled;
        this.shutDownPeriod = shutDownPeriod;
    }

    @Override
    public Boolean canDecorate() {
        return httpSourceConfig != null;
    }

    @Override
    public DataStream<Row> decorate(DataStream<Row> inputStream) {
        HttpAsyncConnector httpAsyncConnector = new HttpAsyncConnector(httpSourceConfig, stencilClient, columnNameManager, telemetryEnabled, shutDownPeriod);
        httpAsyncConnector.notifySubscriber(telemetrySubscriber);
        return AsyncDataStream.orderedWait(inputStream, httpAsyncConnector, httpSourceConfig.getStreamTimeout(), TimeUnit.MILLISECONDS, httpSourceConfig.getCapacity());
    }
}
