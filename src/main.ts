// ✅ CRITICAL: Add Zone.js import at the very top
import 'zone.js';

import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { importProvidersFrom } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { App } from './app/app';
import { routes } from './app/app.routes';

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),  // ✅ Add router
    provideHttpClient(withInterceptorsFromDi()),
    importProvidersFrom(FormsModule)
  ]
}).catch(err => console.error(err));
