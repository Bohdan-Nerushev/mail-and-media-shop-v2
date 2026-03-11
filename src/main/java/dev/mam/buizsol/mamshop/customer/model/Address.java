package dev.mam.buizsol.mamshop.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    @Size(max = 250)
    @Column(name = "street", nullable = false)
    private String street;

    @NotBlank
    @Size(max = 100)
    @Column(name = "number", nullable = false)
    private String number;

    @NotBlank
    @Size(max = 100)
    @Column(name = "postcode", nullable = false)
    private String postcode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "country", nullable = false)
    private String country;

    public Address(
            final String street, final String number, final String postcode, final String city, final String country) {
        this.street = street;
        this.number = number;
        this.postcode = postcode;
        this.city = city;
        this.country = country;
    }
}
