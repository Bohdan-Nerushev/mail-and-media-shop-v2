package dev.mam.buizsol.mamshop.billing.service;

import dev.mam.buizsol.mamshop.contract.service.ContractService;
import dev.mam.buizsol.mamshop.customer.service.CustomerService;
import dev.mam.buizsol.mamshop.product.service.ProductService;
import jakarta.validation.constraints.NotNull;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingConfig {

    @Bean
    @NotNull
    public BillingService billingService(
            @NotNull final CustomerService customerService,
            @NotNull final ProductService productService,
            @NotNull final ContractService contractService) {
        return new BillingServiceImpl(
                customerService,
                productService,
                contractService);
    }
}
