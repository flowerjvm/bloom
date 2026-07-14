package io.github.flowerjvm.bloom.spring;

import io.github.flowerjvm.bloom.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minimal Spring configuration for Bloom.
 */
@Configuration
public class BloomConfiguration {

    @Bean
    public static BloomEventBusRegistrar bloomEventBusRegistrar() {
        return new BloomEventBusRegistrar();
    }

    @Bean
    public static BloomBeanPostProcessor bloomBeanPostProcessor(EventBus eventBus) {
        return new BloomBeanPostProcessor(eventBus);
    }
}
