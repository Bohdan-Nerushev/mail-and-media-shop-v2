package dev.mam.buizsol.mamshop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import dev.mam.buizsol.mamshop.billing.service.BillingConfig;
import dev.mam.buizsol.mamshop.shop.service.ShopConfig;

@Configuration
@ComponentScan("dev.mam.buizsol.mamshop")
@Import({ BillingConfig.class, ShopConfig.class })
public class AppConfig {
}
