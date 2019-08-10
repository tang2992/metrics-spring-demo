package com.tangkf.metrics.threadpool;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class ThreadPoolMetricsGaugeSet implements MetricSet {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final Map<String, Metric> metrics;

    public ThreadPoolMetricsGaugeSet(ThreadPoolExecutor threadPoolExecutor) {
        if (threadPoolExecutor == null) {
            throw new RuntimeException("illegal thread pool executor,must not be null");
        }
        this.threadPoolExecutor = threadPoolExecutor;
        metrics = new HashMap<>(5);
    }



    /**
     * A map of metric names to metrics.
     *
     * @return the metrics
     */
    @Override
    public Map<String, Metric> getMetrics() {
        log.info("largestPoolSizeMe: {}", threadPoolExecutor.getLargestPoolSize());
        if (threadPoolExecutor == null) {
            return metrics;
        }

        metrics.put("active-size", new Gauge<Integer>(){
            /**
             * Returns the metric's current value.
             *
             * @return the metric's current value
             */
            @Override
            public Integer getValue() {
                return threadPoolExecutor.getActiveCount();
            }
        });

        metrics.put("queue-size", new Gauge<Integer>(){
            /**
             * Returns the metric's current value.
             *
             * @return the metric's current value
             */
            @Override
            public Integer getValue() {
                return threadPoolExecutor.getQueue().size();
            }
        });

        metrics.put("core-pool-size", new Gauge<Integer>(){
            /**
             * Returns the metric's current value.
             *
             * @return the metric's current value
             */
            @Override
            public Integer getValue() {
                return threadPoolExecutor.getCorePoolSize();
            }
        });

        metrics.put("max-pool-size", new Gauge<Integer>(){
            /**
             * Returns the metric's current value.
             *
             * @return the metric's current value
             */
            @Override
            public Integer getValue() {
                return threadPoolExecutor.getMaximumPoolSize();
            }
        });

        metrics.put("pool-size", new Gauge<Integer>(){
            /**
             * Returns the metric's current value.
             *
             * @return the metric's current value
             */
            @Override
            public Integer getValue() {
                return threadPoolExecutor.getPoolSize();
            }
        });


        return metrics;
    }


}
