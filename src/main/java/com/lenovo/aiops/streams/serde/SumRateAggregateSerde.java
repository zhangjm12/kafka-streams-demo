package com.lenovo.aiops.streams.serde;

import com.lenovo.aiops.streams.model.SumRateAggregate;
import org.apache.kafka.common.serialization.Serdes;

public class SumRateAggregateSerde extends Serdes.WrapperSerde<SumRateAggregate> {
    public SumRateAggregateSerde() {
        super(new JsonSerializer<SumRateAggregate>(), new JsonDeserializer<SumRateAggregate>(SumRateAggregate.class));
    }
}