package com.math012.social.video.videoplatformv2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomHandlerException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StructException> handlerUserNotFoundException(Exception e, WebRequest request){
        var exception = new StructException(new Date(),e.getMessage(),request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VideoStorageException.class)
    public ResponseEntity<StructException> handlerFileStorageException(Exception e, WebRequest request){
        var exception = new StructException(new Date(),e.getMessage(),request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<StructException> handlerVideoNotFoundException(Exception e, WebRequest request){
        var exception = new StructException(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenProviderException.class)
    public ResponseEntity<StructException> handlerTokenProviderException(Exception e, WebRequest request){
        var exception = new StructException(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityConfigException.class)
    public ResponseEntity<StructException> handlerSecurityConfigException(Exception e, WebRequest request){
        var exception = new StructException(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityAuthenticationException.class)
    public ResponseEntity<StructException> handlerSecurityAuthenticationException(Exception e, WebRequest request){
        var exception = new StructException(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegisterUserException.class)
    public ResponseEntity<StructException> handlerRegisterUserException(Exception e, WebRequest request){
        var exception = new StructException(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoadVideoException.class)
    public ResponseEntity<StructException> handlerLoadVideoException(Exception e, WebRequest request){
        var exception = new StructException(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }
}
