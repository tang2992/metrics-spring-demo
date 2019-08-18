/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.tangkf.metrics.threadpool;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.reporter.Slf4jReporter;
import com.alibaba.metrics.threadpool.ThreadPoolMetricsGaugeSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ThreadPoolMetricsGuageSetTest {
    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @Test
    public void testCollectThreadPoolMetrics() throws InterruptedException {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        MetricName name = MetricName.build("threadpool3");

        MetricManager.register("test", name,
                new ThreadPoolMetricsGaugeSet(10, TimeUnit.MILLISECONDS, executor));

        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("test");

        Thread.sleep(20);
        for (int i = 0; i < 100; i++) {
            System.out.println(executor.toString());
            Runnable t = new Runnable() {

                @Override
                public void run() {
                    // do thread work
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            };
            if (i == 10) {
                MetricName active = name.resolve("active");
                System.out.println(active);
                System.out.println(registry.getGauges().get(active).getValue());
                System.out.println(registry.getGauges().get(name.resolve("queued")).getValue());
                Assert.assertTrue(
                        (Long) registry.getGauges().get(active).getValue() > 0L);
                Assert.assertTrue(
                        (Long) registry.getGauges().get(name.resolve("queued")).getValue() >= 0L);
            }
            executor.submit(t);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(100L, registry.getGauges().get(name.resolve("completed")).getValue());
        Assert.assertEquals(3L, registry.getGauges().get(name.resolve("pool")).getValue());


    }

    @Test
    public void testCollectThreadPoolMetrics2() throws InterruptedException {
        ThreadPoolExecutor executor = asyncServiceExecutor.getThreadPoolExecutor();

        MetricName name = MetricName.build("threadpool2");

        MetricManager.register("test", name,
                new ThreadPoolMetricsGaugeSet(10, TimeUnit.MILLISECONDS, executor));

        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("test");
        Slf4jReporter.forRegistry(registry).build().start(20, TimeUnit.MILLISECONDS);
        Thread.sleep(20);
        for (int i = 0; i < 100; i++) {
            Runnable t = new Runnable() {

                @Override
                public void run() {
                    // do thread work
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            };
//            if (i == 10) {
//                System.out.println(registry.getGauges().get(name.resolve("active")).getValue());
//                System.out.println(registry.getGauges().get(name.resolve("queued")).getValue());
//                System.out.println(executor.toString());
//                Assert.assertTrue(
//                        (Long) registry.getGauges().get(name.resolve("active")).getValue() > 0L);
//                Assert.assertTrue(
//                        (Long) registry.getGauges().get(name.resolve("queued")).getValue() >= 0L);
//            }
            executor.submit(t);
            Thread.sleep(20);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(100L, registry.getGauges().get(name.resolve("completed")).getValue());
        Assert.assertEquals(3L, registry.getGauges().get(name.resolve("pool")).getValue());


    }

    @Test
    public void testCollectThreadPoolMetrics3() throws InterruptedException {
        ThreadPoolExecutor executor = asyncServiceExecutor.getThreadPoolExecutor();

        MetricName name = MetricName.build("threadpool4");

        MetricManager.register("test", name,
                new ThreadPoolMetricsGaugeSet(10, TimeUnit.MILLISECONDS, executor));

        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("test");
        Slf4jReporter.forRegistry(registry).build().start(20, TimeUnit.MILLISECONDS);
        Thread.sleep(20);
        for (int i = 0; i < 100; i++) {
            Runnable t = new Runnable() {

                @Override
                public void run() {
                    // do thread work
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }
            };
//            if (i == 10) {
//                System.out.println(registry.getGauges().get(name.resolve("active")).getValue());
//                System.out.println(registry.getGauges().get(name.resolve("queued")).getValue());
//                System.out.println(executor.toString());
//                Assert.assertTrue(
//                        (Long) registry.getGauges().get(name.resolve("active")).getValue() > 0L);
//                Assert.assertTrue(
//                        (Long) registry.getGauges().get(name.resolve("queued")).getValue() >= 0L);
//            }
//            executor.submit(t);
            asyncServiceExecutor.submit(t);
            Thread.sleep(20);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // do nothing
        }
        Assert.assertEquals(100L, registry.getGauges().get(name.resolve("completed")).getValue());
        Assert.assertEquals(3L, registry.getGauges().get(name.resolve("pool")).getValue());


    }
}
