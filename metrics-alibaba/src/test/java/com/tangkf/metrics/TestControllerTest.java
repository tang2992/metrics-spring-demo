package com.tangkf.metrics;

import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.reporter.Slf4jReporter;
import com.alibaba.metrics.threadpool.ThreadPoolMetricsGaugeSet;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@Slf4j
//@WebMvcTest
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestControllerTest {
    private MockMvc mockMvc; // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。

    @Autowired
    private WebApplicationContext wac; // 注入WebApplicationContext

    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

    @Before // 在测试开始前初始化工作
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void process() {
    }

    @Test
    public void process2() {
    }

    @Test
    public void submit() throws Exception {
        // 注册到度量
        MetricName name = MetricName.build("threadpool");
        MetricManager.register("test", name, new ThreadPoolMetricsGaugeSet(asyncServiceExecutor.getThreadPoolExecutor()));
        MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup("test");
        Slf4jReporter.forRegistry(registry).build().start(20, TimeUnit.SECONDS);

        while (true) {
            MvcResult result = mockMvc.perform(post("/test/api3"))
//                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk())// 模拟向testRest发送get请求
                    .andReturn();// 返回执行请求的结果
        }


    }
}