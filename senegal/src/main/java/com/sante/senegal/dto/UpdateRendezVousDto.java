package com.sante.senegal.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateRendezVousDto {
    private LocalDateTime dateHeure;
    private Integer dureePrevue;
}

