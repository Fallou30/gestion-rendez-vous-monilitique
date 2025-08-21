import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';

import { FileUploadService, FileUploadResponse } from './file-upload.service';
import { AuthResponseDto, UtilisateurDetailDto, MedecinByServiceDto, MedecinByHopitalDto, DemandeMedecin, Medecin } from '../models/utilisateur/utilisateur.module';





@Injectable({
  providedIn: 'root'
})
export class MedecinService {
  private readonly apiUrl = 'http://localhost:8081/api/v1';

  constructor(
    private http: HttpClient,
    private fileUploadService: FileUploadService
  ) {}

  /**
   * Soumettre une demande d'inscription médecin avec fichiers
   * Utilise le FileUploadService pour gérer l'upload des fichiers
   */
  demanderInscriptionMedecin(
    dto: DemandeMedecin,
    cv?: File,
    diplome?: File,
    carteOrdre?: File
  ): Observable<AuthResponseDto> {
    // Si des fichiers sont fournis, les uploader d'abord
    if (cv || diplome || carteOrdre) {
      return this.uploadFichiersEtCreerDemande(dto, { cv, diplome, carteOrdre });
    }
    
    // Sinon, créer la demande sans fichiers
    return this.creerDemandeMedecin(dto);
  }

  /**
   * Upload les fichiers puis créer la demande avec les chemins des fichiers
   */
  private uploadFichiersEtCreerDemande(
    dto: DemandeMedecin, 
    fichiers: { cv?: File; diplome?: File; carteOrdre?: File }
  ): Observable<AuthResponseDto> {
    const uploadObservables: Observable<FileUploadResponse>[] = [];
    const fichierTypes: string[] = [];

    if (fichiers.cv) {
      uploadObservables.push(this.fileUploadService.uploadFile(fichiers.cv, 'cv'));
      fichierTypes.push('cv');
    }
    if (fichiers.diplome) {
      uploadObservables.push(this.fileUploadService.uploadFile(fichiers.diplome, 'diplome'));
      fichierTypes.push('diplome');
    }
    if (fichiers.carteOrdre) {
      uploadObservables.push(this.fileUploadService.uploadFile(fichiers.carteOrdre, 'carteOrdre'));
      fichierTypes.push('carteOrdre');
    }

    return forkJoin(uploadObservables).pipe(
      switchMap((uploadResponses: FileUploadResponse[]) => {
        // Ajouter les chemins des fichiers au DTO
        const dtoAvecFichiers = { ...dto };
        uploadResponses.forEach((response, index) => {
          const type = fichierTypes[index];
          switch (type) {
            case 'cv':
              (dtoAvecFichiers as any).cvPath = response.filePath;
              break;
            case 'diplome':
              (dtoAvecFichiers as any).diplomePath = response.filePath;
              break;
            case 'carteOrdre':
              (dtoAvecFichiers as any).carteOrdrePath = response.filePath;
              break;
          }
        });
        
        return this.creerDemandeMedecin(dtoAvecFichiers);
      })
    );
  }

  /**
   * Créer la demande médecin avec FormData
   */
  private creerDemandeMedecin(dto: DemandeMedecin | any): Observable<AuthResponseDto> {
    const formData = new FormData();
    
    Object.keys(dto).forEach(key => {
      const value = dto[key];
      if (value !== null && value !== undefined) {
        if (Array.isArray(value)) {
          value.forEach((item, index) => {
            formData.append(`${key}[${index}]`, item.toString());
          });
        } else {
          formData.append(key, value.toString());
        }
      }
    });

    return this.http.post<AuthResponseDto>(`${this.apiUrl}/medecin/demande`, formData);
  }

  /**
   * Obtenir un médecin par son ID
   */
  getMedecinById(id: number): Observable<Medecin> {
    return this.http.get<Medecin>(`${this.apiUrl}/medecin/${id}`);
  }

  /**
   * Vérifier si un email existe déjà
   */
  emailExiste(email: string): Observable<boolean> {
    const params = new HttpParams().set('email', email);
    return this.http.get<boolean>(`${this.apiUrl}/public/medecin/email-existe`, { params });
  }

  /**
   * Modifier le profil d'un médecin
   */
  modifierProfil(id: number, dto: UtilisateurDetailDto): Observable<UtilisateurDetailDto> {
    return this.http.put<UtilisateurDetailDto>(`${this.apiUrl}/medecin/${id}/profil`, dto);
  }

  /**
   * Obtenir les médecins par service
   */
  getMedecinsByService(serviceId: number): Observable<MedecinByServiceDto[]> {
    return this.http.get<MedecinByServiceDto[]>(`${this.apiUrl}/medecins/service/${serviceId}`);
  }

  /**
   * Obtenir les médecins par hôpital
   */
  getMedecinsByHopital(hopitalId: number): Observable<MedecinByHopitalDto[]> {
    return this.http.get<MedecinByHopitalDto[]>(`${this.apiUrl}/medecins/hopital/${hopitalId}`);
  }

  /**
   * Obtenir les médecins par service et hôpital
   */
  getMedecinsByServiceAndHopital(serviceId: number, hopitalId: number): Observable<Medecin[]> {
    return this.http.get<Medecin[]>(`${this.apiUrl}/medecins/service/${serviceId}/hopital/${hopitalId}`);
  }

  /**
   * Méthodes utilitaires pour la gestion des formulaires
   */

  /**
   * Valider les données avant soumission
   */
  validerDonneesMedecin(dto: DemandeMedecin): string[] {
    const erreurs: string[] = [];

    if (!dto.nom?.trim()) erreurs.push('Le nom est obligatoire');
    if (!dto.prenom?.trim()) erreurs.push('Le prénom est obligatoire');
    if (!dto.email?.trim()) erreurs.push('L\'email est obligatoire');
    if (!dto.telephone?.trim()) erreurs.push('Le téléphone est obligatoire');
    if (!dto.specialite?.trim()) erreurs.push('La spécialité est obligatoire');
    if (!dto.numeroOrdre?.trim()) erreurs.push('Le numéro d\'ordre est obligatoire');
    if (!dto.matricule?.trim()) erreurs.push('Le matricule est obligatoire');
    if (!dto.idsHopitaux || dto.idsHopitaux.length === 0) {
      erreurs.push('Au moins un hôpital doit être sélectionné');
    }
    if (!dto.idService) erreurs.push('Le service est obligatoire');

    return erreurs;
  }

  /**
   * Valider les fichiers uploadés en utilisant le FileUploadService
   */
  validerFichiers(cv?: File, diplome?: File, carteOrdre?: File): string[] {
    const erreurs: string[] = [];

    const validerFichier = (fichier: File | undefined, nom: string) => {
      if (fichier) {
        const validation = this.fileUploadService.validateFile(fichier);
        if (!validation.valid && validation.message) {
          erreurs.push(`${nom}: ${validation.message}`);
        }
      }
    };

    validerFichier(cv, 'CV');
    validerFichier(diplome, 'Diplôme');
    validerFichier(carteOrdre, 'Carte d\'ordre');

    return erreurs;
  }

  /**
   * Obtenir la progression d'upload des fichiers
   */
  getUploadProgress() {
    return this.fileUploadService.getUploadProgress();
  }

  /**
   * Prévisualiser un fichier uploadé
   */
  previewFile(filePath: string): string {
    return this.fileUploadService.getFilePreviewUrl(filePath);
  }

  /**
   * Télécharger un fichier
   */
  downloadFile(filePath: string): Observable<Blob> {
    return this.fileUploadService.downloadFile(filePath);
  }

  /**
   * Supprimer un fichier uploadé
   */
  deleteFile(filePath: string): Observable<any> {
    return this.fileUploadService.deleteFile(filePath);
  }
}