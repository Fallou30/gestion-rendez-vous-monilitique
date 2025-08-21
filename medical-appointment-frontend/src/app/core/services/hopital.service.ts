import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StatutHopital, Hopital, HopitalRequest } from '../models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';

@Injectable({
  providedIn: 'root'
})
export class HopitalService {
  private apiUrl = 'http://localhost:8081/api/v1/hopitaux';

  constructor(private http: HttpClient) {}

  getAllHopitaux(): Observable<Hopital[]> {
    return this.http.get<Hopital[]>(this.apiUrl);
  }

  getHopitalById(id: number): Observable<Hopital> {
    return this.http.get<Hopital>(`${this.apiUrl}/${id}`);
  }

  getHopitauxByStatut(statut: StatutHopital): Observable<Hopital[]> {
    return this.http.get<Hopital[]>(`${this.apiUrl}/statut/${statut}`);
  }

  getHopitauxByVille(ville: string): Observable<Hopital[]> {
    const params = new HttpParams().set('ville', ville);
    return this.http.get<Hopital[]>(`${this.apiUrl}/ville`, { params });
  }

  getHopitauxByRegion(region: string): Observable<Hopital[]> {
    const params = new HttpParams().set('region', region);
    return this.http.get<Hopital[]>(`${this.apiUrl}/region`, { params });
  }

  searchHopitauxByNom(nom: string): Observable<Hopital[]> {
    const params = new HttpParams().set('nom', nom);
    return this.http.get<Hopital[]>(`${this.apiUrl}/search`, { params });
  }

  createHopital(hopital: HopitalRequest): Observable<Hopital> {
    return this.http.post<Hopital>(this.apiUrl, hopital);
  }

  updateHopital(id: number, hopital: HopitalRequest): Observable<Hopital> {
    return this.http.put<Hopital>(`${this.apiUrl}/${id}`, hopital);
  }

  deleteHopital(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  changeStatutHopital(id: number, statut: StatutHopital): Observable<Hopital> {
    return this.http.patch<Hopital>(`${this.apiUrl}/${id}/statut`, { statut });
  }
}