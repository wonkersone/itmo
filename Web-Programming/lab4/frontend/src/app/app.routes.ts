import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { MainComponent } from './components/main/main';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  {
    path: 'main',
    component: MainComponent,
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '' }
];
