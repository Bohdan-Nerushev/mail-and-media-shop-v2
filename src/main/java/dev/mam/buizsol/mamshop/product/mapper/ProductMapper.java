package dev.mam.buizsol.mamshop.product.mapper;

import dev.mam.buizsol.mamshop.product.dto.ProductResponseDTO;
import dev.mam.buizsol.mamshop.product.model.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponseDTO toProductResponseDTO (@NotNull Product product)  {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getSetupFee(),
                product.getMonthlyFee(),
                product.getStorageSize().orElse(null)
        );
    }
}
