package com.lenovo.aiops.streams.serde;

import com.lenovo.aiops.streams.model.Metric;
import org.apache.kafka.common.serialization.Serdes;

public class MetricSerde extends Serdes.WrapperSerde<Metric> {
    public MetricSerde() {
        super(new JsonSerializer<Metric>(), new JsonDeserializer<Metric>(Metric.class));
    }
}