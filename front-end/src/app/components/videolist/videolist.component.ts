import { Component, inject } from '@angular/core';
import { Video } from '../../models/video';
import { Router } from '@angular/router';
import { VideoService } from '../../services/video.service';
import { LoginService } from '../../auth/login.service';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-videolist',
  standalone: true,
  imports: [],
  templateUrl: './videolist.component.html',
  styleUrl: './videolist.component.scss'
})
export class VideolistComponent {

  router = inject(Router);
  lista: Video[] = [];
  videoService = inject(VideoService);


  constructor(){
    this.findAllVideosRandom();

  }

  findAllVideosRandom(){
    this.videoService.findAllVideosRandom().subscribe({
      next: lista =>{
        this.lista = lista;
      },
      error: erro =>{
       
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: "Erro no servidor, por favor tente novamente",
        });
      }
    })
  }

  openVideo(video: Video){
    this.router.navigate(['admin/videos/' + video.id]);
  }


}
