// notification.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  showSuccess(message: string): void {
    alert('Succès: ' + message); // Remplacez par Toast ou autre
  }

  showError(message: string): void {
    alert('Erreur: ' + message); // Remplacez par Toast ou autre
  }
}