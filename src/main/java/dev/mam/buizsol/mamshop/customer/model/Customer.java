package dev.mam.buizsol.mamshop.customer.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id", nullable = false)
    @NotNull
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotNull
    @Valid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Nullable
    @Valid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_address_id")
    private Address invoiceAddress;

    @Nullable
    @Valid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communication_details_id")
    private CommunicationDetails communicationDetails;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "brand", nullable = false)
    private Brand brand;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerStatus status;

    public static Customer create(
            @NotBlank final String firstName,
            @NotBlank final String lastName,
            @NotNull @Past final LocalDate birthDate,
            @NotNull @Valid final Address address,
            @Nullable @Valid final Address invoiceAddress,
            @NotNull @Valid final CommunicationDetails communicationDetails,
            @NotNull final Brand brand) {

        return Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .address(address)
                .invoiceAddress(invoiceAddress != null ? invoiceAddress : address)
                .communicationDetails(communicationDetails)
                .brand(brand)
                .status(CustomerStatus.INACTIVE)
                .build();
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", brand=" + brand + ", status=" + status + '}';
    }
}
