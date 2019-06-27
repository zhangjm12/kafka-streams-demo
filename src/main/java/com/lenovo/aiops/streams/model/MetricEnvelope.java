package com.lenovo.aiops.streams.model;

import java.util.Map;

/**
 * Created by gwen on 1/22/17.
 */
public class MetricEnvelope {

    Metric metric;
    Map<String, Object> meta;
    long creation_time;

    protected MetricEnvelope() {
    }


    public MetricEnvelope(Metric metric) {
        this.metric = metric;
        this.creation_time = System.currentTimeMillis()/1000;
    }

    public MetricEnvelope(Metric metric, Map<String, Object> meta) {
        this(metric);
        this.meta = meta;
    }

    public Metric getMetric() {
        return metric;
    }

    @Override
    public String toString() {
        return "MetricEnvelope{" +
                "metric=" + metric +
                ", meta=" + meta +
                ", creation_time=" + creation_time +
                '}';
    }
}
