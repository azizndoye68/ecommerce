package sn.aziz.ecommerce.service.impl;

import sn.aziz.ecommerce.dto.UtilisateurDTO;
import sn.aziz.ecommerce.dto.UtilisateurUpdateRequest;
import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.exception.RessourceNonTrouveeException;
import sn.aziz.ecommerce.mapper.UtilisateurMapper;
import sn.aziz.ecommerce.repository.UtilisateurRepository;
import sn.aziz.ecommerce.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UtilisateurDTO> obtenirTous() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurDTO obtenirParId(Long id) {
        Utilisateur utilisateur = trouverParId(id);
        return utilisateurMapper.toDTO(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurDTO obtenirMonProfil(String email) {
        return utilisateurMapper.toDTO(trouverParEmail(email));
    }

    @Override
    @Transactional
    public UtilisateurDTO modifierMonProfil(String email, UtilisateurUpdateRequest request) {
        Utilisateur utilisateur = trouverParEmail(email);
        appliquerInformationsPersonnelles(utilisateur, request);
        return utilisateurMapper.toDTO(utilisateurRepository.save(utilisateur));
    }

    @Override
    @Transactional
    public void changerMonMotDePasse(String email, String motDePasseActuel, String nouveauMotDePasse) {
        Utilisateur utilisateur = trouverParEmail(email);
        if (!passwordEncoder.matches(motDePasseActuel, utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
        }
        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public UtilisateurDTO modifier(Long id, UtilisateurUpdateRequest request) {
        Utilisateur utilisateur = trouverParId(id);
        appliquerInformationsPersonnelles(utilisateur, request);
        return utilisateurMapper.toDTO(utilisateurRepository.save(utilisateur));
    }

    private void appliquerInformationsPersonnelles(Utilisateur utilisateur, UtilisateurUpdateRequest request) {
        if (StringUtils.hasText(request.getNom())) {
            utilisateur.setNom(request.getNom());
        }
        if (StringUtils.hasText(request.getPrenom())) {
            utilisateur.setPrenom(request.getPrenom());
        }
        if (StringUtils.hasText(request.getTelephone())) {
            utilisateur.setTelephone(request.getTelephone());
        }
        if (StringUtils.hasText(request.getAdresse())) {
            utilisateur.setAdresse(request.getAdresse());
        }
    }

    @Override
    public void supprimer(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new RessourceNonTrouveeException("Utilisateur introuvable avec l'id : " + id);
        }
        utilisateurRepository.deleteById(id);
    }

    private Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable avec l'id : " + id));
    }

    private Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable"));
    }
}
