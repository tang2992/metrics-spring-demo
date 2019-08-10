package com.tangkf.metrics;

import com.tangkf.metrics.config.MetricsConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@Configuration
@WebListener
public class SpringBeanTool implements ApplicationContextAware, ServletContextListener {

    /**
     * 上下文对象实例
     */
    private ApplicationContext applicationContext;

    private ServletContext servletContext;

    @Autowired
    private MetricsConfigurer configurer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取servletContext
     *
     * @return
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(String name, Class<T> clazz) {
        Assert.hasText(name, "name为空");
        return getApplicationContext().getBean(name, clazz);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        servletContext.setAttribute("com.codahale.metrics.servlets.MetricsServlet.registry", configurer.getMetricRegistry());
		servletContext.setAttribute("com.codahale.metrics.servlets.HealthCheckServlet.registry", configurer.getHealthCheckRegistry());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}