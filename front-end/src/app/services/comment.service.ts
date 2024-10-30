import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Commentary } from '../models/commentary';
import { HttpClient } from '@angular/common/http';
import { CommentDTO } from '../models/comment-dto';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  http = inject(HttpClient);
  API = "http://localhost:8080"
  commentDTO: CommentDTO = new CommentDTO()

  constructor() { }

  findAllCommentsByVideo(videoId: string): Observable<Commentary[]>{
    return this.http.get<Commentary[]>(this.API+"/comment/video/"+videoId);
  }

  postComment(username: string, videoId: string, comment: string): Observable<Commentary>{
    this.commentDTO.textContent = comment;
    return this.http.post<Commentary>(this.API+"/api/v2/video/comment/"+username+"/"+videoId,this.commentDTO)
  }

  deleteComment(videoId: string):Observable<string>{
    return this.http.delete<string>(this.API+"/api/v2/video/comment/"+videoId,{responseType: 'text' as 'json'})
  }
}
