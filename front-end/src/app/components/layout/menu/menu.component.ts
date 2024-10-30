import { Component, inject } from '@angular/core';
import { LoginService } from '../../../auth/login.service';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user';
import { Router } from '@angular/router';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent {

  loginService = inject(LoginService);
  userService = inject(UserService);
  user: User = new User();
  router = inject(Router)


  constructor(){
    this.findUserByUsername();

  }

  findUserByUsername(){
    let username = this.loginService.getUsernameFromLocalStorage();
    if(username){
      this.userService.findUserByUsername(username).subscribe({
        next: user =>{
          this.user = user;
        },
        error: erro =>{
          Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Não foi possivel carregar as informações de usuario",
          });
        }
      })
    }
  }
  logout(){
    this.router.navigate(['/login'])
  }

}
