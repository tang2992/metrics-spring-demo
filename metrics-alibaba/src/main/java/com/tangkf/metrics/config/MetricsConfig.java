package com.tangkf.metrics.config;

import com.alibaba.metrics.AliMetricManager;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.NOPMetricManager;
import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;
import com.alibaba.metrics.reporter.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class MetricsConfig  {

	private MetricRegistry registry;

	@Bean
	public MetricsCollectPeriodConfig metricsCollectPeriodConfig() {
		return new MetricsCollectPeriodConfig();
	}
	/*@Bean
	public MetricRegistry getMetricRegistry() {
		
		if (this.registry == null) {
			registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("test");
			Slf4jReporter.forRegistry(registry).build().start(30, TimeUnit.SECONDS);

			// register JVM metrics
//			registry.registerAll(new GarbageCollectorMetricSet());
//			registry.registerAll(new MemoryUsageGaugeSet());
//			registry.registerAll(new ThreadStatesGaugeSet());

		}
		return registry;
	}*/
}
