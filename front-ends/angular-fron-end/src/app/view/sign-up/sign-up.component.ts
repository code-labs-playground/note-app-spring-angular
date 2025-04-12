import { Component } from '@angular/core';

@Component({
  selector: 'app-sign-up',
  standalone: false,
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.scss'
})
export class SignUpComponent {
  user = {
    fullName: '',
    email: '',
    password: '',
    confirmPassword: ''
  }
  value = 'ijse';

}
