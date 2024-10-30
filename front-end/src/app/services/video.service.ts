import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Video } from '../models/video';
import { VideoDTO } from '../models/video-dto';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class VideoService {

  http = inject(HttpClient);
  API = "http://localhost:8080"

  constructor() { }


  findAllVideosRandom(): Observable<Video[]>{
    return this.http.get<Video[]>(this.API+"/videos/pageable?page=0&size=8");
  }

  findAllVideosByUsername(username: string): Observable<Video[]>{
    return this.http.get<Video[]>(this.API+"/videos/user/"+username)
  }

  postVideoByUser(file: File, videoDTO: VideoDTO, username: string, fileThumbnail: File): Observable<Video>{
    const formData = new FormData();
    formData.append('file', file);
    formData.append('videoDTO', JSON.stringify(videoDTO));
    formData.append('thumbnail', fileThumbnail)
    return this.http.post<Video>(this.API+"/api/v2/post/video/"+username,formData);
  }

  findById(id: string): Observable<Video>{
    return this.http.get<Video>(this.API+"/video/detail/"+id);
  }

  deleteVideoById(videoId: string): Observable<string>{
    return this.http.delete<string>(this.API+"/api/v2/video/comment/delete/"+videoId,{responseType: 'text' as 'json'});
  }

  deleteVideoByIdWithoutComment(videoId: string): Observable<string>{
    return this.http.delete<string>(this.API+"/api/v2/video/"+videoId,{responseType: 'text' as 'json'});
  }

  findUsernameByVideoId(id: string): Observable<string>{
    return this.http.get<string>(this.API+"/video/username/videos/"+id,{responseType: 'text' as 'json'});
  }
}
