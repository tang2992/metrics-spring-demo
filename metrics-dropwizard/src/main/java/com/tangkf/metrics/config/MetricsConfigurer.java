package com.tangkf.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategies;
import com.codahale.metrics.httpclient.InstrumentedHttpClients;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.codahale.metrics.servlets.AdminServlet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import com.tangkf.metrics.RestProvider;
import com.tangkf.metrics.reporter.OpenFalcon;
import com.tangkf.metrics.reporter.OpenFalconReporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableMetrics
public class MetricsConfigurer extends MetricsConfigurerAdapter {

	private MetricRegistry registry;
	private HealthCheckRegistry healthCheckRegistry;


//	@Bean
//	public ServletRegistrationBean servletRegistrationBean(MetricRegistry metricRegistry) {
//		return new ServletRegistrationBean(new MetricsServlet(metricRegistry), "/monitor/metrics");
//	}
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		AdminServlet adminServlet = new AdminServlet();
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(adminServlet, "/admin/*");
		return servletRegistrationBean;
	}
	
	@Override
	public MetricRegistry getMetricRegistry() {
		
		if (this.registry == null) {
			registry = new MetricRegistry();
		
			// register JVM metrics
//			registry.registerAll(new GarbageCollectorMetricSet());
//			registry.registerAll(new MemoryUsageGaugeSet());
//			registry.registerAll(new ThreadStatesGaugeSet());

		}
		
		return registry;
	}

	@Override
	public HealthCheckRegistry getHealthCheckRegistry() {
		if (healthCheckRegistry == null) {
			healthCheckRegistry = new HealthCheckRegistry();
		}
		return healthCheckRegistry;
	}
	
	@Override
	public void configureReporters(MetricRegistry metricRegistry) {
		OpenFalcon openFalcon = new OpenFalcon.Builder("http://47.106.137.38:1988").create();
		OpenFalconReporter.forRegistry(metricRegistry).build(openFalcon).start(15, TimeUnit.SECONDS);
		Slf4jReporter.forRegistry(metricRegistry).build().start(15, TimeUnit.SECONDS);
	}
}
