import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly message = signal('');
  private timeout?: ReturnType<typeof setTimeout>;

  afficher(message: string): void {
    if (this.timeout) clearTimeout(this.timeout);
    this.message.set(message);
    this.timeout = setTimeout(() => {
      this.message.set('');
      this.timeout = undefined;
    }, 2800);
  }
}
