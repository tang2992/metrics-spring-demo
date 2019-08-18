/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tangkf.metrics.reporter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.metrics.*;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.MetricManagerReporter;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * A reporter which publishes all MetricManager metrics values to a OpenTSDB server.
 *
 *
 */
public class OpenFalconMetricManagerReporter extends MetricManagerReporter {

    /** key-value 连接符*/
    public static final String TAG_CONNECTOR = "=";
    /** tag 连接符*/
    public static final String TAG_CONJUNCTION = ",";

    private final OpenFalcon openFalcon;
    private final Clock clock;
    private final String prefix;
    private final Map<String, String> globalTags;
    private final String endpoint;
    private final TimeUnit timestampPrecision;
    private final MetricsCollectPeriodConfig metricsReportPeriodConfig;

    /**
     * Returns a new {@link Builder} for {@link OpenFalconReporter}.
     *
     * @param metricManager
     *            the metricManager to report
     * @return a {@link Builder} instance for a {@link OpenFalconReporter}
     */
    public static Builder forMetricManager(IMetricManager metricManager) {
        return new Builder(metricManager);
    }

    /**
     * A builder for {@link OpenFalconReporter} instances. Defaults to not using a
     * prefix, using the default clock, converting rates to events/second,
     * converting durations to milliseconds, and not filtering metrics.
     */
    public static class Builder {
        private final IMetricManager metricManager;
        private Clock clock;
        private String prefix;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private MetricsCollectPeriodConfig metricsReportPeriodConfig;
        private Map<String, String> globalTags;
        private int batchSize;

        // 提交到服务器的时间戳的单位，只支持毫秒和秒，默认是秒
        private TimeUnit timestampPrecision = TimeUnit.SECONDS;

        private Builder(IMetricManager metricManager) {
            this.metricManager = metricManager;
            this.clock = Clock.defaultClock();
            this.prefix = null;
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.batchSize = OpenFalcon.DEFAULT_BATCH_SIZE_LIMIT;
            this.metricsReportPeriodConfig = new MetricsCollectPeriodConfig();
        }

        /**
         * Use the given {@link Clock} instance for the time.
         *
         * @param clock
         *            a {@link Clock} instance
         * @return {@code this}
         */
        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        /**
         * Prefix all metric names with the given string.
         *
         * @param prefix
         *            the prefix for all metric names
         * @return {@code this}
         */
        public Builder prefixedWith(String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit
         *            a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter
         *            a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         *
         * @param metricsReportPeriodConfig
         * @return
         */
        public Builder metricsReportPeriodConfig(MetricsCollectPeriodConfig metricsReportPeriodConfig) {
            this.metricsReportPeriodConfig = metricsReportPeriodConfig;
            return this;
        }

        /**
         * Append tags to all reported metrics
         *
         * @param globalTags
         * @return
         */
        public Builder withGlobalTags(Map<String, String> globalTags) {
            this.globalTags = globalTags;
            return this;
        }

        /**
         * specify number of metrics send in each request
         *
         * @param batchSize
         * @return
         */
        public Builder withBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder timestampPrecision(TimeUnit timestampPrecision) {
            if (TimeUnit.SECONDS.equals(timestampPrecision) || TimeUnit.MILLISECONDS.equals(timestampPrecision)) {
                this.timestampPrecision = timestampPrecision;
                return this;
            } else {
                throw new IllegalArgumentException(
                        "timestampPrecision must be TimeUnit.SECONDS or TimeUnit.MILLISECONDS!, do not support: "
                                + timestampPrecision);
            }
        }

        /**
         * Builds a {@link OpenFalconReporter} with the given properties, sending
         * metrics using the given
         * {@link OpenFalcon} client.
         *
         * @param openFalcon
         *            a {@link OpenFalcon} client
         * @return a {@link OpenFalconReporter}
         */
        public OpenFalconMetricManagerReporter build(OpenFalcon openFalcon, String endpoint) {
            openFalcon.setBatchSizeLimit(batchSize);
            if (globalTags == null) {
                globalTags = Collections.emptyMap();
            }
            return new OpenFalconMetricManagerReporter(metricManager, openFalcon, clock, prefix, rateUnit, timestampPrecision, durationUnit, filter, metricsReportPeriodConfig, globalTags, endpoint);
        }
    }

    private OpenFalconMetricManagerReporter(IMetricManager metricManager, OpenFalcon openFalcon, Clock clock, String prefix, TimeUnit rateUnit,
                                            TimeUnit durationUnit, TimeUnit timestampPrecision, MetricFilter filter, MetricsCollectPeriodConfig metricsReportPeriodConfig, Map<String, String> globalTags, String endpoint) {
        super(metricManager, "open-falcon-reporter", filter, metricsReportPeriodConfig, rateUnit, durationUnit);
        this.openFalcon = openFalcon;
        this.clock = clock;
        this.prefix = prefix;
        this.globalTags = globalTags;
        this.endpoint = endpoint;
        this.timestampPrecision = timestampPrecision;
        this.metricsReportPeriodConfig = metricsReportPeriodConfig;
    }

    @Override
    public void report(Map<MetricName, Gauge> gauges, Map<MetricName, Counter> counters,
                       Map<MetricName, Histogram> histograms, Map<MetricName, Meter> meters,
                       Map<MetricName, Timer> timers, Map<MetricName, Compass> compasses, Map<MetricName, FastCompass> fastCompasses, Map<MetricName, ClusterHistogram> clusterHistogrames) {

        long timestamp = clock.getTime();

        if (TimeUnit.MICROSECONDS.equals(timestampPrecision)) {
            timestamp = timestamp / 1000;
        }

        final Set<OpenFalconMetric> metrics = new HashSet<>();

        for (Entry<MetricName, Gauge> g : gauges.entrySet()) {
            if (g.getValue().getValue() instanceof Collection && ((Collection) g.getValue().getValue()).isEmpty()) {
                continue;
            }
            metrics.add(buildGauge(g.getKey(), g.getValue(), timestamp));
        }

        for (Entry<MetricName, Counter> entry : counters.entrySet()) {
            metrics.add(buildCounter(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Histogram> entry : histograms.entrySet()) {
            metrics.addAll(buildHistograms(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Meter> entry : meters.entrySet()) {
            metrics.addAll(buildMeters(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Timer> entry : timers.entrySet()) {
            metrics.addAll(buildTimers(entry.getKey(), entry.getValue(), timestamp));
        }

        for (Entry<MetricName, Compass> entry : compasses.entrySet()) {
            metrics.addAll(buildCompass(entry.getKey(), entry.getValue(), timestamp));
        }

        openFalcon.send(metrics);
    }

    private Set<OpenFalconMetric> buildTimers(MetricName name, Timer timer, long timestamp) {
        final MetricsCollector collector = MetricsCollector.createNew(metricsReportPeriodConfig, prefix(name.getKey()), name.getMetricLevel(),
                merge(globalTags, name.getTags()), timestamp, endpoint);
        final Snapshot snapshot = timer.getSnapshot();

        return collector.addMetric("count", timer.getCount())
                // convert rate
                .addMetric("m15", convertRate(timer.getFifteenMinuteRate()))
                .addMetric("m5", convertRate(timer.getFiveMinuteRate()))
                .addMetric("m1", convertRate(timer.getOneMinuteRate()))
                .addMetric("mean_rate", convertRate(timer.getMeanRate()))
                // convert duration
                .addMetric("max", convertDuration(snapshot.getMax()))
                .addMetric("min", convertDuration(snapshot.getMin()))
                .addMetric("mean", convertDuration(snapshot.getMean()))
                .addMetric("stddev", convertDuration(snapshot.getStdDev()))
                .addMetric("median", convertDuration(snapshot.getMedian()))
                .addMetric("p75", convertDuration(snapshot.get75thPercentile()))
                .addMetric("p95", convertDuration(snapshot.get95thPercentile()))
                .addMetric("p98", convertDuration(snapshot.get98thPercentile()))
                .addMetric("p99", convertDuration(snapshot.get99thPercentile()))
                .addMetric("p999", convertDuration(snapshot.get999thPercentile())).build();
    }

    private Set<OpenFalconMetric> buildCompass(MetricName name, Compass compass, long timestamp) {

        final MetricsCollector collector = MetricsCollector.createNew(metricsReportPeriodConfig, prefix(name.getKey()), name.getMetricLevel(),
                merge(globalTags, name.getTags()), timestamp, endpoint);

        // TODO add build compass logic

        return collector.build();
    }

    private Set<OpenFalconMetric> buildHistograms(MetricName name, Histogram histogram, long timestamp) {

        final MetricsCollector collector = MetricsCollector.createNew(metricsReportPeriodConfig, prefix(name.getKey()), name.getMetricLevel(),
                merge(globalTags, name.getTags()), timestamp, endpoint);
        final Snapshot snapshot = histogram.getSnapshot();

        return collector.addMetric("count", histogram.getCount()).addMetric("max", snapshot.getMax())
                .addMetric("min", snapshot.getMin()).addMetric("mean", snapshot.getMean())
                .addMetric("stddev", snapshot.getStdDev()).addMetric("median", snapshot.getMedian())
                .addMetric("p75", snapshot.get75thPercentile()).addMetric("p95", snapshot.get95thPercentile())
                .addMetric("p98", snapshot.get98thPercentile()).addMetric("p99", snapshot.get99thPercentile())
                .addMetric("p999", snapshot.get999thPercentile()).build();
    }

    private Set<OpenFalconMetric> buildMeters(MetricName name, Meter meter, long timestamp) {

        final MetricsCollector collector = MetricsCollector.createNew(metricsReportPeriodConfig, prefix(name.getKey()), name.getMetricLevel(),
                merge(globalTags, name.getTags()), timestamp, endpoint);

        return collector.addMetric("count", meter.getCount())
                // convert rate
                .addMetric("mean_rate", convertRate(meter.getMeanRate()))
                .addMetric("m1", convertRate(meter.getOneMinuteRate()))
                .addMetric("m5", convertRate(meter.getFiveMinuteRate()))
                .addMetric("m15", convertRate(meter.getFifteenMinuteRate())).build();
    }

    private OpenFalconMetric buildCounter(MetricName name, Counter counter, long timestamp) {
        return OpenFalconMetric.named(prefix(name.getKey(), "count")).withTimestamp(timestamp)
                .withValue(counter.getCount()).withTags(merge(globalTags, name.getTags())).build();
    }

    private OpenFalconMetric buildGauge(MetricName name, Gauge gauge, long timestamp) {

        return OpenFalconMetric.named(prefix(name.getKey(), "value")).withValue(gauge.getValue()).withTimestamp(timestamp)
                .withEndpoint(endpoint).withStep(metricsReportPeriodConfig.period(name.getMetricLevel()))
                .withTags(merge(globalTags, name.getTags())).build();
    }

    private String prefix(String... components) {
        return MetricRegistry.name(prefix, components).getKey();
    }

    private static String merge(Map<String, String>... maps) {
        int length;
        if (ArrayUtil.isEmpty(maps)) {
            length = 0;
        } else {
            length = maps.length;
        }

        List<String> tags = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Map<String, String> map = maps[i];
            for (Map.Entry<String, String> entry : map.entrySet()) {
                tags.add(entry.getKey() + TAG_CONNECTOR + entry.getValue());
            }
        }

        return CollUtil.join(tags, TAG_CONJUNCTION);
    }

    private static class MetricsCollector {
        private final String prefix;
        private final String tags;
        private final long timestamp;
        private final String endpoint;
        private final int step;
        private final Set<OpenFalconMetric> metrics = new HashSet<>();

        private MetricsCollector(MetricsCollectPeriodConfig metricsCollectPeriodConfig, String prefix, MetricLevel level, String tags, long timestamp, String endpoint) {
            this.prefix = prefix;
            this.tags = tags;
            this.timestamp = timestamp;
            this.endpoint = endpoint;
            this.step = metricsCollectPeriodConfig.period(level);
        }

        public static MetricsCollector createNew(MetricsCollectPeriodConfig metricsCollectPeriodConfig, String prefix, MetricLevel level, String tags, long timestamp, String endpoint) {
            return new MetricsCollector(metricsCollectPeriodConfig, prefix, level, tags, timestamp, endpoint);
        }

        public MetricsCollector addMetric(String metricName, Object value) {
            this.metrics.add(OpenFalconMetric.named(MetricRegistry.name(prefix, metricName).getKey())
                    .withEndpoint(endpoint).withStep(step)
                    .withTimestamp(timestamp).withValue(value).withTags(tags).build());
            return this;
        }

        public Set<OpenFalconMetric> build() {
            return metrics;
        }
    }
}
