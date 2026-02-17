package dev.mam.buizsol.mamshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import dev.mam.buizsol.mamshop.billing.service.BillingConfig;
import dev.mam.buizsol.mamshop.shop.service.ShopConfig;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@ComponentScan("dev.mam.buizsol.mamshop")
@Import({ BillingConfig.class, ShopConfig.class })
public class AppConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

//    @Bean
//    public static MethodValidationPostProcessor validationPostProcessor() {
//        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
//        processor.setAdaptConstraintViolations(true);
//        return processor;
//    }
}
