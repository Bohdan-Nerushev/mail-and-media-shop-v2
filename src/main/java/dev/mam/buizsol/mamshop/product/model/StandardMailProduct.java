package dev.mam.buizsol.mamshop.product.model;

import dev.mam.buizsol.mamshop.customer.model.Brand;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "standard_mail_products")
@DiscriminatorValue("StandardMailProduct")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardMailProduct extends MailProduct {

    private static final BigDecimal FIXED_SETUP_FEE = new BigDecimal("4.99");
    private static final Long FIXED_STORAGE_SIZE = 4L;

    public StandardMailProduct(
            @NotNull final String name,
            @NotNull final Brand brand,
            @NotNull final BigDecimal monthlyFee) {
        super(
                UUID.randomUUID(),
                name,
                brand,
                FIXED_SETUP_FEE,
                monthlyFee,
                FIXED_STORAGE_SIZE);
    }

    @Override
    public StandardMailProduct withMonthlyFee(
            @NotNull final BigDecimal monthlyFee) {
        return this.toBuilder()
                .monthlyFee(monthlyFee)
                .build();
    }
}
