package com.sante.senegal.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "personnel_medical")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelMedical extends Utilisateur {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hopital")
    private Hopital hopital;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service")
    private Service service;
    private String poste;

}