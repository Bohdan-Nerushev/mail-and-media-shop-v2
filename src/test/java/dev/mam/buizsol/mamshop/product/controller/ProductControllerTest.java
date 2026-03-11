// package dev.mam.buizsol.mamshop.product.controller;
//
// import dev.mam.buizsol.mamshop.customer.model.Brand;
// import dev.mam.buizsol.mamshop.product.dto.ProductResponseDTO;
// import dev.mam.buizsol.mamshop.product.model.Product;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
// import static org.mockito.Mockito.*;
//
// import java.math.BigDecimal;
// import java.util.List;
// import dev.mam.buizsol.mamshop.product.exception.ProductNotFoundException;
// import dev.mam.buizsol.mamshop.product.mapper.ProductMapper;
// import dev.mam.buizsol.mamshop.shop.service.ShopService;
//
// @DisplayName(value = "ProductController Tests")
// @WebMvcTest(controllers = ProductController.class)
// public class ProductControllerTest {
//
//        @Autowired
//        private MockMvc mockMvc;
//
//        @MockitoBean
//        private ShopService shopService;
//
//        @MockitoBean
//        private ProductMapper productMapper;
//
//        @DisplayName(value = "Positve: Given a valid brand, when the client requests products, then all products for
// that brand are returned successfully")
//        @Test
//        void testLoadAllProductsForBrand_Success() throws Exception {
//                final Brand brand = Brand.MAIL_COM;
//
//                final Product firstProduct = ProductTestFactory.createPremiumProduct(
//                                "first",
//                                Brand.MAIL_COM,
//                                new BigDecimal("100.0"));
//
//                final Product secondProduct = ProductTestFactory.createStandardProduct(
//                                "second",
//                                Brand.MAIL_COM,
//                                new BigDecimal("120.0"));
//
//                final List<Product> products = ProductTestFactory.createProductList(firstProduct, secondProduct);
//
//                final ProductResponseDTO firstDto = ProductTestFactory.createDtoFromProduct(
//                                firstProduct.getId(),
//                                firstProduct.getName(),
//                                firstProduct.getBrand(),
//                                firstProduct.getSetupFee(),
//                                firstProduct.getMonthlyFee(),
//                                firstProduct.getStorageSize()
//                                                .orElse(0L));
//
//                final ProductResponseDTO secondDto = ProductTestFactory.createDtoFromProduct(
//                                secondProduct.getId(),
//                                secondProduct.getName(),
//                                secondProduct.getBrand(),
//                                secondProduct.getSetupFee(),
//                                secondProduct.getMonthlyFee(),
//                                secondProduct.getStorageSize()
//                                                .orElse(0L));
//
//                when(shopService.loadAllProductsForBrand(brand)).thenReturn(products);
//                when(productMapper.toProductResponseDTO(firstProduct)).thenReturn(firstDto);
//                when(productMapper.toProductResponseDTO(secondProduct)).thenReturn(secondDto);
//
//                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
//                                .param("brand", brand.name()))
//                                .andExpect(MockMvcResultMatchers.status().isOk())
//                                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
//                                                .value(2))
//                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name")
//                                                .value("first"))
//                                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name")
//                                                .value("second"));
//
//                verify(shopService, times(1)).loadAllProductsForBrand(brand);
//                verify(productMapper, times(1)).toProductResponseDTO(firstProduct);
//                verify(productMapper, times(1)).toProductResponseDTO(secondProduct);
//        }
//
//        @Test
//        @DisplayName(value = "Negative: Given a non-existing brand, when the client requests products, then a 404
// error is returned")
//        void testLoadAllProductsForBrand_BrandNotFound() throws Exception {
//                final Brand brand = Brand.MAIL_COM;
//                final String errorMessage = "Brand not found";
//
//                when(shopService.loadAllProductsForBrand(brand))
//                                .thenThrow(new ProductNotFoundException(errorMessage));
//
//                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
//                                .param("brand", brand.name()))
//                                .andExpect(MockMvcResultMatchers.status()
//                                                .isNotFound())
//                                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
//                                                .value(errorMessage));
//
//                verify(shopService, times(1)).loadAllProductsForBrand(brand);
//                verifyNoInteractions(productMapper);
//        }
//
//        @Test
//        @DisplayName(value = "Negative: Given a null brand, when the client requests products, then a 400 Bad Request
// error is returned")
//        void testLoadAllProductsForBrand_NullBrand() throws Exception {
//                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
//                                .andExpect(MockMvcResultMatchers.status()
//                                                .isBadRequest())
//                                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
//                                                .value("MISSING_PARAMETER"));
//
//                verifyNoInteractions(shopService);
//                verifyNoInteractions(productMapper);
//        }
//
// }
