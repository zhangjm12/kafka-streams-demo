package com.lenovo.aiops.streams.serde;

import com.lenovo.aiops.streams.model.MetricRateAggregate;

public class MetricRateAggregateSerde extends WrapperSerde<MetricRateAggregate> {
    public MetricRateAggregateSerde() {
        super(new JsonSerializer<MetricRateAggregate>(), new JsonDeserializer<MetricRateAggregate>(MetricRateAggregate.class));
    }
}