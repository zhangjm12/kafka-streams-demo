package com.lenovo.aiops.streams.serde;

import com.lenovo.aiops.streams.model.MetricRateAggregate;
import org.apache.kafka.common.serialization.Serdes;

public class MetricRateAggregateSerde extends Serdes.WrapperSerde<MetricRateAggregate> {
    public MetricRateAggregateSerde() {
        super(new JsonSerializer<MetricRateAggregate>(), new JsonDeserializer<MetricRateAggregate>(MetricRateAggregate.class));
    }
}