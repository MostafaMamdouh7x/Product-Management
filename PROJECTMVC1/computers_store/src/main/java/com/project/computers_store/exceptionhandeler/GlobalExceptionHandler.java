package com.project.computers_store.exceptionhandeler;

import com.project.computers_store.exception.ResourceNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model){
        model.addAttribute("status", 404);
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "products/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAll(Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("message", "server Error");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "products/error";
    }
}
