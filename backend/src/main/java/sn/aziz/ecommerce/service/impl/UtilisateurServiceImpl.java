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
    public UtilisateurDTO modifier(Long id, UtilisateurUpdateRequest request) {
        Utilisateur utilisateur = trouverParId(id);

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
        if (StringUtils.hasText(request.getMotDePasse())) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        return utilisateurMapper.toDTO(utilisateurRepository.save(utilisateur));
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
}