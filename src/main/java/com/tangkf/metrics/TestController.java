package com.tangkf.metrics;

import com.codahale.metrics.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Random;

@Slf4j
@Controller
@EnableAutoConfiguration
public class TestController {
		

	@Inject private RestProvider restProvider;

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
        return new Random().nextInt();
	}
}
