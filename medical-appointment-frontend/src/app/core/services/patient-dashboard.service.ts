// patient-dashboard.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { NiveauUrgence, RendezVous, StatutRendezVous, TypeConsultation } from '../models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';
import { Consultation, ConsultationService } from './consultation.service';
import { DossierMedical, DossierMedicalService, DocumentMedical } from './dossier-medical.service';
import { Examen, ExamenService } from './examen.service';
import { RendezVousService } from './rendez-vous.service';
import { Prescription, PrescriptionService } from './prescription.service';

export interface PatientDashboardData {
  prochainRendezVous: RendezVous[]; // ← corriger ici si tu fais du .map(...)
  rendezVousRecents: RendezVous[];
  consultationsRecentes: Consultation[];
  prescriptionsActives: Prescription[];
  examensEnAttente: Examen[];
  examensRecents: Examen[];
  dossierMedical: DossierMedical;
  alertes: AlertePatient[];
  statistiques: PatientStatistiques;
}


export interface AlertePatient {
  id: number;
  type: 'MEDICAMENT' | 'RENDEZ_VOUS' | 'EXAMEN' | 'URGENCE';
  message: string;
  priorite: 'HAUTE' | 'MOYENNE' | 'BASSE';
  dateCreation: Date;
  lue: boolean;
}

export interface PatientStatistiques {
  nombreConsultations: number;
  nombrePrescriptions: number;
  nombreExamens: number;
  derniereConsultation: Date;
  prochainRendezVous: Date;
  medicamentsActifs: number;
  examensEnAttente: number;
}

export interface RendezVousPatient {
  id: number;
  dateRendezVous: Date;
  heureRendezVous: string;
  motif: string;
  statut: 'PROGRAMME' | 'CONFIRME' | 'EN_COURS' | 'TERMINE' | 'ANNULE';
  medecinNom: string;
  specialite: string;
  dureeEstimee: number;
  urgence: boolean;
  notes: string;
}

export interface ConsultationPatient {
  id: number;
  dateConsultation: Date;
  medecinNom: string;
  specialite: string;
  diagnostic: string;
  symptomes: string;
  recommandations: string;
  dureeReelle: number;
  statut: string;
}

export interface PrescriptionPatient {
  id: number;
  dateCreation: Date;
  medecinNom: string;
  dureeTraitement: number;
  instructionsGenerales: string;
  statut: 'ACTIVE' | 'TERMINEE' | 'SUSPENDUE';
  medicaments: MedicamentPrescritPatient[];
}

export interface MedicamentPrescritPatient {
  id: number;
  nomMedicament: string;
  dosage: string;
  frequence: string;
  duree: number;
  instructionsSpecifiques: string;
  quantitePrescrite: number;
  quantiteRestante: number;
  statut: 'PRESCRIT' | 'EN_COURS' | 'TERMINE' | 'ARRETE';
  dateDebut: Date;
  dateFin: Date;
  prochainePrise: Date;
}

export interface ExamenPatient {
  id: number;
  consultationId: number;
  typeExamen: string;
  nomExamen: string;
  description: string;
  dateRealisation: Date;
  urgence: boolean;
  statut: 'PRESCRIT' | 'PROGRAMME' | 'REALISE' | 'ANNULE';
  resultats: string;
  interpretation: string;
  medecinPrescripteur: string;
}

@Injectable({
  providedIn: 'root'
})
export class PatientDashboardService {
  private apiUrl = 'http://localhost:8081/api/v1';

  constructor(
    private http: HttpClient,
    private rendezVousService: RendezVousService,
    private consultationService: ConsultationService,
    private prescriptionService: PrescriptionService,
    private examenService: ExamenService,
    private dossierMedicalService: DossierMedicalService
  ) {}

  // Récupérer toutes les données du dashboard patient
  getPatientDashboardData(patientId: number): Observable<PatientDashboardData> {
    const today = new Date();
    const dateDebut = new Date(today.getFullYear(), today.getMonth() - 3, 1).toISOString().split('T')[0];
    const dateFin = today.toISOString().split('T')[0];

    return forkJoin({
      prochainRendezVous: this.getProchainRendezVous(patientId),
      rendezVousRecents: this.rendezVousService.getRendezVousByPatient(patientId),
      consultationsRecentes: this.consultationService.getHistoriqueConsultations(patientId),
      prescriptionsActives: this.prescriptionService.getPrescriptionsActives(patientId),
      examensEnAttente: this.getExamensEnAttentePatient(patientId),
      examensRecents: this.examenService.getExamensPatient(patientId),
      dossierMedical: this.dossierMedicalService.getDossierPatient(patientId),
      alertes: this.getAlertesPatient(patientId),
      statistiques: this.getStatistiquesPatient(patientId)
    }).pipe(
      map(data => {
        // Transformer les rendez-vous patients en RendezVous
        const prochainRendezVous: RendezVous[] = data.prochainRendezVous.map(rv => ({
          idRdv: rv.id,
          dateHeure: `${this.formatDate(rv.dateRendezVous)}T${rv.heureRendezVous}`,
          motif: rv.motif,
          statut: this.mapToStatutRendezVous(rv.statut),
          notesMedecin: rv.notes,
          // Valeurs par défaut pour les propriétés requises
          dureePrevue: rv.dureeEstimee || 30,
          niveauUrgence: rv.urgence ? NiveauUrgence.URGENT : NiveauUrgence.NORMALE,
          typeConsultation: TypeConsultation.CONSULTATION_GENERALE,
          // Autres propriétés optionnelles
          notesPatient: '',
          modePriseRdv: 'EN_LIGNE',
        }));

        return {
          prochainRendezVous,
          rendezVousRecents: data.rendezVousRecents.slice(0, 5),
          consultationsRecentes: data.consultationsRecentes.slice(0, 5),
          prescriptionsActives: data.prescriptionsActives,
          examensEnAttente: data.examensEnAttente,
          examensRecents: data.examensRecents.slice(0, 5),
          dossierMedical: data.dossierMedical,
          alertes: data.alertes,
          statistiques: data.statistiques
        };
      })
    );
  }

  // Helper function to format date
  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  // Helper function to map status strings to enum
  private mapToStatutRendezVous(statut: string): StatutRendezVous {
    switch(statut) {
      case 'PROGRAMME': return StatutRendezVous.PROGRAMME;
      case 'CONFIRME': return StatutRendezVous.CONFIRME;
      case 'EN_COURS': return StatutRendezVous.EN_COURS;
      case 'TERMINE': return StatutRendezVous.TERMINE;
      case 'ANNULE': return StatutRendezVous.ANNULE;
      default: return StatutRendezVous.PROGRAMME;
    }
  }

  // Récupérer les prochains rendez-vous
  getProchainRendezVous(patientId: number): Observable<RendezVousPatient[]> {
    return this.http.get<RendezVousPatient[]>(`${this.apiUrl}/rendez-vous/patient/${patientId}/prochains`);
  }

  // Récupérer les examens en attente pour un patient
  getExamensEnAttentePatient(patientId: number): Observable<ExamenPatient[]> {
    return this.http.get<ExamenPatient[]>(`${this.apiUrl}/examens/patient/${patientId}/en-attente`);
  }

  // Récupérer les alertes du patient
  getAlertesPatient(patientId: number): Observable<AlertePatient[]> {
    return this.http.get<AlertePatient[]>(`${this.apiUrl}/alertes/patient/${patientId}`);
  }

  // Marquer une alerte comme lue
  marquerAlerteLue(alerteId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/alertes/${alerteId}/lue`, {});
  }

  // Récupérer les statistiques du patient
  getStatistiquesPatient(patientId: number): Observable<PatientStatistiques> {
    return this.http.get<PatientStatistiques>(`${this.apiUrl}/statistiques/patient/${patientId}`);
  }

  // Récupérer l'historique complet des consultations
  getHistoriqueConsultationsComplet(patientId: number): Observable<ConsultationPatient[]> {
    return this.http.get<ConsultationPatient[]>(`${this.apiUrl}/consultations/patient/${patientId}/historique-complet`);
  }

  // Récupérer les prescriptions terminées
  getPrescriptionsTerminees(patientId: number): Observable<PrescriptionPatient[]> {
    return this.http.get<PrescriptionPatient[]>(`${this.apiUrl}/prescriptions/patient/${patientId}/terminees`);
  }

  // Récupérer les médicaments à prendre aujourd'hui
  getMedicamentsAujourdhui(patientId: number): Observable<MedicamentPrescritPatient[]> {
    return this.http.get<MedicamentPrescritPatient[]>(`${this.apiUrl}/prescriptions/patient/${patientId}/medicaments-aujourdhui`);
  }

  // Confirmer la prise d'un médicament
  confirmerPriseMedicament(medicamentId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/prescriptions/medicaments/${medicamentId}/confirmer-prise`, {});
  }

  // Demander un rendez-vous
  //On a une interface pour la demande de rendez-vous, mais on ne l'utilise pas ici
  // demanderRendezVous(patientId: number, demande: any): Observable<RendezVous> {
  //   return this.http.post<RendezVous>(`${this.apiUrl}/rendez-vous/demander`, {
  //     patientId: patientId,
  //     ...demande
  //   });
  // }


  // Annuler un rendez-vous
  annulerRendezVousPatient(rendezVousId: number, motif: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/rendez-vous/${rendezVousId}/annuler-patient`, { motif });
  }

  // Reporter un rendez-vous
  reporterRendezVousPatient(rendezVousId: number, nouvelleDate: string, motif: string): Observable<RendezVous> {
    return this.http.put<RendezVous>(`${this.apiUrl}/rendez-vous/${rendezVousId}/reporter-patient`, {
      nouvelleDate: nouvelleDate,
      motif: motif
    });
  }

  // Récupérer les créneaux disponibles
  getCreneauxDisponibles(medecinId: number, date: string): Observable<any[]> {
    const params = new HttpParams().set('date', date);
    return this.http.get<any[]>(`${this.apiUrl}/rendez-vous/creneaux-disponibles/medecin/${medecinId}`, { params });
  }

  // Récupérer la liste des médecins
  getMedecins(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/medecins`);
  }

  // Récupérer les médecins par spécialité
  getMedecinsParSpecialite(specialite: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/medecins/specialite/${specialite}`);
  }

  // Télécharger un document médical
  telechargerDocument(documentId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/documents/${documentId}/telecharger`, { responseType: 'blob' });
  }

  // Récupérer les documents du patient
  getDocumentsPatient(patientId: number): Observable<DocumentMedical[]> {
    return this.http.get<DocumentMedical[]>(`${this.apiUrl}/dossiers-medicaux/patient/${patientId}/documents`);
  }

  // Mettre à jour le profil patient
  mettreAJourProfilPatient(patientId: number, profil: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/patients/${patientId}/profil`, profil);
  }

  // Récupérer les notifications du patient
  getNotificationsPatient(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/notifications/patient/${patientId}`);
  }

  // Marquer une notification comme lue
  marquerNotificationLue(notificationId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/notifications/${notificationId}/lue`, {});
  }

  // Récupérer les rappels de médicaments
  getRappelsMedicaments(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/rappels/medicaments/patient/${patientId}`);
  }

  // Configurer les rappels de médicaments
  configurerRappelsMedicaments(patientId: number, rappels: any[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/rappels/medicaments/patient/${patientId}`, rappels);
  }

  // Récupérer l'historique des symptômes
  getHistoriqueSymptomes(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/symptomes/patient/${patientId}/historique`);
  }

  // Ajouter un symptôme
  ajouterSymptome(patientId: number, symptome: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/symptomes/patient/${patientId}`, symptome);
  }

  // Récupérer les constantes vitales
  getConstantesVitales(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/constantes-vitales/patient/${patientId}`);
  }

  // Ajouter des constantes vitales
  ajouterConstantesVitales(patientId: number, constantes: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/constantes-vitales/patient/${patientId}`, constantes);
  }

  // Récupérer les allergies du patient
  getAllergiesPatient(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/allergies/patient/${patientId}`);
  }

  // Ajouter une allergie
  ajouterAllergie(patientId: number, allergie: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/allergies/patient/${patientId}`, allergie);
  }

  // Récupérer les contacts d'urgence
  getContactsUrgence(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/contacts-urgence/patient/${patientId}`);
  }

  // Mettre à jour les contacts d'urgence
  mettreAJourContactsUrgence(patientId: number, contacts: any[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/contacts-urgence/patient/${patientId}`, contacts);
  }

  // Récupérer les informations de facturation
  getFacturationPatient(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/facturation/patient/${patientId}`);
  }

  // Payer une facture
  payerFacture(factureId: number, methodePaiement: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/facturation/${factureId}/payer`, { methodePaiement });
  }

  // Récupérer les assurances du patient
  getAssurancesPatient(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/assurances/patient/${patientId}`);
  }

  // Mettre à jour les informations d'assurance
  mettreAJourAssurance(patientId: number, assurance: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/assurances/patient/${patientId}`, assurance);
  }
}