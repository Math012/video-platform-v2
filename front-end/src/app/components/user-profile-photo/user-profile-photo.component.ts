import { Component, EventEmitter, Output } from '@angular/core';
import { Profilephoto } from '../../models/profilephoto';

@Component({
  selector: 'app-user-profile-photo',
  standalone: true,
  imports: [],
  templateUrl: './user-profile-photo.component.html',
  styleUrl: './user-profile-photo.component.scss'
})
export class UserProfilePhotoComponent {

  selectedFile: File | null = null;
  profilePhoto: Profilephoto = new Profilephoto();
  @Output("profilePhotoRetorno") profilePhotoRetorno = new EventEmitter<any>()

  onFileSelected(event: any){
    this.profilePhoto.file = event.target.files[0];
  }

  postProfilePhotoUser(){
    this.profilePhotoRetorno.emit(this.profilePhoto);
  }

}
