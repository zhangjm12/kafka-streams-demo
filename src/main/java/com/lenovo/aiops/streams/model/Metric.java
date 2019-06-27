package com.lenovo.aiops.streams.model;

import java.util.Map;

/**
 * Created by gwen on 1/22/17.
 */
public class Metric {

    double timestamp;
    String name;
    Map<String, String> dimensions;
    double value;
    Map<String, String> value_meta;

    public Metric() {}

    public Metric(String name, Map<String, String> dimensions, double timestamp,
                  double value, Map<String, String> value_meta) {
        this.name = name;
        this.dimensions = dimensions;
        this.timestamp = timestamp;
        this.value = value;
        this.value_meta = value_meta;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "name='" + name + '\'' +
                ", dimensions=" + dimensions +
                ", timestamp=" + timestamp +
                ", value=" + value +
                ", value_meta=" + value_meta +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
    }

    public long getTimestamp() {
        return (long)timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Map<String, String> getValueMeta() {
        return value_meta;
    }

    public void setValueMeta(Map<String, String> valueMeta) {
        this.value_meta = value_meta;
    }
}
