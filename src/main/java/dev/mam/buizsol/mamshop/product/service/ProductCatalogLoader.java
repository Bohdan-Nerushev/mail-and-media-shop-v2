package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.model.PartnerProduct;
import dev.mam.buizsol.mamshop.product.model.PremiumMailProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.springframework.validation.annotation.Validated;

@Component
@Validated
public final class ProductCatalogLoader {

    private final ProductService productService;

    public ProductCatalogLoader(
            final ProductService productService) {
        this.productService = productService;
    }

    public void load(
            @NotNull @Size(max = 100) final String csvPath) {
        if (csvPath == null || csvPath.length() > 100) {
            throw new IllegalArgumentException("CSV path must not be null and must not exceed 100 characters");
        }
        final InputStream inputStream = ProductCatalogLoader.class.getResourceAsStream(csvPath);
        if (inputStream == null) {
            throw new IllegalStateException("Product catalog file '" + csvPath + "' not found in resources.");
        }
        load(productService, inputStream);
    }

    static void load(
            @NotNull final ProductService productService,
            @NotNull final InputStream inputStream) {
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.lines()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .forEach(line -> productService.createProduct(parseProductFromCsvLine(line)));
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read product catalog from stream", e);
        }
    }

    @NotNull
    private static Product parseProductFromCsvLine(@NotNull @Size(max = 100) final String line) {
        if (line == null || line.length() > 100) {
            throw new IllegalArgumentException("CSV line must not be null and must not exceed 100 characters");
        }
        final String[] parts = line.split(",");
        validateCsvStructure(parts, line);

        final String type = parts[0].trim().toUpperCase();
        final String name = parts[1].trim();
        final Brand brand = Brand.valueOf(parts[2].trim());
        final BigDecimal monthlyFee = new BigDecimal(parts[3].trim());

        return switch (type) {
            case "STANDARD", "PREMIUM" -> createMailProduct(type, name, brand, monthlyFee);
            case "PARTNER" -> createPartnerProduct(parts, name, brand, monthlyFee, line);
            default -> throw new IllegalArgumentException("Unknown product type: " + type);
        };
    }

    private static void validateCsvStructure(
            @NotNull final String[] parts,
            @NotNull final String line) {
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV line format (at least 4 columns required): " + line);
        }
    }

    @NotNull
    private static Product createMailProduct(
            @NotNull final String type,
            @NotNull final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal monthlyFee) {
        if ("STANDARD".equals(type)) {
            return new StandardMailProduct(name, brand, monthlyFee);
        }
        return new PremiumMailProduct(name, brand, monthlyFee);
    }

    @NotNull
    private static Product createPartnerProduct(
            @NotNull final String[] parts,
            @NotNull final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal monthlyFee,
            @NotNull final String line) {
        if (parts.length < 5) {
            throw new IllegalArgumentException("Partner product requires setup fee in CSV at line: " + line);
        }
        final BigDecimal setupFee = new BigDecimal(parts[4].trim());
        return new PartnerProduct(name, brand, setupFee, monthlyFee);
    }
}
