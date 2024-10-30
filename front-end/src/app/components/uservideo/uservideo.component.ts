import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
import { MdbModalModule, MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { PostvideoComponent } from '../postvideo/postvideo.component';
import { Video } from '../../models/video';
import { VideoService } from '../../services/video.service';
import { LoginService } from '../../auth/login.service';
import { Router } from '@angular/router';
import { VideoDTO } from '../../models/video-dto';
import { Commentary } from '../../models/commentary';
import { CommentService } from '../../services/comment.service';
import { User } from '../../models/user';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-uservideo',
  standalone: true,
  imports: [MdbModalModule, PostvideoComponent],
  templateUrl: './uservideo.component.html',
  styleUrl: './uservideo.component.scss'
})
export class UservideoComponent {

  modalService = inject(MdbModalService);
  @ViewChild("modalUserVideo") modalUserVideo!: TemplateRef<any>;
  modalRef!: MdbModalRef<any>;
  lista: Video[] = [];
  comments: Commentary[] = [];
  videoDTO: VideoDTO = new VideoDTO();
  videoService = inject(VideoService);
  loginService = inject(LoginService);
  commentService = inject(CommentService);
  router = inject(Router);
  video:Video = new Video;
  selectedVideo: any;


  constructor(){
    this.findAllVideosByUsername();
  }


  postVideo(){
    this.modalRef = this.modalService.open(this.modalUserVideo)

  }

  videoRetorno(video: Video){
    this.videoDTO.title = video.title;
    this.videoDTO.description = video.description;
    let username = this.loginService.getUsernameFromLocalStorage();
    if(username != null){
      this.videoService.postVideoByUser(video.file, this.videoDTO, username,video.fileThumbnail).subscribe({
        next: video =>{
          this.lista.push(video);
        }, error: erro =>{
          Swal.fire({
          icon: "error",
          title: "Oops...",
          text: erro.error.msg,
        });
        }
      })
    }else{
      alert('username not found')
    }

    this.modalRef.close();

  }


  findAllVideosByUsername(){
    let username = this.loginService.getUsernameFromLocalStorage();
    if(username != null){
      this.videoService.findAllVideosByUsername(username).subscribe({
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
    }else{
      alert('username not found')
    }
  }

  openVideo(video: Video){
    this.router.navigate(['admin/videos/' + video.id]);
  }
  
  


  findAllComments(){
    alert("Lista depois da exclusao: " + this.comments.length)
  }

 


}
