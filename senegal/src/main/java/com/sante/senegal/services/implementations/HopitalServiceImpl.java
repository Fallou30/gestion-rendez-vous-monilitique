package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.HopitalDto;
import com.sante.senegal.dto.HopitalRequestDto;
import com.sante.senegal.entities.Hopital;
import com.sante.senegal.repositories.HopitalRepository;

import com.sante.senegal.services.interfaces.HopitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HopitalServiceImpl implements HopitalService {

    private final HopitalRepository hopitalRepository;

    @Override
    public List<HopitalDto> getAllHopitaux() {
        return hopitalRepository.findAll().stream().map(this::toDto).collect(toList());
    }

    @Override
    public HopitalDto getHopitalById(Long id) {
        return hopitalRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé avec l'ID: " + id));
    }

    @Override
    public List<HopitalDto> getHopitauxByStatut(Hopital.StatutHopital statut) {
        return hopitalRepository.findByStatut(statut).stream().map(this::toDto).collect(toList());
    }

    @Override
    public List<HopitalDto> getHopitauxByVille(String ville) {
        return hopitalRepository.findByVille(ville).stream().map(this::toDto).collect(toList());
    }

    @Override
    public List<HopitalDto> getHopitauxByRegion(String region) {
        return hopitalRepository.findByRegion(region).stream().map(this::toDto).collect(toList());
    }

    @Override
    public List<HopitalDto> searchHopitauxByNom(String nom) {
        return hopitalRepository.findByNomContainingIgnoreCase(nom).stream().map(this::toDto).collect(toList());
    }

    @Override
    public List<HopitalDto> getHopitauxByTypeEtablissement(String type) {
        return hopitalRepository.findByTypeEtablissement(type).stream().map(this::toDto).collect(toList());
    }

    @Override
    public List<HopitalDto> getHopitauxByCapaciteMinimum(Integer capaciteMin) {
        return hopitalRepository.findByCapaciteMinimum(capaciteMin).stream().map(this::toDto).collect(toList());
    }

    @Override
    public HopitalDto createHopital(HopitalRequestDto dto) {
        Hopital hopital = fromDto(dto);
        log.info("Création de l’hôpital: {}", dto.getNom());
        return toDto(hopitalRepository.save(hopital));
    }

    @Override
    public HopitalDto updateHopital(Long id, HopitalRequestDto dto) {
        Hopital hopital = hopitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé avec l'ID: " + id));

        hopital.setNom(dto.getNom());
        hopital.setAdresse(dto.getAdresse());
        hopital.setVille(dto.getVille());
        hopital.setRegion(dto.getRegion());
        hopital.setTelephone(dto.getTelephone());
        hopital.setEmail(dto.getEmail());
        hopital.setSiteWeb(dto.getSiteWeb());
        hopital.setCoordonneesGps(dto.getCoordonneesGps());
        hopital.setHeuresOuverture(dto.getHeuresOuverture());
        hopital.setTypeEtablissement(dto.getTypeEtablissement());
        hopital.setCapaciteLits(dto.getCapaciteLits());

        return toDto(hopitalRepository.save(hopital));
    }

    @Override
    public void deleteHopital(Long id) {
        if (!hopitalRepository.existsById(id)) {
            throw new RuntimeException("Hôpital non trouvé avec l'ID: " + id);
        }
        log.info("Suppression de l’hôpital avec ID: {}", id);
        hopitalRepository.deleteById(id);
    }

    @Override
    public HopitalDto changeStatutHopital(Long id, Hopital.StatutHopital nouveauStatut) {
        Hopital hopital = hopitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hôpital non trouvé avec l'ID: " + id));
        hopital.setStatut(nouveauStatut);
        return toDto(hopitalRepository.save(hopital));
    }

    // ==== MAPPERS ====

    private HopitalDto toDto(Hopital hopital) {
        HopitalDto dto = new HopitalDto();
        dto.setIdHopital(hopital.getIdHopital());
        dto.setNom(hopital.getNom());
        dto.setAdresse(hopital.getAdresse());
        dto.setVille(hopital.getVille());
        dto.setRegion(hopital.getRegion());
        dto.setTelephone(hopital.getTelephone());
        dto.setEmail(hopital.getEmail());
        dto.setSiteWeb(hopital.getSiteWeb());
        dto.setCoordonneesGps(hopital.getCoordonneesGps());
        dto.setHeuresOuverture(hopital.getHeuresOuverture());
        dto.setTypeEtablissement(hopital.getTypeEtablissement());
        dto.setCapaciteLits(hopital.getCapaciteLits());
        dto.setStatut(hopital.getStatut());
        return dto;
    }

    private Hopital fromDto(HopitalRequestDto dto) {
        return Hopital.builder()
                .nom(dto.getNom())
                .adresse(dto.getAdresse())
                .ville(dto.getVille())
                .region(dto.getRegion())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .siteWeb(dto.getSiteWeb())
                .coordonneesGps(dto.getCoordonneesGps())
                .heuresOuverture(dto.getHeuresOuverture())
                .typeEtablissement(dto.getTypeEtablissement())
                .capaciteLits(dto.getCapaciteLits())
                .statut(Hopital.StatutHopital.ACTIF)
                .build();
    }
}
