import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  user = { login: '', password: '' };
  isLoginMode = true;

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    if (!this.user.login || !this.user.password) {
      alert("Пожалуйста, заполните все поля");
      return;
    }

    const url = this.isLoginMode ? '/api/auth/login' : '/api/auth/register';

    this.http.post<any>(url, this.user).subscribe({
      next: (response) => {
        if (this.isLoginMode) {
          localStorage.setItem('authToken', response.token);
          localStorage.setItem('currentUser', this.user.login);

          this.router.navigate(['/main']);
        } else {
          alert("Регистрация успешна! Теперь вы можете войти.");
          this.isLoginMode = true;
          this.user.password = '';
        }
      },
      error: (err) => {
        console.error(err);
        const errorMessage = err.error || (this.isLoginMode ? "Ошибка входа" : "Ошибка регистрации");
        alert(typeof errorMessage === 'string' ? errorMessage : "Ошибка связи с сервером");
      }
    });
  }
}
