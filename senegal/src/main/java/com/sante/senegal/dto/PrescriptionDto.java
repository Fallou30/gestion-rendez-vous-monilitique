package com.sante.senegal.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionDto {
    private Long idPrescription;
    private String diagnostic;
    private LocalDateTime datePrescription;
    private Long medecinId;
    private Long patientId;
    private List<MedicamentPrescritDto> medicaments;
}
