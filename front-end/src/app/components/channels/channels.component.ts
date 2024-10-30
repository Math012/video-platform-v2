import { Component, inject } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user';
import { Router } from '@angular/router';
import Swal from 'sweetalert2'
import { LoginService } from '../../auth/login.service';

@Component({
  selector: 'app-channels',
  standalone: true,
  imports: [],
  templateUrl: './channels.component.html',
  styleUrl: './channels.component.scss'
})
export class ChannelsComponent {

  userService = inject(UserService);
  loginService = inject(LoginService);
  users: User[] = [];
  router = inject(Router);

  constructor(){
    this.findAllUsers();
  }

  findAllUsers(){
    this.userService.findAllUsers().subscribe({
      next: lista =>{
        this.users = lista;
      },
      error: erro =>{
        Swal.fire({
          title: "Erro no servidor",
          text: "Por favor, tente novamente",
          icon: "question"
        });
      }
    })
  }

  openUserVideos(user: User){
    this.router.navigate(['admin/videos/by/'+user.username])
  }

  openMainUserVideos(user: User){
    this.router.navigate(['admin/uservideos'])
  }


}
