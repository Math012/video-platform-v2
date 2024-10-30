import { Component, inject, TemplateRef, ViewChild } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user';
import { LoginService } from '../../auth/login.service';
import { MdbModalModule, MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { UserDescrptionComponent } from '../user-descrption/user-descrption.component';
import { DescriptionDTO } from '../../models/description-dto';
import { UserProfilePhotoComponent } from '../user-profile-photo/user-profile-photo.component';
import { Profilephoto } from '../../models/profilephoto';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [MdbModalModule, UserDescrptionComponent, UserProfilePhotoComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {

  modalService = inject(MdbModalService);
  @ViewChild("modalUserProfile") modalUserProfile!: TemplateRef<any>;
  @ViewChild("modalUserDescription") modalUserDescription!: TemplateRef<any>;
  modalRef!: MdbModalRef<any>;

  userService = inject(UserService);
  loginService = inject(LoginService);
  user: User = new User();



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

  profilePhotoRetorno(profilePhoto: Profilephoto){
    let username = this.loginService.getUsernameFromLocalStorage();
    if(username != null){
      this.userService.uploadPhotoProfile(username, profilePhoto.file).subscribe({
        next: response =>{
          window.location.reload();
          
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

  descriptionRetorno(user: DescriptionDTO){
    let username = this.loginService.getUsernameFromLocalStorage();
    if(username){
      this.userService.changeTheUserDescription(username, user.description).subscribe({
        next: response =>{
          console.log(response)
          window.location.reload();
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
    this.modalRef.close();
  }

  openModalProfile(){
    this.modalRef = this.modalService.open(this.modalUserProfile)
  }

  openModalDescription(){
    this.modalRef = this.modalService.open(this.modalUserDescription)
  }
}
