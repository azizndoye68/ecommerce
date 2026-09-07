package sn.aziz.ecommerce.config;

import sn.aziz.ecommerce.entity.Role;
import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.nom}")
    private String adminNom;

    @Value("${admin.prenom}")
    private String adminPrenom;

    @Override
    public void run(String... args) {
        if (adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        boolean adminExiste = utilisateurRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.ADMIN);

        if (!adminExiste) {
            Utilisateur admin = Utilisateur.builder()
                    .nom(adminNom)
                    .prenom(adminPrenom)
                    .email(adminEmail)
                    .motDePasse(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();

            utilisateurRepository.save(admin);
            System.out.println("Compte admin créé avec l'email : " + adminEmail);
        }
    }
}
