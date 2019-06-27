package com.lenovo.aiops.streams.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwen on 1/22/17.
 */
public class SumRateAggregate {
    Map<String, MetricRateAggregate> m;

    public SumRateAggregate() {
        m = new HashMap<>();
    }

    @Override
    public String toString() {
        return "SumAverageAggregate{...}";
    }

    public SumRateAggregate add(String key, double value, long ts) {
        if (!m.containsKey(key))
            m.put(key, new MetricRateAggregate());
        m.get(key).add(value, ts);
        return this;
    }

    public double getRate() {
        double r = 0;
        for (String k: m.keySet()) {
            r += m.get(k).getRate();
        }
        return r;
    }
}
