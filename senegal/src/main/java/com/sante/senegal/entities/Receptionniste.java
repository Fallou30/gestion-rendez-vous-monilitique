package com.sante.senegal.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("RECEPTIONNISTE")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Receptionniste extends PersonnelMedical {

}