package com.lenovo.aiops.streams;

import java.util.*;
import java.time.Duration;

import com.lenovo.aiops.streams.extractor.MetricEnvelopeTimestampExtractor;
import com.lenovo.aiops.streams.model.Metric;
import com.lenovo.aiops.streams.model.MetricEnvelope;
import com.lenovo.aiops.streams.model.MetricRateAggregate;
import com.lenovo.aiops.streams.model.SumRateAggregate;
import com.lenovo.aiops.streams.serde.MetricEnvelopeSerde;
import com.lenovo.aiops.streams.serde.MetricRateAggregateSerde;
import com.lenovo.aiops.streams.serde.SumRateAggregateSerde;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.common.utils.Bytes;

public class StreamTestProcessor {
		static final String inputTopic = "metrics";

		public static void main(final String[] args) {
			final String bootstrapServers = args.length > 0 ? args[0] : "10.121.9.164:31092";

			final Properties streamsConfiguration = new Properties();
			streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-test");
			streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "kafka-stream-test-client");
			streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

			streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());//
			streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, MetricEnvelopeSerde.class.getName());
			streamsConfiguration.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MetricEnvelopeTimestampExtractor.class.getName());
			streamsConfiguration.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getName());

			final StreamsBuilder builder = new StreamsBuilder();

			MetricEnvelopeSerde metricEnvelopeSerde = new MetricEnvelopeSerde();

			final KStream<String, MetricEnvelope> envelopeKStream = builder.stream(inputTopic,
					Consumed.with(Serdes.String(), metricEnvelopeSerde).withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

			Predicate<String, MetricEnvelope> isRequest =
					 	(k, v) -> {
							Metric m = v.getMetric();
							if (m.getName().equals("nginx_requests_total")) {
								Map<String, String> d = m.getDimensions();
								if (d.containsKey("server_zone"))
									return true;
							}
							return false;
						};

			Predicate<String, MetricEnvelope> isResponse =
					(k, v) -> {
						Metric m = v.getMetric();
						if (m.getName().equals("nginx_responses_total")) {
							Map<String, String> d = m.getDimensions();
							if (d.containsKey("server_zone") && d.containsKey("status_code") && d.get("status_code").trim().length()>0)
								return true;
						}
						return false;
					};

			int request = 0;
			int response = 1;

			final KStream<String, MetricEnvelope>[] streamByType =
					envelopeKStream.branch(isRequest, isResponse);

			final KStream<String, MetricEnvelope> rateRequestStream = streamByType[request]
					.groupBy((key,value) -> value.getMetric().getDimensions().get("server_zone"))
					.windowedBy(TimeWindows.of(Duration.ofSeconds(15)).advanceBy(Duration.ofSeconds(5)).grace(Duration.ofSeconds(5)))
					.aggregate(
							() -> new MetricRateAggregate(),
							(k, v, agg) -> {
								Metric m = v.getMetric();
								double mv =  m.getValue();
								long ts = m.getTimestamp();
								agg = agg.add(mv, ts);
								return agg;
							},
							Materialized.<String, MetricRateAggregate, WindowStore<Bytes, byte[]>>as("time-windowed-sum-rate-request-stream-store")
									.withKeySerde(Serdes.String()).withValueSerde(new MetricRateAggregateSerde()) /* serde for aggregate value */
					)
					.suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
					.toStream()
					.mapValues((k,v) -> {
						Metric m = new Metric();
						m.setName("nginx_requests_total_sec");
						m.setValue(v.getRate());
						m.setTimestamp(k.window().end());
						HashMap<String, String> d = new HashMap<>();
						d.put("server_zone", k.key());
						m.setDimensions(d);
						m.setValueMeta(null);

						HashMap<String, Object> meta = new HashMap<>();
						meta.put("tenantId", "5d4632a0ad404ff19c862fecd65d57ef");
						meta.put("region", "useast");
						MetricEnvelope env = new MetricEnvelope(m, meta);
						return env;
					})
					.selectKey((k,v) -> k.key());

			rateRequestStream.print(Printed.toSysOut());
			rateRequestStream.to(inputTopic, Produced.with(Serdes.String(), new MetricEnvelopeSerde()));

			final KStream<String, MetricEnvelope> rateResponseStream = streamByType[1]
				  .groupBy((key,value) -> value.getMetric().getDimensions().get("server_zone"))
				  .windowedBy(TimeWindows.of(Duration.ofSeconds(15)).advanceBy(Duration.ofSeconds(5)).grace(Duration.ofSeconds(5)))
				  .aggregate(
						  () -> new SumRateAggregate(),
						  (k, v, agg) -> {
							  Metric m = v.getMetric();
							  double mv =  m.getValue();
							  long ts = m.getTimestamp();
							  String key = v.getMetric().getDimensions().get("status_code");
							  agg = agg.add(key, mv, ts);
							  return agg;
						  },
						  Materialized.<String, SumRateAggregate, WindowStore<Bytes, byte[]>>as("time-windowed-sum-rate-aggregated-stream-store")
								  .withKeySerde(Serdes.String()).withValueSerde(new SumRateAggregateSerde()) /* serde for aggregate value */
				  )
				  .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
				  .toStream()
				  .mapValues((k,v) -> {
						Metric m = new Metric();
						m.setName("nginx_responses_total_sec");
						m.setValue(v.getRate());
						m.setTimestamp(k.window().end());
						HashMap<String, String> d = new HashMap<>();
						d.put("server_zone", k.key());
						m.setDimensions(d);
						m.setValueMeta(null);

						HashMap<String, Object> meta = new HashMap<>();
						meta.put("tenantId", "5d4632a0ad404ff19c862fecd65d57ef");
						meta.put("region", "useast");
						MetricEnvelope env = new MetricEnvelope(m, meta);
						return env;
				  	})
				   .selectKey((k,v) -> k.key());

			rateResponseStream.print(Printed.toSysOut());
			rateResponseStream.to(inputTopic, Produced.with(Serdes.String(), new MetricEnvelopeSerde()));

			final KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfiguration);

			  kafkaStreams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
				  throwable.printStackTrace();
			  });

			kafkaStreams.cleanUp();
			kafkaStreams.start();

			Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
		}

}
