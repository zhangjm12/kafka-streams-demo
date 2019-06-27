package com.lenovo.aiops.streams.serde;

import com.lenovo.aiops.streams.model.MetricEnvelope;
import org.apache.kafka.common.serialization.Serdes;

public class MetricEnvelopeSerde extends Serdes.WrapperSerde<MetricEnvelope> {
    public MetricEnvelopeSerde() {
        super(new JsonSerializer<MetricEnvelope>(), new JsonDeserializer<MetricEnvelope>(MetricEnvelope.class));
    }
}