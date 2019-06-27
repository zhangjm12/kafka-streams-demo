package com.lenovo.aiops.streams.extractor;

import com.lenovo.aiops.streams.model.MetricEnvelope;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class MetricEnvelopeTimestampExtractor implements TimestampExtractor {
		@Override
		public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
			MetricEnvelope metricEnvelope = (MetricEnvelope) record.value();
			return metricEnvelope.getMetric().getTimestamp();
		}
}