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
@Table(name = "country")
public class Country {
    @Setter
    @Getter
    @Id
    @SequenceGenerator(
            name = "countrySeq",
            sequenceName = "countrySeq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String type;
    private String number;

    @JoinColumn(name = "customerId", nullable = false)
    private Long customerId;
}
