package com.sante.senegal.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.opencsv.CSVWriter;
import com.sante.senegal.entities.Consultation;
import com.sante.senegal.entities.Patient;
import com.sante.senegal.repositories.ConsultationRepository;
import com.sante.senegal.repositories.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;

    public String exporterPatientsCSV() throws IOException {
        List<Patient> patients = patientRepository.findAll();
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);

        String[] entetes = {"ID", "Nom", "Prénom", "Date de naissance", "Téléphone", "Email", "Adresse"};
        csvWriter.writeNext(entetes);

        for (Patient patient : patients) {
            String[] ligne = {
                    patient.getId().toString(),
                    patient.getNom(),
                    patient.getPrenom(),
                    patient.getDateNaissance().toString(),
                    patient.getTelephone(),
                    patient.getEmail(),
                    patient.getAdresse()
            };
            csvWriter.writeNext(ligne);
        }

        csvWriter.close();
        return writer.toString();
    }

    public String exporterConsultationsJSON(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        List<Consultation> consultations = consultationRepository
                .findByDateHeureBetween(dateDebut.atStartOfDay(), dateFin.plusDays(1).atStartOfDay());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Map<String, Object> export = new HashMap<>();
        export.put("periode", Map.of("debut", dateDebut, "fin", dateFin));
        export.put("consultations", consultations);
        export.put("nombreConsultations", consultations.size());
        export.put("dateExport", LocalDateTime.now());

        return mapper.writeValueAsString(export);
    }

    public byte[] genererRapportPDF(Long patientId) throws Exception {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient non trouvé"));

        List<Consultation> consultations = consultationRepository
                .findByPatientIdOrderByDateDesc(patientId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Rapport médical").setBold().setFontSize(18));

        document.add(new Paragraph("\nInformations Patient:"));
        document.add(new Paragraph("Nom: " + patient.getNom()));
        document.add(new Paragraph("Prénom: " + patient.getPrenom()));
        document.add(new Paragraph("Date de naissance: " + patient.getDateNaissance()));

        document.add(new Paragraph("\nHistorique des consultations:"));
        for (Consultation consultation : consultations) {
            document.add(new Paragraph("Date: " + consultation.getDateHeure()));
            document.add(new Paragraph("Diagnostic: " + consultation.getDiagnostic()));
            document.add(new Paragraph("Observations: " + consultation.getObservations()));
            document.add(new Paragraph("Recommandations: " + consultation.getRecommandations()));
            document.add(new Paragraph("Symptômes: " + consultation.getSymptomes()));
            document.add(new Paragraph("Prescriptions"+ consultation.getPrescriptions()));
            document.add(new Paragraph("---------------"));
        }

        document.close();
        return baos.toByteArray();
    }
}
