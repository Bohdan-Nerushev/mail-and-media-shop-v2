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

@Component
public class ProductCatalogLoader {

    private final ProductService productService;

    private final int MAX_CSV_PATH_LENGTH = 100;
    private static final int MAX_CSV_PATH_STATIC = 100;

    private final int MIN_COLUMNS_VALUE = 4;
    private static final int MIN_COLUMNS_VALUE_STATIC = 4;

    private final int MAX_COLUMNS_VALUE = 5;
    private static final int MAX_COLUMNS_VALUE_STATIC = 5;

    public ProductCatalogLoader(
            final ProductService productService) {
        this.productService = productService;
    }

    public void load(
            @NotNull @Size(max = MAX_CSV_PATH_LENGTH) final String csvPath) {
        if (csvPath == null || csvPath.length() > MAX_CSV_PATH_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("CSV path must not be null and must not exceed %d characters", MAX_CSV_PATH_LENGTH));
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
    private static Product parseProductFromCsvLine(@NotNull @Size(max = MAX_CSV_PATH_STATIC) final String line) {
        if (line == null || line.length() > MAX_CSV_PATH_STATIC) {
            throw new IllegalArgumentException(
                    String.format("CSV line must not be null and must not exceed %d characters", MAX_CSV_PATH_STATIC));
        }
        final String[] parts = line.split(",");
        if (parts.length < MIN_COLUMNS_VALUE_STATIC) {
            throw new IllegalArgumentException(
                    String.format("Invalid CSV line format (at least %d columns required): ", MIN_COLUMNS_VALUE_STATIC)
                            + line);
        }

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
        if (parts.length < MAX_COLUMNS_VALUE_STATIC) {
            throw new IllegalArgumentException("Partner product requires setup fee in CSV at line: " + line);
        }
        final BigDecimal setupFee = new BigDecimal(parts[4].trim());
        return new PartnerProduct(name, brand, setupFee, monthlyFee);
    }
}
