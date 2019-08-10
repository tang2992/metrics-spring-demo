package com.tangkf.metrics.service;

import com.alibaba.metrics.annotation.EnableMeter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    @EnableMeter(group = "test", key="test.async.task.executor")
    @Override
    @Async("asyncServiceExecutor")
    public void executeAsync() {
        log.info("start executeAsync");
        try{
            Thread.sleep(1000);
        }catch(Exception e){
            e.printStackTrace();
        }
        log.info("end executeAsync");
    }
}