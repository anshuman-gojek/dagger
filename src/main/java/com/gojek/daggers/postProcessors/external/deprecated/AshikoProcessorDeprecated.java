package com.gojek.daggers.postProcessors.external.deprecated;

import com.gojek.daggers.core.StreamInfo;
import com.gojek.daggers.postProcessors.PostProcessorConfig;
import com.gojek.daggers.postProcessors.common.PostProcessor;
import com.gojek.daggers.postProcessors.external.common.StreamDecorator;
import com.gojek.de.stencil.StencilClient;
import com.google.gson.Gson;
import com.google.protobuf.Descriptors;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

import static com.gojek.daggers.utils.Constants.*;


public class AshikoProcessorDeprecated implements PostProcessor {

    private Configuration configuration;
    private StencilClient stencilClient;

    public AshikoProcessorDeprecated(Configuration configuration, StencilClient stencilClient) {
        this.configuration = configuration;
        this.stencilClient = stencilClient;
    }

    @Override
    public StreamInfo process(StreamInfo streamInfo) {
        String asyncConfigurationString = configuration.getString(ASYNC_IO_KEY, "");
        Map<String, Object> asyncConfig = new Gson().fromJson(asyncConfigurationString, Map.class);
        Descriptors.Descriptor outputDescriptor = this.outputDescriptor();
        int size = outputDescriptor.getFields().size();
        String[] columnNames = new String[size];
        DataStream<Row> resultStream = streamInfo.getDataStream();
        for (Descriptors.FieldDescriptor fieldDescriptor : outputDescriptor.getFields()) {
            String fieldName = fieldDescriptor.getName();
            if (!asyncConfig.containsKey(fieldName)) {
                continue;
            }
            Map<String, String> fieldConfiguration = ((Map<String, String>) asyncConfig.get(fieldName));
            int asyncIOCapacity = Integer.valueOf(fieldConfiguration.getOrDefault(ASYNC_IO_CAPACITY_KEY, ASYNC_IO_CAPACITY_DEFAULT));
            int fieldIndex = fieldDescriptor.getIndex();
            fieldConfiguration.put(FIELD_NAME_KEY, fieldName);
            StreamDecorator streamDecorator = AshikoStreamDecoratorFactory.getStreamDecorator(fieldConfiguration, fieldIndex, stencilClient, asyncIOCapacity, size);
            columnNames[fieldIndex] = fieldName;
            resultStream = streamDecorator.decorate(resultStream);
        }
        return new StreamInfo(resultStream, columnNames);
    }

    @Override
    public boolean canProcess(PostProcessorConfig postProcessorConfig) {
        return false;
    }

    // TODO: [PORTAL_MIGRATION] Remove this switch when migration to new portal is done
    private Descriptors.Descriptor outputDescriptor() {
        // [PORTAL_MIGRATION] Move conteont inside this block to process method
        if (configuration.getString(PORTAL_VERSION, "1").equals("2")) {
            String protoClassName = configuration.getString(OUTPUT_PROTO_MESSAGE, "");
            return stencilClient.get(protoClassName);
        }

        String outputProtoPrefix = configuration.getString(OUTPUT_PROTO_CLASS_PREFIX_KEY, "");
        String protoClassName = String.format("%sMessage", outputProtoPrefix);
        return stencilClient.get(protoClassName);
    }
}