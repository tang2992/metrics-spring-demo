package com.tangkf.metrics.config;

import com.codahale.metrics.MetricRegistry;
import com.tangkf.metrics.threadpool.ThreadPoolMetricsGaugeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Slf4j
@Configuration
@EnableAsync
public class ExecutorConfig {
    @Resource
    private MetricRegistry metricRegistry;

    @Bean
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(5);
        //配置最大线程数
        executor.setMaxPoolSize(5);
        //配置队列大小
        executor.setQueueCapacity(99999);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-service-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor
                .CallerRunsPolicy());
        //执行初始化
        executor.initialize();

        // 注册到度量
        metricRegistry.registerAll("asyncServiceExecutor", new ThreadPoolMetricsGaugeSet(executor.getThreadPoolExecutor()));
        return executor;
    }
}
