import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Disponibilite, StatutDisponibilite } from '../models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';


@Injectable({
  providedIn: 'root'
})
export class DisponibiliteService {
  private apiUrl = 'http://localhost:8081/api/v1/disponibilites';

  constructor(private http: HttpClient) {}

  getAllDisponibilites(): Observable<Disponibilite[]> {
    return this.http.get<Disponibilite[]>(this.apiUrl);
  }

  getDisponibiliteById(id: number): Observable<Disponibilite> {
    return this.http.get<Disponibilite>(`${this.apiUrl}/${id}`);
  }
  getDisponibilitesByService(serviceId: number, dateDebut: string, dateFin: string): Observable<Disponibilite[]> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);

    return this.http.get<Disponibilite[]>(`${this.apiUrl}/service/${serviceId}/between`, { params });
  }
  getDisponibilitesByMedecin(idMedecin: number): Observable<Disponibilite[]> {
    return this.http.get<Disponibilite[]>(`${this.apiUrl}/medecin/${idMedecin}`);
  }

  getDisponibilitesByDate(date: string): Observable<Disponibilite[]> {
    return this.http.get<Disponibilite[]>(`${this.apiUrl}/date/${date}`);
  }

  getAvailableDisponibilites(debut: string, fin: string): Observable<Disponibilite[]> {
    return this.http.get<Disponibilite[]>(`${this.apiUrl}/disponibles?debut=${debut}&fin=${fin}`);
  }

  createDisponibilite(disponibilite: Disponibilite): Observable<Disponibilite> {
    return this.http.post<Disponibilite>(this.apiUrl, disponibilite);
  }

  updateDisponibilite(id: number, disponibilite: Disponibilite): Observable<Disponibilite> {
    return this.http.put<Disponibilite>(`${this.apiUrl}/${id}`, disponibilite);
  }

  deleteDisponibilite(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  changeStatutDisponibilite(id: number, statut: StatutDisponibilite): Observable<Disponibilite> {
    return this.http.patch<Disponibilite>(`${this.apiUrl}/${id}/statut?statut=${statut}`, {});
  }

  checkConflit(idMedecin: number, date: string, heureDebut: string, heureFin: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check-conflit?idMedecin=${idMedecin}&date=${date}&heureDebut=${heureDebut}&heureFin=${heureFin}`);
  }
}