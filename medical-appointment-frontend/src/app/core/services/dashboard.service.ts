
// dashboard.service.ts - Service principal pour orchestrer le dashboard
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { Examen, ExamenService } from './examen.service';
import { RendezVousService } from './rendez-vous.service';
import { StatistiqueService } from './statistiques.service';

export interface DashboardData {
  rendezVousDuJour: any[];
  rendezVousUrgents: any[];
  examensEnAttente: Examen[];
  examensUrgents: Examen[];
  statistiques: any;
}
export interface DashboardMedecinStatDto {
     consultationsJour: number;
     dureeMoyenne: number;
     tauxSatisfaction: number;
}
@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8081/api/v1/statistiques';

  constructor(
    private rendezVousService: RendezVousService,
    private examenService: ExamenService,
    private statistiquesService: StatistiqueService,
    private http: HttpClient
  ) {}

  getDashboardData(medecinId: number): Observable<DashboardData> {
    const today = new Date();
    const dateDebut = new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0];
    const dateFin = today.toISOString().split('T')[0];

    return forkJoin({
      rendezVousDuJour: this.rendezVousService.getRendezVousDuJour(),
      rendezVousUrgents: this.rendezVousService.getRendezVousUrgents(),
      examensEnAttente: this.examenService.getExamensEnAttente(),
      examensUrgents: this.examenService.getExamensUrgents(),
      statistiques: this.statistiquesService.getStatistiquesConsultationsMedecin(medecinId, dateDebut, dateFin)
    }).pipe(
      map(data => ({
        rendezVousDuJour: data.rendezVousDuJour,
        rendezVousUrgents: data.rendezVousUrgents,
        examensEnAttente: data.examensEnAttente,
        examensUrgents: data.examensUrgents,
        statistiques: data.statistiques
      }))
    );
  }

  getRendezVousByMedecin(medecinId: number): Observable<any[]> {
    return this.rendezVousService.getRendezVousByMedecin(medecinId);
  }
  getStatsGlobales(medecinId: number): Observable<DashboardMedecinStatDto> {
   return this.http.get<DashboardMedecinStatDto>(`/dashboard/medecin/${medecinId}`);
  }

}