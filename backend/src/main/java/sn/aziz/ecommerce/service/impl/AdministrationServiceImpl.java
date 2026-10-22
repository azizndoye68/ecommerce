package sn.aziz.ecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.aziz.ecommerce.dto.*;
import sn.aziz.ecommerce.entity.Role;
import sn.aziz.ecommerce.entity.Utilisateur;
import sn.aziz.ecommerce.exception.RessourceNonTrouveeException;
import sn.aziz.ecommerce.mapper.UtilisateurMapper;
import sn.aziz.ecommerce.repository.UtilisateurRepository;
import sn.aziz.ecommerce.service.AdministrationService;
import sn.aziz.ecommerce.service.AuditService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdministrationServiceImpl implements AdministrationService {
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    public UtilisateurDTO creerAdministrateur(String acteurEmail, CreerAdministrateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        Utilisateur admin = Utilisateur.builder().nom(request.getNom()).prenom(request.getPrenom()).email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse())).telephone(request.getTelephone()).adresse(request.getAdresse())
                .role(Role.ADMIN).actif(true).build();
        admin = utilisateurRepository.save(admin);
        auditService.journaliser(acteur(acteurEmail).getId(), "CREATION_ADMIN", "utilisateur:" + admin.getId());
        return utilisateurMapper.toDTO(admin);
    }

    @Override
    public UtilisateurDTO modifierStatut(String acteurEmail, Long id, StatutUtilisateurRequest request) {
        Utilisateur cible = utilisateurRepository.findById(id).orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable"));
        Utilisateur acteur = acteur(acteurEmail);
        if (cible.getRole() == Role.SUPER_ADMIN && !request.getActif() && utilisateurRepository.countByRole(Role.SUPER_ADMIN) <= 1)
            throw new IllegalArgumentException("Le dernier SUPER_ADMIN ne peut pas être désactivé");
        cible.setActif(request.getActif());
        auditService.journaliser(acteur.getId(), request.getActif() ? "ACTIVATION_UTILISATEUR" : "DESACTIVATION_UTILISATEUR", "utilisateur:" + cible.getId());
        Utilisateur utilisateurMisAJour = utilisateurRepository.save(cible);
        log.info("Statut du compte modifié: utilisateurId={}, actif={}, acteurId={}",
                utilisateurMisAJour.getId(), utilisateurMisAJour.isActif(), acteur.getId());
        return utilisateurMapper.toDTO(utilisateurMisAJour);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> listerAdministrateurs() {
        return utilisateurRepository.findAll().stream().filter(u -> u.getRole() != Role.CLIENT).map(utilisateurMapper::toDTO).toList();
    }
    private Utilisateur acteur(String email) { return utilisateurRepository.findByEmail(email).orElseThrow(() -> new RessourceNonTrouveeException("Administrateur introuvable")); }
}
