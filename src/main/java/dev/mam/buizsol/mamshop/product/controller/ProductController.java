package dev.mam.buizsol.mamshop.product.controller;

import dev.mam.buizsol.mamshop.config.ErrorResponse;
import dev.mam.buizsol.mamshop.customer.model.Brand;
import dev.mam.buizsol.mamshop.product.dto.ProductResponseDTO;
import dev.mam.buizsol.mamshop.product.mapper.ProductMapper;
import dev.mam.buizsol.mamshop.product.model.Product;
import dev.mam.buizsol.mamshop.shop.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Product", description = "Product API")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ShopService shopService;
    private final ProductMapper productMapper;

    public ProductController(final ShopService shopService, final ProductMapper productMapper) {
        this.shopService = shopService;
        this.productMapper = productMapper;
    }

    @Operation(summary = "Load all products for a brand", description = "Returns all products for the specified brand.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products loaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public List<ProductResponseDTO> loadAllProductsForBrand(@RequestParam @NotNull Brand brand) {
        log.debug("Loading all products for brand: {}", brand);

        List<Product> productList = shopService.loadAllProductsForBrand(brand);
        log.debug("Product list: {}", productList);

        List<ProductResponseDTO> productResponseDTOList = productList.stream()
                .map(productMapper::toProductResponseDTO)
                .toList();
        log.debug("Product response DTO list: {}", productResponseDTOList);
        log.info("Products loaded successfully: {}", productResponseDTOList);
        return productResponseDTOList;
    }
}
