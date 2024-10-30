import { CommentService } from './../../services/comment.service';
import { Component, inject, LOCALE_ID } from '@angular/core';
import {VgApiService, VgCoreModule} from '@videogular/ngx-videogular/core';
import {VgControlsModule} from '@videogular/ngx-videogular/controls';
import {VgOverlayPlayModule} from '@videogular/ngx-videogular/overlay-play';
import {VgBufferingModule} from '@videogular/ngx-videogular/buffering';
import { Commentary } from '../../models/commentary';
import { ActivatedRoute } from '@angular/router';
import { Video } from '../../models/video';
import { VideoService } from '../../services/video.service';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../auth/login.service';
import { CommonModule } from '@angular/common';
import { registerLocaleData } from '@angular/common';
import localePT from '@angular/common/locales/pt';
import { User } from '../../models/user';
import Swal from 'sweetalert2'
registerLocaleData(localePT);

@Component({
  selector: 'app-videodetail',
  standalone: true,
  imports: [VgCoreModule,VgControlsModule,VgOverlayPlayModule,VgBufferingModule, FormsModule, CommonModule],
  templateUrl: './videodetail.component.html',
  styleUrl: './videodetail.component.scss',
  providers: [
    {provide: LOCALE_ID, useValue: 'pt-br'}
  ]
})
export class VideodetailComponent {

  preload: string = 'auto';
  api: VgApiService = new VgApiService;
  commentary: Commentary = new Commentary();
  lista: Commentary[] = [];
  router = inject(ActivatedRoute);
  video: Video = new Video();
  videoService = inject(VideoService);
  commentService = inject(CommentService);
  loginService = inject(LoginService);
  user:User = new User;


  constructor(){
    let id = this.router.snapshot.params['id'];
    this.findById(id)
    this.findAllCommentsByVideo(id);
    this.findUsernameByVideoId(id);
  }

  findById(id: string){
    this.videoService.findById(id).subscribe({
      next: video =>{
        this.video = video;
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


  onPlayerReady(source: VgApiService){
    this.api = source;
    console.log("onPlayerReady");
    this.api.getDefaultMedia().subscriptions.loadedMetadata.subscribe(
      this.autoplay.bind(this)
    )
  }

  autoplay(){
    console.log("play")
    this.api.play();
  }

  findAllCommentsByVideo(videoId: string){
    this.commentService.findAllCommentsByVideo(videoId).subscribe({
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

  postComment(){
    let id = this.router.snapshot.params['id'];
    let username = this.loginService.getUsernameFromLocalStorage();
    if(this.loginService.userHasLogin()){
      if(username){
        this.commentService.postComment(username, id, this.commentary.text).subscribe({
          next: comment =>{
            this.lista.push(comment);
            
          },
          error: erro=>{
            alert("this comment is not posted")
          }
        })
      }else{
        alert("username not found for post comment")
      }
    }else{
      Swal.fire("Por favor, faça login para comentar");
    }

  }

  deleteComment(comment: Commentary){

    Swal.fire({
      title: "Deseja deletar este comentario?",
      showCancelButton: true,
      confirmButtonText: "Sim",
      cancelButtonText: "Cancelar"
    
    }).then((result) => {
      /* Read more about isConfirmed, isDenied below */
      if (result.isConfirmed) {
        this.commentService.deleteComment(comment.id.toString()).subscribe({
          next: response =>{
            window.location.reload();
          },
          error: erro=>{
            const errorResponse = JSON.parse(erro.error);
            Swal.fire({
            icon: "error",
            title: "Oops...",
            text: errorResponse.msg,
        });
          }
        })
      }
    });
  }

  findUsernameByVideoId(id:string){
    this.videoService.findUsernameByVideoId(id).subscribe({
      next: response=>{
          this.user.username = response;
      },
      error: erro=>{
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
