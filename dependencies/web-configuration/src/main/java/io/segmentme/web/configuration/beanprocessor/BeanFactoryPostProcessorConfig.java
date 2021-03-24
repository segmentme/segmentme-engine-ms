package io.segmentme.web.configuration.beanprocessor;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Configuration
class BeanFactoryPostProcessorConfig {

    @Bean
    static BeanFactoryPostProcessor beanPostProcessor(final Environment environment, final GenericWebApplicationContext context) {
        return new WebClientBeanFactoryPostProcessor(environment, context);
    }
}