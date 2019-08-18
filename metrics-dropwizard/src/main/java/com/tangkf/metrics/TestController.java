package com.tangkf.metrics;

import com.codahale.metrics.annotation.*;
import com.tangkf.metrics.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Controller
@EnableAutoConfiguration
public class TestController {

	@Autowired
	private RestProvider restProvider;

	@Autowired
	private AsyncService asyncService;


	@ResponseMetered(name = "testResponseMetered")
	@ExceptionMetered(name = "testExceptionMetered")
	@Counted(name = "testCounted")
	@Metered(name = "testMetered")
	@Timed
	@ResponseBody
	@RequestMapping("/test/api")
	public String process(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		
		log.info("Processing Request");
		
		// get first request
		HttpResponse resp =
			restProvider.get(new URI("http://api.openweathermap.org/data/2.5/weather?q=Bristol,CT"));
		return resp.getEntity().toString();
	}

	@Gauge(name = "api2")
    @ResponseBody
	@RequestMapping("/test/api2")
	public Integer process2() {
        return new Random().nextInt(5);
	}

	@Timed
	@ResponseBody
	@RequestMapping("/test/api3")
	public String submit(){
		log.info("start submit");

		//调用service层的任务
		asyncService.executeAsync();

		log.info("end submit");

		return "success";
	}
}
