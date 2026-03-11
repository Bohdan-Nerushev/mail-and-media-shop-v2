package dev.mam.buizsol.mamshop.shop.service.controller;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.dto.ProductResponseDTO;
import dev.mam.buizsol.mamshop.product.model.PremiumMailProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class ShopTestFactory {

    private ShopTestFactory() {}

    public static PremiumMailProduct createPremiumProduct(String name, Brand brand, BigDecimal price) {

        return new PremiumMailProduct(name, brand, price);
    }

    public static StandardMailProduct createStandardProduct(String name, Brand brand, BigDecimal price) {

        return new StandardMailProduct(name, brand, price);
    }

    public static List<Product> createProductList(Product... products) {
        return List.of(products);
    }

    public static ProductResponseDTO createDtoFromProduct(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getSetupFee(),
                product.getMonthlyFee(),
                product.getStorageSize().orElse(0L));
    }

    public static ProductResponseDTO createDtoFromProduct(
            UUID id, String name, Brand brand, BigDecimal setupFee, BigDecimal monthlyFee, Long storageSize) {
        return new ProductResponseDTO(id, name, brand, setupFee, monthlyFee, storageSize);
    }
}
