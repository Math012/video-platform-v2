import { Component, inject } from '@angular/core';
import { VideoService } from '../../services/video.service';
import { Video } from '../../models/video';
import { LoginService } from '../../auth/login.service';
import { Commentary } from '../../models/commentary';
import { CommentService } from '../../services/comment.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-manage-user-videos',
  standalone: true,
  imports: [],
  templateUrl: './manage-user-videos.component.html',
  styleUrl: './manage-user-videos.component.scss'
})
export class ManageUserVideosComponent {


  videoService = inject(VideoService);
  loginService = inject(LoginService);
  commentService = inject(CommentService);
  router = inject(Router);
  videos: Video[] = [];
  comments: Commentary[] = [];

  constructor(){
    this.findAllVideosByUsername();
  }


  findAllVideosByUsername(){
    let username = this.loginService.getUsernameFromLocalStorage();
    if(username != null){
      this.videoService.findAllVideosByUsername(username).subscribe({
        next: videos =>{
          this.videos = videos;
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
    }else{
      Swal.fire("username not found");
    }
  }


  deleteVideo(video:Video){

    Swal.fire({
      title: "Deseja deletar este vídeo?",
      showCancelButton: true,
      confirmButtonText: "Sim",
      cancelButtonText: "Cancelar"
    
    }).then((result) => {
      if (result.isConfirmed) {
        this.commentService.findAllCommentsByVideo(video.id.toString()).subscribe({
          next: lista =>{
            this.comments = lista;
            if (this.comments.length > 0) {
              this.deleteVideoWithComments(video.id.toString());
            }else {
              this.deleteVideoWithoutComments(video.id.toString());
            }
        },
        error: erro=>{
          const errorResponse = JSON.parse(erro.error);
          Swal.fire({
            icon: "error",
            title: "Oops...",
            text: errorResponse.msg,
          });
  
        }
      });
      }
    });
  }

  deleteVideoWithoutComments(videoId: string) {
    this.videoService.deleteVideoByIdWithoutComment(videoId).subscribe({
      next: response => {
        window.location.reload(); 
      },
      error: erro => {
        const errorResponse = JSON.parse(erro.error);
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: errorResponse.msg,
        });
      }
    });
  }

  deleteVideoWithComments(videoId: string) {
    this.videoService.deleteVideoById(videoId).subscribe({
      next: response => {
        window.location.reload();
      },
      error: erro => {
        const errorResponse = JSON.parse(erro.error);
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: errorResponse.msg,
        });
      }
    });
  }
}
