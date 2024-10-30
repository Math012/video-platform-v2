import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { MdbTabsModule } from 'mdb-angular-ui-kit/tabs';
import { MenuComponent } from '../menu/menu.component';
import { LoginService } from '../../../auth/login.service';
import { Login } from '../../../models/login';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2'
import { RegisterDTO } from '../../../models/register-dto';
import { CustomHandlerException } from '../../../models/custom-handler-exception';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule,MdbFormsModule, MdbTabsModule, MenuComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  router = inject(Router);
  loginService = inject(LoginService);
  login: Login = new Login()
  registerDTO: RegisterDTO = new RegisterDTO()
  customHandlerException: CustomHandlerException = new CustomHandlerException();

  constructor(){
    this.loginService.removeTokenFromLocalStorage();
    this.loginService.removerUsernameFromLocalStorage();
  }

  loginUser(){
    this.loginService.login(this.login).subscribe({
      next: token =>{
        if(token){
          this.loginService.addTokenLocalStorage(token);
          this.loginService.addUsernameLocalStorage(this.login.username);
          this.router.navigate(['admin/videos']);
        }
      }, error: erro =>{
        const errorResponse = JSON.parse(erro.error);
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: errorResponse.msg,
        });
      }
    })
  }

  registerUser(){
    this.loginService.register(this.registerDTO).subscribe({
      next: response=>{
        Swal.fire({
          title: "Conta criada com sucesso!",
          confirmButtonText: "Ok",
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.reload();
          }
        });
       
      },
      error: erro =>{
        const errorResponse = JSON.parse(erro.error);
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: errorResponse.msg,
        });
      }
    })
  }
}
