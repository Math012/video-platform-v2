import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Video } from '../../models/video';
import { UserService } from '../../services/user.service';
import Swal from 'sweetalert2'
@Component({
  selector: 'app-uservideopage',
  standalone: true,
  imports: [],
  templateUrl: './uservideopage.component.html',
  styleUrl: './uservideopage.component.scss'
})
export class UservideopageComponent {
  router = inject(ActivatedRoute);
  lista: Video[] = [];
  usernameTitle: string = "";
  userService = inject(UserService);
  routerNavigate = inject(Router);

  constructor(){
    let username = this.router.snapshot.params['username'];
    this.usernameTitle = username;
    this.findAllVideosByUsername(username);
  }

  findAllVideosByUsername(username: string){
    this.userService.findAllVideosByUsername(username).subscribe({
      next: lista =>{
        this.lista = lista;
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

  openVideo(video: Video){
    this.routerNavigate.navigate(['admin/videos/' + video.id]);
  }

}
