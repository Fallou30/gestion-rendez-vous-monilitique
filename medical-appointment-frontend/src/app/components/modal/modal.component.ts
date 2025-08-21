// src/app/shared/modal/modal.component.ts
import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="visible" class="modal-overlay" (click)="close()">
      <div class="modal-content" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>{{ title }}</h3>
          <button (click)="close()" class="close-button">&times;</button>
        </div>
        <div class="modal-body">
          <ng-content></ng-content>
        </div>
        <div class="modal-footer" *ngIf="showFooter">
          <button (click)="close()" class="btn btn-secondary">Annuler</button>
          <button (click)="confirm()" class="btn btn-primary">Confirmer</button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./modal.component.scss']
})
export class ModalComponent {
  @Input() title = '';
  @Input() visible = false;
  @Input() showFooter = true;
  @Output() onClose = new EventEmitter();
  @Output() onConfirm = new EventEmitter();

  close() {
    this.onClose.emit();
    this.visible = false;
  }

  confirm() {
    this.onConfirm.emit();
    this.visible = false;
  }
}