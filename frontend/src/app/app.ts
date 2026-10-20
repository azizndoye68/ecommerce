import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './shared/header/header';
import { Footer } from './shared/footer/footer';
import { Store } from '@ngrx/store';
import { AuthActions } from './store/auth/auth.actions';
import { NotificationService } from './core/services/notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('frontend');
  private store = inject(Store);
  readonly notification = inject(NotificationService);

  ngOnInit(): void {
    this.store.dispatch(AuthActions.restaurerSession());
  }
}
