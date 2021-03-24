package io.segmentme.web.configuration.beanprocessor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@RequiredArgsConstructor
class WebClientBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private final static String HTTP_SERVICE_CONFIG_YML_PROP = "segmentme.application.web-client.configuration";

    private final Environment environment;

    private final GenericWebApplicationContext context;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
        Binder.get(environment)
                .bind(HTTP_SERVICE_CONFIG_YML_PROP, Bindable.mapOf(String.class, WebClientConfigurationProperties.class))
                .orElse(Collections.emptyMap())
                .entrySet()
                .stream()
                .filter(it -> !context.isBeanNameInUse(it.getKey()))
                .forEach(entry -> createBeanDefinition(entry.getKey(), entry.getValue(), beanDefinitionRegistry));
    }

    private void createBeanDefinition(String key, WebClientConfigurationProperties configProperty, BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinitionBuilder beanDefinition = BeanDefinitionBuilder
                .rootBeanDefinition(WebClient.class)
                .addConstructorArgValue(key)
                .addConstructorArgValue(configProperty)
                .setFactoryMethodOnBean("buildWebClientBean", StringUtils.uncapitalize(WebClientBeanFactory.class.getSimpleName()));

        beanDefinitionRegistry.registerBeanDefinition(key, beanDefinition.getBeanDefinition());
    }
}
