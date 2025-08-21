package com.sante.senegal.config;

import com.sante.senegal.dto.*;
import com.sante.senegal.entities.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // ==== Mapping RendezVous → RendezVousDto ====
        modelMapper.addMappings(new PropertyMap<RendezVous, RendezVousDto>() {
            @Override
            protected void configure() {
                map().setPatientId(source.getPatient() != null ? source.getPatient().getId() : null);
                map().setPatientNomComplet(source.getPatient() != null ? source.getPatient().getPrenom() + " " + source.getPatient().getNom() : null);

                map().setMedecinId(source.getMedecin() != null ? source.getMedecin().getId() : null);
                map().setMedecinNomComplet(source.getMedecin() != null ? source.getMedecin().getPrenom() + " " + source.getMedecin().getNom() : null);
                map().setMedecinSpecialite(source.getMedecin() != null ? source.getMedecin().getSpecialite() : null);
                map().setServiceId(source.getService() != null ? source.getService().getIdService() : null);
                map().setServiceNom(source.getService() != null ? source.getService().getNom() : null);

                map().setHopitalId(source.getHopital() != null ? source.getHopital().getIdHopital() : null);
                map().setHopitalNom(source.getHopital() != null ? source.getHopital().getNom() : null);
                map().setAdresseHopital(source.getHopital() != null ? source.getHopital().getAdresse() : null);
                map().setVilleHopital(source.getHopital() != null ? source.getHopital().getVille() : null);
                map().setRegionHopital(source.getHopital() != null ? source.getHopital().getRegion() : null);
            }
        });

        // ==== Mapping Consultation → ConsultationDto ====
        modelMapper.typeMap(Consultation.class, ConsultationDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getRendezVous().getIdRdv(), ConsultationDto::setIdRendezVous);
                    mapper.map(src -> src.getDossier().getIdDossier(), ConsultationDto::setIdDossier);
                });


        modelMapper.typeMap(DocumentMedical.class, DocumentMedicalDto.class).addMappings(mapper -> mapper.map(src -> src.getDossier().getIdDossier(), DocumentMedicalDto::setIdDossier));
        modelMapper.typeMap(Medecin.class, MedecinDto.class).addMappings(mapper -> {
            mapper.skip(MedecinDto::setIdsHopitaux);
            mapper.skip(MedecinDto::setIdService);
        });
        modelMapper.typeMap(Planning.class, PlanningDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getMedecin().getId(), PlanningDto::setIdMedecin);
            mapper.map(src -> src.getMedecin().getNom(), PlanningDto::setNomMedecin);
            mapper.map(src-> src.getMedecin().getSpecialite(),PlanningDto::setSpecialiteMedecin );
            mapper.map(src -> src.getService().getIdService(), PlanningDto::setIdService);
            mapper.map(src -> src.getService().getNom(), PlanningDto::setNomService);
            mapper.map(src -> src.getHopital().getIdHopital(), PlanningDto::setIdHopital);
            mapper.map(src -> src.getHopital().getNom(), PlanningDto::setNomHopital);
            mapper.map(src -> src.getHopital().getAdresse(),PlanningDto::setAdresseHopital);
        });

        return modelMapper;
    }
}
