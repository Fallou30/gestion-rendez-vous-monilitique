import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Service, StatutService } from '../models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';

@Injectable({
  providedIn: 'root'
})
export class ServiceService {
  private apiUrl = 'http://localhost:8081/api/v1/services';

  constructor(private http: HttpClient) {}

  getAllServices(): Observable<Service[]> {
    return this.http.get<Service[]>(this.apiUrl);
  }

  getServiceById(id: number): Observable<Service> {
    return this.http.get<Service>(`${this.apiUrl}/${id}`);
  }

  getServicesByHopital(idHopital: number): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.apiUrl}/hopital/${idHopital}`);
  }

  getServicesByStatut(statut: StatutService): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.apiUrl}/statut/${statut}`);
  }

  searchServicesByNom(nom: string): Observable<Service[]> {
    const params = new HttpParams().set('nom', nom);
    return this.http.get<Service[]>(`${this.apiUrl}/search`, { params });
  }

  createService(service: Service): Observable<Service> {
    return this.http.post<Service>(this.apiUrl, service);
  }

  updateService(id: number, service: Service): Observable<Service> {
    return this.http.put<Service>(`${this.apiUrl}/${id}`, service);
  }

  deleteService(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  changeStatutService(id: number, statut: StatutService): Observable<Service> {
    return this.http.patch<Service>(`${this.apiUrl}/${id}/statut`, { statut });
  }
}
