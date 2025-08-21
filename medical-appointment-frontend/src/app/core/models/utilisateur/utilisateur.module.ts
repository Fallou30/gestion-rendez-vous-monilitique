import { StatutHopital, Service, RendezVous } from "../service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module";

export enum Sexe {
  MASCULIN = 'MASCULIN',
  FEMININ = 'FEMININ'
}

export enum StatutUtilisateur {
  ACTIF = 'ACTIF',
  INACTIF = 'INACTIF',
  SUSPENDU = 'SUSPENDU',
  SUPPRIME = 'SUPPRIME'
}

export enum TypeUtilisateur {
  SUPER_ADMIN = 'SUPER_ADMIN',
  ADMIN = 'ADMIN',
  MEDECIN = 'MEDECIN',
  PATIENT = 'PATIENT',
  RECEPTIONNISTE = 'RECEPTIONNISTE',
  MEDECIN_NOUVEAU='MEDECIN_NOUVEAU'
}

export enum NiveauAcces {
  STANDARD = 'STANDARD',
  AVANCE = 'AVANCE',
  ADMINISTRATEUR = 'ADMINISTRATEUR'
}
// utilisateur.model.ts

export interface Utilisateur {
  id: number;
  nom: string;
  prenom: string;
  dateNaissance: Date | string;
  lieuNaissance: string;
  sexe: Sexe;
  adresse: string;
  telephone: string;
  email: string;
  dateCreation: Date | string;
  dateModification: Date | string;
  dateDerniereConnexion: Date | string;
  statut: StatutUtilisateur;
  type: TypeUtilisateur;
}
export interface UtilisateurDetailDto {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  type: TypeUtilisateur;
  statut: StatutUtilisateur;
  dateNaissance?: string;  // Using string to handle LocalDate from Java
  lieuNaissance?: string;
  sexe?: Sexe;
  adresse?: string;
  telephone?: string;
  dateCreation?: Date | string;
  dateModification?: Date | string;
  dateDerniereConnexion?: Date | string;
  // Patient specific fields
  profession?: string;
  groupeSanguin?: string;
  allergies?: string;
  contactUrgenceNom?: string;
  contactUrgenceTelephone?: string;
  preferencesNotification?: string;
  
  // Receptionist specific fields
  poste?: string;
  idHopital?: number;
  
  // Doctor specific fields
  titre?: string;
  specialite?: string;
  matricule?: string;
  idshopitaux?: number[];
  biographie?: string;
  numeroOrdre?: string;
  experience?: number;
  
  // Common to doctors and receptionists
  idService?: number;
}
export interface ModificationProfilDto {
  // Champs communs
  nom?: string;
  prenom?: string;
  dateNaissance?: string;
  lieuNaissance?: string;
  sexe?: string;
  adresse?: string;
  telephone?: string;
  
  // Champs spécifiques aux patients
  profession?: string;
  numAssurance?: string;
  groupeSanguin?: string;
  allergies?: string;
  contactUrgenceNom?: string;
  contactUrgenceTelephone?: string;
  preferencesNotification?: string;
  
  // Champs spécifiques aux médecins
  titre?: string;
  specialite?: string;
  matricule?: string;
  numeroOrdre?: string;
  experience?: number;
  biographie?: string;
  
  // Champs spécifiques aux réceptionnistes
  poste?: string;
  idHopital?: number;
  idService?: number;
}
export interface CreationMedecinDto extends Utilisateur {
  idsHopitaux?: number[];
  idService?: number;
  specialite?: string;
  numeroOrdre?: string;
  titre?: string;
  matricule?: string;
  experience?: number;
  biographie?: string;
}
export interface ModificationProfilMedecinDto extends Utilisateur {
  specialite?: string;
  biographie?: string;
  numeroOrdre?: string;
  titre?: string;
  matricule?: string;
  experience?: string;
}
export interface MedecinByServiceDto extends Medecin {
  serviceName: string;
}
export interface MedecinByHopitalDto extends Medecin {
  hopitalName: string;
}
export interface NouveauMedecinDto {
  email: string;
  nom: string;
  prenom: string;
  telephone?: string; // Le téléphone est optionnel dans le DTO Java (pas d'annotation @NotBlank)
}
export interface Patient extends Utilisateur {
  numAssurance: string;
  groupeSanguin: string;
  allergies: string;
  contactUrgenceNom: string;
  contactUrgenceTelephone: string;
  profession: string;
  preferencesNotification: string;
  idDossierMedical: number;
}
export interface ModificationPatient extends Utilisateur {
  numAssurance: string;
  groupeSanguin: string;
  allergies: string;
  contactUrgenceNom: string;
  contactUrgenceTelephone: string;
  profession: string;
  preferencesNotification: string;
}
export interface InscriptionBase {
  nom: string;
  prenom: string;
  dateNaissance: Date | string;
  lieuNaissance: string;
  sexe: Sexe;
  adresse: string;
  telephone: string;
  email: string;
  motDePasse: string;
  confirmationMotDePasse: string;
}
export interface CreationAdminDto extends Utilisateur {
  role: string;  // Obligatoire (équivalent @NotBlank)
  permissions?: string;
  idCreateur: number;  // Obligatoire (équivalent @NotNull)
  commentaire?: string;
}

// inscription-patient.model.ts
export interface InscriptionPatient extends InscriptionBase {
  profession: string;
  groupeSanguin: string;
  allergies: string;
  contactUrgenceNom: string;
  contactUrgenceTelephone: string;
  preferencesNotification: string;
}

export interface Medecin extends Utilisateur {
  idsHopitaux: number[];
  idService: number;
  specialite: string;
  numeroOrdre: string;
  titre: string;
  matricule: string;
  experience: number;
  biographie: string;
   diplomePath?: string;
  carteOrdrePath?: string;
  cvPath?: string;

  [key: string]: any;
}
export interface DemandeMedecin extends Utilisateur {
  idsHopitaux: number[];
  idService: number;
  specialite: string;
  numeroOrdre: string;
  titre: string;
  matricule: string;
  experience: number;
  biographie: string;
  diplomePath: string;
  carteOrdrePath: string;
  cvPath: string;
}
export interface ModificationMedecinDto extends Utilisateur {
  idsHopitaux: number[];
  idService: number;
  specialite: string;
  numeroOrdre: string;
  matricule: string;
  titre: string;
  experience: number;
  biographie: string;
  diplomePath?: string;
  carteOrdrePath?: string;
  cvPath?: string;
}


export interface Receptionniste extends Utilisateur {
  poste?: string;
  serviceId?: number;
  hopitalId: number;
}
export interface ModificationAdminDto extends Utilisateur {
  role: string;  // Obligatoire (équivalent @NotBlank)
  permissions: string[];
}


// DTOs pour les requêtes
export interface LoginRequestDto {
  email: string;
  motDePasse: string;
}

export interface AuthResponseDto {
  token: string;
  utilisateur: Utilisateur;
  message?: string;
}



