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
@Table(name = "identity")
public class Identity {
    @Setter
    @Getter
    @Id
    @SequenceGenerator(
            name = "identitySeq",
            sequenceName = "identitySeq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String type;
    private String number;
    private String image;
    private String country;

    @JoinColumn(name = "customerId", nullable = false)
    private Long customerId;
}
