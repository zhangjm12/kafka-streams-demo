package com.lenovo.aiops.streams.serde;

import com.lenovo.aiops.streams.model.Metric;

public class MetricSerde extends WrapperSerde<Metric> {
    public MetricSerde() {
        super(new JsonSerializer<Metric>(), new JsonDeserializer<Metric>(Metric.class));
    }
}