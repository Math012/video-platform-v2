import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user';
import { Video } from '../models/video';
import { DescriptionDTO } from '../models/description-dto';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  http = inject(HttpClient);
  API = "http://localhost:8080"
  descriptionDTO : DescriptionDTO = new DescriptionDTO();

  constructor() { }

  

  findAllUsers(): Observable<User[]>{
    return this.http.get<User[]>(this.API+"/all/channels");
  }

  findAllVideosByUsername(username: string): Observable<Video[]>{
    return this.http.get<Video[]>(this.API+"/all/videos/"+username);
  }

  findUserByUsername(username: string): Observable<User>{
    return this.http.get<User>(this.API+"/user/info/"+username);
  }

  changeTheUserDescription(username: string, description: string): Observable<string>{
    this.descriptionDTO.description = description;
    return this.http.post<string>(this.API + "/change/description/user/" + username, this.descriptionDTO,{responseType: 'text' as 'json'});
  }

  uploadPhotoProfile(username: string, file: File): Observable<User>{
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<User>(this.API+"/api/v2/post/photo/profile/"+username, formData);
  }

}
