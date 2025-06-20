package org.example.controller;

import org.example.dto.GoogleSignupRequest;
import org.example.dto.GoogleTokenRequest;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Login - MedShare");
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("pageTitle", "Cadastro - MedShare");
        return "signup";
    }

    @PostMapping("/api/auth/google-login")
    @ResponseBody
    public ResponseEntity<?> googleLogin(@RequestBody GoogleTokenRequest request) {
        try {
            Map<String, Object> response = authService.authenticateWithGoogle(request.getToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/auth/google-signup")
    @ResponseBody
    public ResponseEntity<?> googleSignup(@RequestBody GoogleSignupRequest request) {
        try {
            Map<String, Object> response = authService.registerWithGoogle(
                    request.getToken(),
                    request.getNewsletter()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard - MedShare");
        return "dashboard";
    }
}