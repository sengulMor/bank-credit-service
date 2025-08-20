package com.bank.credit.service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//@Data is not used here, because it creates also the toString, eauls, hashcode for the entity
// that is not suitable with jpa, when there are relations then can toString make a infinitive loop
@Getter
@Setter
@NoArgsConstructor       //generates a constructor with no parameters (default constructor).
@AllArgsConstructor      //generates a constructor with one parameter for each field in the class.
@Entity
@EntityListeners(AuditingEntityListener.class)  // interacts(listens) actions of this entity to create or update the fields for time
public class Customer extends BaseEntity {

    @NotBlank(message = "Name can not be blank")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Surname can not be blank")
    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    //precision = 15: Total number of digits allowed (before + after decimal).
    //scale = 2: Number of digits after the decimal (i.e., cents for money).
    @Column(precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(precision = 15, scale = 2)
    private BigDecimal usedCreditLimit;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

}
