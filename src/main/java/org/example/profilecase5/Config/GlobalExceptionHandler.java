package org.example.profilecase5.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private Environment env;

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, HttpServletRequest request, Model model) {
        // Thông điệp ngắn
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : ex.toString();

        // Chỉ show stacktrace khi active profile chứa 'dev'
        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

        String exceptionDetails;
        if (isDev) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            exceptionDetails = sw.toString();
        } else {
            exceptionDetails = "Stack trace is hidden. Enable 'dev' profile to see details.";
        }

        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("exceptionDetails", exceptionDetails);
        model.addAttribute("requestUri", request.getRequestURI());
        model.addAttribute("exceptionClass", ex.getClass().getName());

        // trả về template src/main/resources/templates/error.html
        return "error";
    }
}
