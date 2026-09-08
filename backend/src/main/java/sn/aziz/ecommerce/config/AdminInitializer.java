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
        // Ne jamais promouvoir un compte existant sur la seule base de l'adresse par défaut.
        // Le mot de passe configuré sert de preuve explicite que le bootstrap est voulu.
        if (adminPassword == null || adminPassword.isBlank()) return;
        boolean adminExiste = utilisateurRepository.existsByRole(Role.SUPER_ADMIN);

        if (!adminExiste) {
            Utilisateur existant = utilisateurRepository.findByEmail(adminEmail).orElse(null);
            if (existant != null) {
                existant.setRole(Role.SUPER_ADMIN);
                existant.setActif(true);
                utilisateurRepository.save(existant);
                return;
            }
            Utilisateur admin = Utilisateur.builder()
                    .nom(adminNom)
                    .prenom(adminPrenom)
                    .email(adminEmail)
                    .motDePasse(passwordEncoder.encode(adminPassword))
                    .role(Role.SUPER_ADMIN)
                    .build();

            utilisateurRepository.save(admin);
            System.out.println("Compte admin créé avec l'email : " + adminEmail);
        }
    }
}
