package sn.aziz.ecommerce.service.impl;

import sn.aziz.ecommerce.dto.AuthResponse;
import sn.aziz.ecommerce.dto.ConnexionRequest;
import sn.aziz.ecommerce.dto.InscriptionRequest;
import sn.aziz.ecommerce.entity.Role;
import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.repository.UtilisateurRepository;
import sn.aziz.ecommerce.security.JwtService;
import sn.aziz.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse inscrire(InscriptionRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .role(Role.CLIENT)
                .build();

        utilisateurRepository.save(utilisateur);

        String token = jwtService.genererToken(utilisateur);

        return AuthResponse.builder()
                .token(token)
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(utilisateur.getRole().name())
                .build();
    }

    @Override
    public AuthResponse connecter(ConnexionRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        String token = jwtService.genererToken(utilisateur);

        return AuthResponse.builder()
                .token(token)
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(utilisateur.getRole().name())
                .build();
    }
}