package com.lenovo.aiops.streams.serde;

import com.lenovo.aiops.streams.model.SumRateAggregate;

public class SumRateAggregateSerde extends WrapperSerde<SumRateAggregate> {
    public SumRateAggregateSerde() {
        super(new JsonSerializer<SumRateAggregate>(), new JsonDeserializer<SumRateAggregate>(SumRateAggregate.class));
    }
}