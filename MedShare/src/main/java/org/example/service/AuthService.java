package org.example.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Value("${google.client.id}")
    private String googleClientId;

    private GoogleIdTokenVerifier getVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    public Map<String, Object> authenticateWithGoogle(String token) throws Exception {
        GoogleIdToken idToken = getVerifier().verify(token);

        if (idToken == null) {
            throw new Exception("Token inválido");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();

        Optional<User> existingUser = userRepository.findByGoogleId(googleId);

        if (existingUser.isEmpty()) {
            throw new Exception("Usuário não encontrado. Por favor, cadastre-se primeiro.");
        }

        User user = existingUser.get();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login realizado com sucesso!");
        response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getFullName(),
                "email", user.getEmail()
        ));

        return response;
    }

    public Map<String, Object> registerWithGoogle(String token, Boolean newsletter) throws Exception {
        GoogleIdToken idToken = getVerifier().verify(token);

        if (idToken == null) {
            throw new Exception("Token inválido");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String picture = (String) payload.get("picture");

        // Verificar se usuário já existe
        Optional<User> existingUser = userRepository.findByGoogleId(googleId);
        if (existingUser.isPresent()) {
            throw new Exception("Usuário já cadastrado. Faça login.");
        }

        // Criar novo usuário
        User newUser = new User(googleId, firstName, lastName, email);
        newUser.setProfilePicture(picture);
        newUser.setNewsletterEnabled(newsletter != null ? newsletter : false);

        User savedUser = userRepository.save(newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Conta criada com sucesso!");
        response.put("user", Map.of(
                "id", savedUser.getId(),
                "name", savedUser.getFullName(),
                "email", savedUser.getEmail()
        ));

        return response;
    }
}