// src/app/core/services/file-upload.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from './auth.service';

export interface FileUploadResponse {
  success: boolean;
  fileName: string;
  filePath: string;
  fileUrl: string;
  fileSize: number;
  message?: string;
}

export interface FileUploadProgress {
  progress: number;
  loaded: number;
  total: number;
}

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private readonly API_URL = 'http://localhost:8081/api/v1';
  private uploadProgress = new BehaviorSubject<FileUploadProgress>({ progress: 0, loaded: 0, total: 0 });

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  /**
   * Upload un fichier avec suivi de progression
   */
  uploadFile(file: File, type: 'diplome' | 'carteOrdre' | 'cv' | 'document'): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);

    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken() || ''
    });

    return this.http.post<FileUploadResponse>(`${this.API_URL}/files/upload`, formData, {
      headers,
      reportProgress: true,
      observe: 'events'
    }).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            if (event.total) {
              const progress = Math.round((event.loaded / event.total) * 100);
              this.uploadProgress.next({
                progress,
                loaded: event.loaded,
                total: event.total
              });
            }
            return { progress: true };
          case HttpEventType.Response:
            this.uploadProgress.next({ progress: 100, loaded: event.body.fileSize, total: event.body.fileSize });
            return event.body;
          default:
            return { progress: true };
        }
      })
    );
  }

  /**
   * Upload multiple fichiers
   */
  uploadMultipleFiles(files: File[], type: string): Observable<FileUploadResponse[]> {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    formData.append('type', type);

    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken() || ''
    });

    return this.http.post<FileUploadResponse[]>(`${this.API_URL}/files/upload-multiple`, formData, {
      headers,
      reportProgress: true,
      observe: 'events'
    }).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            if (event.total) {
              const progress = Math.round((event.loaded / event.total) * 100);
              this.uploadProgress.next({
                progress,
                loaded: event.loaded,
                total: event.total
              });
            }
            return [];
          case HttpEventType.Response:
            return event.body;
          default:
            return [];
        }
      })
    );
  }

  /**
   * Télécharger un fichier
   */
  downloadFile(filePath: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken() || ''
    });

    return this.http.get(`${this.API_URL}/files/download`, {
      headers,
      params: { filePath },
      responseType: 'blob'
    });
  }

  /**
   * Obtenir l'URL de prévisualisation d'un fichier
   */
  getFilePreviewUrl(filePath: string): string {
    const token = this.authService.getToken();
    return `${this.API_URL}/files/preview?filePath=${encodeURIComponent(filePath)}&token=${token}`;
  }

  /**
   * Supprimer un fichier
   */
  deleteFile(filePath: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken() || ''
    });

    return this.http.delete(`${this.API_URL}/files/delete`, {
      headers,
      params: { filePath }
    });
  }

  /**
   * Obtenir la progression d'upload
   */
  getUploadProgress(): Observable<FileUploadProgress> {
    return this.uploadProgress.asObservable();
  }

  /**
   * Valider un fichier avant upload
   */
  validateFile(file: File): { valid: boolean; message?: string } {
    const maxSize = 5 * 1024 * 1024; // 5MB
    const allowedTypes = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png'];

    if (file.size > maxSize) {
      return { valid: false, message: 'Le fichier ne peut pas dépasser 5MB' };
    }

    if (!allowedTypes.includes(file.type)) {
      return { valid: false, message: 'Type de fichier non autorisé. Utilisez PDF, JPG ou PNG' };
    }

    return { valid: true };
  }
  /**
 * Upload un document lié à un médecin (CV, diplôme, carte d'ordre)
 */
uploadMedecinDocument(medecinId: number, file: File, type: 'cv' | 'diplome' | 'carteOrdre'): Observable<FileUploadResponse> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('type', type);

  const headers = new HttpHeaders({
    'Authorization': this.authService.getToken() || ''
  });

  return this.http.post<FileUploadResponse>(`${this.API_URL}/files/medecin/${medecinId}/documents`, formData, {
    headers,
    reportProgress: true,
    observe: 'events'
  }).pipe(
    map((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          if (event.total) {
            const progress = Math.round((event.loaded / event.total) * 100);
            this.uploadProgress.next({
              progress,
              loaded: event.loaded,
              total: event.total
            });
          }
          return { progress: true };
        case HttpEventType.Response:
          this.uploadProgress.next({
            progress: 100,
            loaded: event.body.fileSize,
            total: event.body.fileSize
          });
          return event.body;
        default:
          return { progress: true };
      }
    })
  );
}

}