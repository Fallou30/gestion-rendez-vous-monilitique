package com.sante.senegal.services.implementations;

import com.sante.senegal.dto.ServiceDto;
import com.sante.senegal.dto.ServiceRequestDto;
import com.sante.senegal.entities.Service.StatutService;
import com.sante.senegal.repositories.ServiceRepository;

import com.sante.senegal.services.interfaces.ServiceHospitalierService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceHospitalierServiceImpl implements ServiceHospitalierService {

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceDto getServiceById(Long id) {
        com.sante.senegal.entities.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service non trouvé avec l'ID: " + id));
        return modelMapper.map(service, ServiceDto.class);
    }

    @Override
    public List<ServiceDto> getServicesByHopital(Long idHopital) {
        return serviceRepository.findByHopitalIdHopital(idHopital).stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDto> getServicesByStatut(StatutService statut) {
        return serviceRepository.findByStatut(statut).stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDto> searchServicesByNom(String nom) {
        return serviceRepository.findByNomContainingIgnoreCase(nom).stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDto> getServicesByChefService(Long idChefService) {
        return serviceRepository.findByChefServiceId(idChefService).stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDto> getActiveServicesByVille(String ville) {
        return serviceRepository.findActiveServicesByVille(ville).stream()
                .map(service -> modelMapper.map(service, ServiceDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceDto createService(ServiceRequestDto dto) {
       com.sante.senegal.entities.Service service = modelMapper.map(dto, com.sante.senegal.entities.Service.class);
        log.info("Création d'un nouveau service: {}", service.getNom());
        return modelMapper.map(serviceRepository.save(service), ServiceDto.class);
    }

    @Override
    public ServiceDto updateService(Long id, ServiceRequestDto dto) {
       com.sante.senegal.entities.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service non trouvé avec l'ID: " + id));

        modelMapper.map(dto, service);
        log.info("Mise à jour du service: {}", service.getNom());
        return modelMapper.map(serviceRepository.save(service), ServiceDto.class);
    }

    @Override
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new EntityNotFoundException("Service non trouvé avec l'ID: " + id);
        }
        log.info("Suppression du service avec ID: {}", id);
        serviceRepository.deleteById(id);
    }

    @Override
    public ServiceDto changeStatutService(Long id, StatutService nouveauStatut) {
       com.sante.senegal.entities.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service non trouvé avec l'ID: " + id));
        service.setStatut(nouveauStatut);
        log.info("Changement de statut du service {} vers {}", service.getNom(), nouveauStatut);
        return modelMapper.map(serviceRepository.save(service), ServiceDto.class);
    }
}
