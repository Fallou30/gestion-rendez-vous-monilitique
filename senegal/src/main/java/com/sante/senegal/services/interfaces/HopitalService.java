package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.HopitalDto;
import com.sante.senegal.dto.HopitalRequestDto;
import com.sante.senegal.entities.Hopital;

import java.util.List;

public interface HopitalService {
    List<HopitalDto> getAllHopitaux();
    HopitalDto getHopitalById(Long id);
    List<HopitalDto> getHopitauxByStatut(Hopital.StatutHopital statut);
    List<HopitalDto> getHopitauxByVille(String ville);
    List<HopitalDto> getHopitauxByRegion(String region);
    List<HopitalDto> searchHopitauxByNom(String nom);
    List<HopitalDto> getHopitauxByTypeEtablissement(String type);
    List<HopitalDto> getHopitauxByCapaciteMinimum(Integer capaciteMin);
    HopitalDto createHopital(HopitalRequestDto dto);
    HopitalDto updateHopital(Long id, HopitalRequestDto dto);
    void deleteHopital(Long id);
    HopitalDto changeStatutHopital(Long id, Hopital.StatutHopital nouveauStatut);
}
