import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
  importProvidersFrom
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {
  provideClientHydration,
  withEventReplay
} from '@angular/platform-browser';
import {
  provideHttpClient,
  withInterceptorsFromDi
} from '@angular/common/http';
import { FormsModule } from '@angular/forms';

export const appConfig: ApplicationConfig = {
  providers: [
    // ✅ Global error listeners and zoneless mode (optional but modern)
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),

    // ✅ Core features
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    importProvidersFrom(FormsModule),
    provideClientHydration(withEventReplay())
  ]
};
