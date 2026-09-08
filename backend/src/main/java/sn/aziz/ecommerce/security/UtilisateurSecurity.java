package sn.aziz.ecommerce.security;

import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("utilisateurSecurity")
@RequiredArgsConstructor
public class UtilisateurSecurity {

    private final UtilisateurRepository utilisateurRepository;

    public boolean estProprietaire(Long id, Authentication authentication) {
        String email = authentication.getName();
        return utilisateurRepository.findById(id)
                .map(u -> u.getEmail().equals(email))
                .orElse(false);
    }
}