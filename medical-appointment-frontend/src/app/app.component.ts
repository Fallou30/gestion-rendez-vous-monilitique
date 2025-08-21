import { formatDate } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'medical-appointment-frontend';
   dateFormatted: string;
   constructor() {
    const now = new Date();
    this.dateFormatted = formatDate(now, 'fullDate', 'fr-FR');
  }
}
