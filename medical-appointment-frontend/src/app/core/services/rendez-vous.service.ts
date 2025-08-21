import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RendezVousRequest, RendezVous } from '../models/service-hopital-rdv-disponibite/service-hopital-rdv-disponibite.module';


@Injectable({
  providedIn: 'root'
})
export class RendezVousService {
  private apiUrl = 'http://localhost:8081/api/v1/rendez-vous';

  constructor(private http: HttpClient) {}

  getAllRendezVous(): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(this.apiUrl);
  }

  getRendezVousById(id: number): Observable<RendezVous> {
    return this.http.get<RendezVous>(`${this.apiUrl}/${id}`);
  }
  
  updateRendezVous(id: number, start: Date, end: Date): Observable<RendezVous> {
  return this.http.patch<RendezVous>(`${this.apiUrl}/${id}`, {
    dateHeure: start.toISOString(),
    dureePrevue: (end.getTime() - start.getTime()) / 60000
  });
}
getDisponibilitesParMedecinEtDate(idMedecin: number, date: string): Observable<any[]> {
  return this.http.get<any[]>(
    `${this.apiUrl}/disponibilites/search?idMedecin=${idMedecin}&date=${date}&statut=DISPONIBLE`
  );
}

  getRendezVousByPatient(idPatient: number): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.apiUrl}/patient/${idPatient}`);
  }

  getRendezVousByMedecin(idMedecin: number): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.apiUrl}/medecin/${idMedecin}`);
  }

  getRendezVousDuJour(): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.apiUrl}/aujourd-hui`);
  }

  getRendezVousUrgents(): Observable<RendezVous[]> {
    return this.http.get<RendezVous[]>(`${this.apiUrl}/urgents`);
  }

  // createRendezVous(rendezVous: RendezVous): Observable<RendezVous> {
  //   return this.http.post<RendezVous>(this.apiUrl, rendezVous);
  // }
createRendezVous(rdvData: RendezVousRequest): Observable<RendezVous> {
  return this.http.post<RendezVous>(this.apiUrl, rdvData);
}
  updateRendezVousParRv(id: number, rendezVous: RendezVous): Observable<RendezVous> {
    return this.http.put<RendezVous>(`${this.apiUrl}/${id}`, rendezVous);
  }

  deleteRendezVous(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  confirmerRendezVous(id: number): Observable<RendezVous> {
    return this.http.patch<RendezVous>(`${this.apiUrl}/${id}/confirmer`, {});
  }

  annulerRendezVous(id: number): Observable<RendezVous> {
    return this.http.patch<RendezVous>(`${this.apiUrl}/${id}/annuler`, {});
  }

  reporterRendezVous(id: number, nouvelleDate: string): Observable<RendezVous> {
    return this.http.patch<RendezVous>(`${this.apiUrl}/${id}/reporter`, nouvelleDate);
  }

  commencerConsultation(id: number): Observable<RendezVous> {
    return this.http.patch<RendezVous>(`${this.apiUrl}/${id}/commencer`, {});
  }

  terminerConsultation(id: number): Observable<RendezVous> {
    return this.http.patch<RendezVous>(`${this.apiUrl}/${id}/terminer`, {});
  }
  getRendezVousByService(idService: number): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/service/${idService}`);
}
getRendezVousByHopital(idHopital: number): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/hopital/${idHopital}`);
}
getRendezVousByStatut(statut: string): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/statut/${statut}`);
}
getRendezVousByUrgence(niveauUrgence: string): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/urgence/${niveauUrgence}`);
}
getRendezVousBetweenDates(dateDebut: string, dateFin: string): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/periode`, {
    params: {
      dateDebut,
      dateFin
    }
  });
}
getRendezVousByMedecinAndPeriode(idMedecin: number, dateDebut: string, dateFin: string): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/medecin/${idMedecin}/periode`, {
    params: {
      dateDebut,
      dateFin
    }
  });
}
getUpcomingRendezVousByPatient(idPatient: number): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/patient/${idPatient}/prochains`);
}
getRendezVousEnRetard(): Observable<RendezVous[]> {
  return this.http.get<RendezVous[]>(`${this.apiUrl}/en-retard`);
}
countRendezVousByMedecin(idMedecin: number, dateDebut: string, dateFin: string): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/medecin/${idMedecin}/count`, {
    params: {
      dateDebut,
      dateFin
    }
  });
}
changeStatutRendezVous(id: number, statut: string): Observable<RendezVous> {
  return this.http.patch<RendezVous>(`${this.apiUrl}/${id}/statut`, statut);
}

}