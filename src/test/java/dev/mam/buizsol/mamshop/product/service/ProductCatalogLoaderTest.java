package dev.mam.buizsol.mamshop.product.service;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.model.PartnerProduct;
import dev.mam.buizsol.mamshop.product.model.PremiumMailProduct;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.product.model.StandardMailProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCatalogLoader Tests")
class ProductCatalogLoaderTest {

    @Mock
    private ProductService productService;

    @Test
    @DisplayName("Should successfully load products from valid CSV")
    void shouldLoadValidProducts() {
        ProductCatalogLoader.load(productService, "/valid_products.csv");

        final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(3)).createProduct(captor.capture());

        final List<Product> createdProducts = captor.getAllValues();

        assertThat(createdProducts).hasSize(3);

        assertThat(createdProducts.get(0)).isInstanceOf(StandardMailProduct.class);
        assertThat(createdProducts.get(0).getName()).isEqualTo("Standard Mail");
        assertThat(createdProducts.get(0).getBrand()).isEqualTo(Brand.GMX);
        assertThat(createdProducts.get(0).getMonthlyFee()).isEqualByComparingTo("0.50");

        assertThat(createdProducts.get(1)).isInstanceOf(PremiumMailProduct.class);
        assertThat(createdProducts.get(1).getName()).isEqualTo("Premium Mail");
        assertThat(createdProducts.get(1).getBrand()).isEqualTo(Brand.WEB_DE);
        assertThat(createdProducts.get(1).getMonthlyFee()).isEqualByComparingTo("1.50");

        assertThat(createdProducts.get(2)).isInstanceOf(PartnerProduct.class);
        assertThat(createdProducts.get(2).getName()).isEqualTo("Cloud Storage");
        assertThat(createdProducts.get(2).getBrand()).isEqualTo(Brand.MAIL_COM);
        assertThat(createdProducts.get(2).getMonthlyFee()).isEqualByComparingTo("2.00");
        assertThat(createdProducts.get(2).getSetupFee()).isEqualByComparingTo("10.00");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when CSV file is missing")
    void shouldThrowExceptionWhenFileNotFound() {
        assertThatThrownBy(() -> ProductCatalogLoader.load(productService, "/non_existent.csv"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not found in resources");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when CSV line is malformed")
    void shouldThrowExceptionWhenLineIsMalformed() {
        assertThatThrownBy(() -> ProductCatalogLoader.load(productService, "/invalid_format.csv"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid CSV line format");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when Partner product lacks setup fee")
    void shouldThrowExceptionWhenPartnerLacksSetupFee() {
        assertThatThrownBy(() -> ProductCatalogLoader.load(productService, "/missing_setup_fee.csv"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Partner product requires setup fee");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unknown product type is encountered")
    void shouldThrowExceptionWhenUnknownProductType() {
        assertThatThrownBy(() -> ProductCatalogLoader.load(productService, "/unknown_type.csv"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown product type: UNKNOWN");
    }

    @Test
    @DisplayName("Should skip blank or empty lines in CSV")
    void shouldSkipBlankLines() {
        ProductCatalogLoader.load(productService, "/empty_lines.csv");

        verify(productService, times(2)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should throw UncheckedIOException when reader close fails")
    void shouldThrowUncheckedIOExceptionOnCloseError() throws java.io.IOException {
        final InputStream mockInputStream = mock(InputStream.class);

        when(mockInputStream.read(any(byte[].class), any(int.class), any(int.class))).thenReturn(-1);
        org.mockito.Mockito.doThrow(new java.io.IOException("Close error")).when(mockInputStream).close();

        assertThatThrownBy(() -> ProductCatalogLoader.load(productService, mockInputStream))
                .isInstanceOf(java.io.UncheckedIOException.class)
                .hasMessageContaining("Failed to read product catalog from stream");
    }
}
