import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { LoginService } from './login.service';
import Swal from 'sweetalert2'

export const loginGuard: CanActivateFn = (route, state) => {
  let loginService = inject(LoginService);
  let router = inject(Router)

  if(!loginService.userHasLogin() && state.url == '/admin/uservideos'){
    Swal.fire({
      icon: "error",
      title: "Oops...",
      text: "Por favor, faça login para acessar este conteudo!",
    });
    router.navigate(['/login'])
    return false;
  }

  if(!loginService.userHasLogin() && state.url == '/admin/profile'){
    Swal.fire({
      icon: "error",
      title: "Oops...",
      text: "Por favor, faça login para acessar este conteudo!",
    });
    router.navigate(['/login'])
    return false;
  }

  if(!loginService.userHasLogin() && state.url == '/admin/managevideos'){
    Swal.fire({
      icon: "error",
      title: "Oops...",
      text: "Por favor, faça login para acessar este conteudo!",
    });
    router.navigate(['/login'])
    return false;
  }

  return true;
};
