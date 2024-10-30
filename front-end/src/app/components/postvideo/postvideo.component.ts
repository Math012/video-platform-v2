import { Component, EventEmitter, Output } from '@angular/core';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { Video } from '../../models/video';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-postvideo',
  standalone: true,
  imports: [MdbFormsModule, FormsModule],
  templateUrl: './postvideo.component.html',
  styleUrl: './postvideo.component.scss'
})
export class PostvideoComponent {


  selectedFile: File | null = null;
  selectedFileThumbnail: File | null = null;
  video: Video = new Video();
  @Output("videoRetorno") videoRetorno = new EventEmitter<any>();


  postVideoUser(){
    this.videoRetorno.emit(this.video)
  }

  onFileSelected(event: any){
    this.video.file = event.target.files[0];
  }

  onFileSelectedThumbnail(event: any){
    this.video.fileThumbnail = event.target.files[0];
  }

}
