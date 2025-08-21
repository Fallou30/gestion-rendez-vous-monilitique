// models/hopital.model.ts
// hopital.model.ts
export enum StatutHopital {
  ACTIF = 'ACTIF',
  INACTIF = 'INACTIF',
  EN_CONSTRUCTION = 'EN_CONSTRUCTION',
  MAINTENANCE = "MAINTENANCE"
}

export interface Hopital {
  idHopital: number;
  nom: string;
  adresse: string;
  ville: string;
  region: string;
  telephone: string;
  email: string;
  siteWeb: string;
  coordonneesGps: string;
  heuresOuverture: string;
  typeEtablissement: string;
  capaciteLits: number;
  statut: StatutHopital;
}

// hopital-request.model.ts
export interface HopitalRequest {
  nom: string;
  adresse: string;
  ville: string;
  region: string;
  telephone: string;
  email: string;
  siteWeb: string;
  coordonneesGps: string;
  heuresOuverture: string;
  typeEtablissement: string;
  capaciteLits: number;
}

// service.model.ts
export interface Service {
  idService: number;
  idHopital: number;
  nomHopital: string;
  
  nom: string;
  description: string;
  emplacement: string;
  telephone: string;
  email: string;
  capacitePatientsJour: number;
  
  idChefService: number;
  nomChefService: string;
  prenomChefService: string;
  
  statut: StatutService;
}
// service-request.model.ts
export interface ServiceRequest {
  idHopital: number;
  nom: string;
  description: string;
  emplacement: string;
  telephone: string;
  email: string;
  capacitePatientsJour: number;
  idChefService: number;
}

export enum StatutService {
  ACTIF = 'ACTIF',
  INACTIF = 'INACTIF',
  MAINTENANCE = 'MAINTENANCE'
}

// models/disponibilite.model.ts
export interface Disponibilite {
  idDisponibilite?: number;
  medecin?: any;
  service?: Service;
  date: string;
  heureDebut: string;
  heureFin: string;
  statut?: StatutDisponibilite;
  motifIndisponibilite?: string;
  recurrence?: string;
  dateFinRecurrence?: string;
}

export enum StatutDisponibilite {
  DISPONIBLE = 'DISPONIBLE',
  OCCUPE = 'OCCUPE',
  INDISPONIBLE = 'INDISPONIBLE'
}

// models/rendez-vous.model.ts
export interface RendezVous {
  idRdv: number;
  patientId: number;
  patientNomComplet: string;
  medecinId: number;
  medecinNomComplet: string;
  medecinSpecialite: string;
  serviceId: number;
  serviceNom: string;
  hopitalId: number;
  hopitalNom: string;
  adresseHopital: string;
  villeHopital: string;
  regionHopital: string;
  dateHeure: Date | string;
  dureePrevue: number;
  typeConsultation: TypeConsultation;
  motif: string;
  niveauUrgence: NiveauUrgence;
  statut: StatutRendezVous;
  dateCreation: Date | string;
  dateModification: Date | string;
  modePriseRdv: ModePriseRdv;
}
// rendez-vous-request.model.ts
export interface RendezVousRequest {
  dateHeure: Date | string;
  dureePrevue: number;
  motif: string;
  typeConsultation: TypeConsultation | string;
  niveauUrgence: NiveauUrgence | string;
  idPatient: number;
  idMedecin: number;
  idService: number;
  idHopital: number;
}
// rendez-vous.enum.ts
export enum TypeConsultation {
  CONSULTATION = 'CONSULTATION',
  URGENCE = 'URGENCE',
  SUIVI = 'SUIVI',
  CONTROLE = 'CONTROLE'
}

export enum NiveauUrgence {
  NORMAL = 'NORMAL',
  URGENT = 'URGENT',
  TRES_URGENT = 'TRES_URGENT'
}

export enum StatutRendezVous {
  PLANIFIE = 'PLANIFIE',
  CONFIRME = 'CONFIRME',
  ANNULE = 'ANNULE',
  TERMINE = 'TERMINE',
  EN_COURS = 'EN_COURS'
}

export enum ModePriseRdv {
  EN_LIGNE = 'EN_LIGNE',
  SUR_PLACE = 'SUR_PLACE',
  TELEPHONE = 'TELEPHONE'
}