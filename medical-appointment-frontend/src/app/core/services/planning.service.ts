import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interfaces TypeScript correspondant aux DTOs Java
export interface PlanningDto {
  idPlanning: number;
  idMedecin: number;
  nomMedecin: string;
  specialiteMedecin: string;
  idService: number;
  nomService: string;
  idHopital: number;
  nomHopital: string;
  adresseHopital: string;
  date: string; // Format ISO date (YYYY-MM-DD)
  heureDebut: string; // Format ISO time (HH:mm:ss)
  heureFin: string; // Format ISO time (HH:mm:ss)
  reserve: boolean;
  idRendezVous?: number; // Optionnel, null si non réservé
}

export interface PlanningReservationRequestDto {
  idPlanning: number;
  idPatient: number;
  typeConsultation: TypeConsultation;
  motif: string;
}

export enum TypeConsultation {
  CONSULTATION = 'CONSULTATION',
  CONTROLE = 'CONTROLE',
  URGENCE = 'URGENCE',
  CHIRURGIE = 'CHIRURGIE'
}

@Injectable({
  providedIn: 'root'
})
export class PlanningService {
  private readonly apiUrl = `http://localhost:8081/api/v1/planning`;

  constructor(private http: HttpClient) {}

  /**
   * Génère les plannings pour un médecin dans un hôpital pour le mois suivant
   */
  genererPlannings(idMedecin: number, idHopital: number): Observable<string> {
    const params = new HttpParams()
      .set('idMedecin', idMedecin.toString())
      .set('idHopital', idHopital.toString());

    return this.http.post(`${this.apiUrl}/generer`, null, { 
      params,
      responseType: 'text'
    });
  }

  /**
   * Récupère les créneaux disponibles selon les critères spécifiés
   */
  getCreneauxDisponibles(
    dateDebut: string,
    dateFin: string,
    idMedecin?: number,
    idService?: number,
    idHopital?: number
  ): Observable<PlanningDto[]> {
    let params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);

    if (idMedecin) {
      params = params.set('idMedecin', idMedecin.toString());
    }
    if (idService) {
      params = params.set('idService', idService.toString());
    }
    if (idHopital) {
      params = params.set('idHopital', idHopital.toString());
    }

    return this.http.get<PlanningDto[]>(`${this.apiUrl}/creneaux-disponibles`, { params });
  }

  /**
   * Récupère les créneaux disponibles pour une date spécifique
   */
  getCreneauxDisponiblesParDate(
    date: string,
    idMedecin?: number,
    idService?: number,
    idHopital?: number
  ): Observable<PlanningDto[]> {
    let params = new HttpParams()
      .set('date', date);

    if (idMedecin) {
      params = params.set('idMedecin', idMedecin.toString());
    }
    if (idService) {
      params = params.set('idService', idService.toString());
    }
    if (idHopital) {
      params = params.set('idHopital', idHopital.toString());
    }

    return this.http.get<PlanningDto[]>(`${this.apiUrl}/creneaux-disponibles/date`, { params });
  }

  /**
   * Récupère tous les créneaux disponibles d'un médecin sur une période
   */
  getCreneauxDisponiblesParMedecin(
    idMedecin: number,
    dateDebut: string,
    dateFin: string
  ): Observable<PlanningDto[]> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);

    return this.http.get<PlanningDto[]>(`${this.apiUrl}/medecin/${idMedecin}/creneaux-disponibles`, { params });
  }

  /**
   * Récupère tous les créneaux disponibles d'un service sur une période
   */
  getCreneauxDisponiblesParService(
    idService: number,
    dateDebut: string,
    dateFin: string
  ): Observable<PlanningDto[]> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);

    return this.http.get<PlanningDto[]>(`${this.apiUrl}/service/${idService}/creneaux-disponibles`, { params });
  }

  /**
   * Récupère tous les créneaux disponibles d'un hôpital sur une période
   */
  getCreneauxDisponiblesParHopital(
    idHopital: number,
    dateDebut: string,
    dateFin: string
  ): Observable<PlanningDto[]> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);

    return this.http.get<PlanningDto[]>(`${this.apiUrl}/hopital/${idHopital}/creneaux-disponibles`, { params });
  }

  /**
   * Réserve un créneau de consultation pour un patient
   */
  reserverCreneau(request: PlanningReservationRequestDto): Observable<PlanningDto> {
    return this.http.post<PlanningDto>(`${this.apiUrl}/reserver`, request);
  }

  /**
   * Libère un créneau réservé (annule le rendez-vous)
   */
  libererCreneau(idPlanning: number): Observable<PlanningDto> {
    return this.http.put<PlanningDto>(`${this.apiUrl}/liberer/${idPlanning}`, null);
  }

  /**
   * Vérifie si un créneau est disponible pour réservation
   */
  isCreneauDisponible(idPlanning: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/disponible/${idPlanning}`);
  }

  /**
   * Compte le nombre de créneaux disponibles pour un médecin à une date donnée
   */
  countCreneauxDisponibles(idMedecin: number, date: string): Observable<number> {
    const params = new HttpParams()
      .set('idMedecin', idMedecin.toString())
      .set('date', date);

    return this.http.get<number>(`${this.apiUrl}/count/disponibles`, { params });
  }

  // Méthodes utilitaires pour formater les dates
  
  /**
   * Formate une date JavaScript en string ISO (YYYY-MM-DD)
   */
  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  /**
   * Formate une heure JavaScript en string ISO (HH:mm:ss)
   */
  formatTime(date: Date): string {
    return date.toTimeString().split(' ')[0];
  }

  /**
   * Parse une date ISO string en objet Date
   */
  parseDate(dateString: string): Date {
    return new Date(dateString + 'T00:00:00');
  }
  /**
   * Méthode utilitaire pour récupérer les créneaux disponibles par critères
   * (utilisée dans le composant prise-rendez-vous)
   */
  getCreneauxDisponiblesParCriteres(
    idMedecin: number,
    idService: number,
    idHopital: number,
    date: string
  ): Observable<PlanningDto[]> {
    return this.getCreneauxDisponiblesParDate(date, idMedecin, idService, idHopital);
  }


  /**
   * Parse une heure ISO string et une date en objet Date complet
   */
  parseDateTime(dateString: string, timeString: string): Date {
    return new Date(`${dateString}T${timeString}`);
  }
  
}