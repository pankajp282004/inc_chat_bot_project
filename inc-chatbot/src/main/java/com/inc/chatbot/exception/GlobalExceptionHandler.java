package com.inc.chatbot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoSuchElementException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorCode", "404");
        mav.addObject("errorTitle", "Not Found");
        mav.addObject("errorMessage", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorCode", "403");
        mav.addObject("errorTitle", "Access Denied");
        mav.addObject("errorMessage", "You do not have permission to access this resource.");
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorCode", "400");
        mav.addObject("errorTitle", "Bad Request");
        mav.addObject("errorMessage", ex.getMessage());
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorCode", "500");
        mav.addObject("errorTitle", "Internal Server Error");
        mav.addObject("errorMessage", "An unexpected error occurred. Please try again.");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
