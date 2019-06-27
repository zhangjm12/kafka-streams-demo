package com.lenovo.aiops.streams.model;

/**
 * Created by gwen on 1/22/17.
 */
public class MetricRateAggregate {

    double firstValue;
    long firstTS;
    double lastValue;
    long lastTS;
    int count;

    public MetricRateAggregate() {
        count = 0;
        firstValue = 0;
        lastValue = 0;
        firstTS = 0;
        lastTS = 0;
    }

    @Override
    public String toString() {
        return "MetricAverageAggregate{" +
                "First=" + firstValue +
                ", Last=" + lastValue +
                "}";
    }

    public MetricRateAggregate add(double value, long ts) {

        if (count==0) {
            firstValue = value;
            firstTS = ts;
            count++;
        } else {
            lastValue = value;
            lastTS = ts;
            count++;
        }
        return this;
    }

    public double getRate() {
        if (lastTS > firstTS) {
            return 1000 * (lastValue - firstValue) / (lastTS - firstTS);
        } else {
            return 0;
        }
    }
}
