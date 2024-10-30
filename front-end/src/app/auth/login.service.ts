import { Login } from './../models/login';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { jwtDecode, JwtPayload } from "jwt-decode";
import { RegisterDTO } from '../models/register-dto';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  http = inject(HttpClient);
  API = "http://localhost:8080/v2/auth";

  constructor() { }


  login(login: Login): Observable<string>{
    return this.http.post<string>(this.API+"/login",login,{responseType: 'text' as 'json'});
  }

  register(register: RegisterDTO): Observable<string>{
    return this.http.post<string>(this.API+"/register", register,{responseType: 'text' as 'json'})
  }

  addTokenLocalStorage(token: string){
    localStorage.setItem('token', token);
  }

  removeTokenFromLocalStorage(){
    localStorage.removeItem('token');
  }

  getToken(){
    return localStorage.getItem('token');
  }

  jwtDecode(){
    let token = this.getToken();
    if (token){
      return jwtDecode<JwtPayload>(token);
    }
    return "";
  }

  addUsernameLocalStorage(username: string){
    return localStorage.setItem('username', username);
  }

  getUsernameFromLocalStorage(){
    return localStorage.getItem('username');
  }

  removerUsernameFromLocalStorage(){
    return localStorage.removeItem('username');
  }

  userHasLogin(){
    return this.jwtDecode() as Login;
  }

}
