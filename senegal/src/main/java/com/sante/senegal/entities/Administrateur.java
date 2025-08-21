package com.sante.senegal.entities;

import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "administrateurs")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends Utilisateur {
    @Column(length = 50)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String permissions;
}
