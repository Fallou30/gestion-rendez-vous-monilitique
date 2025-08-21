package com.sante.senegal.services.interfaces;

import com.sante.senegal.dto.ServiceDto;
import com.sante.senegal.dto.ServiceRequestDto;
import com.sante.senegal.entities.Service.StatutService;

import java.util.List;

public interface ServiceHospitalierService {
    List<ServiceDto> getAllServices();
    ServiceDto getServiceById(Long id);
    List<ServiceDto> getServicesByHopital(Long idHopital);
    List<ServiceDto> getServicesByStatut(StatutService statut);
    List<ServiceDto> searchServicesByNom(String nom);
    List<ServiceDto> getServicesByChefService(Long idChefService);
    List<ServiceDto> getActiveServicesByVille(String ville);
    ServiceDto createService(ServiceRequestDto dto);
    ServiceDto updateService(Long id, ServiceRequestDto dto);
    void deleteService(Long id);
    ServiceDto changeStatutService(Long id, StatutService nouveauStatut);
}
