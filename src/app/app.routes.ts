import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { AdminDashboard } from './admin-dashboard/admin-dashboard';
import { HostDashboard } from './host-dashboard/host-dashboard';
import { StudentDashboard } from './student-dashboard/student-dashboard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'admin', component: AdminDashboard },
  { path: 'host', component: HostDashboard },
  { path: 'student', component: StudentDashboard },
];