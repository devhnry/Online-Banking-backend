package org.henry.onlinebankingsystemp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Getter
@Setter
@ToString
@Table(name = "address")
public class Address {
    @Setter
    @Getter
    @Id
    @SequenceGenerator(
            name = "addressSeq",
            sequenceName = "addressSeq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    @JoinColumn(name = "customerId", nullable = false)
    private Long customerId;
}
