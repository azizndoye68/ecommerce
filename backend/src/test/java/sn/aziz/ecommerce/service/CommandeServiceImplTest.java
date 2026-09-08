package sn.aziz.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sn.aziz.ecommerce.entity.Commande;
import sn.aziz.ecommerce.entity.ModePaiement;
import sn.aziz.ecommerce.entity.StatutCommande;
import sn.aziz.ecommerce.repository.CommandeRepository;
import sn.aziz.ecommerce.repository.PanierRepository;
import sn.aziz.ecommerce.repository.ProduitRepository;
import sn.aziz.ecommerce.repository.UtilisateurRepository;
import sn.aziz.ecommerce.service.impl.CommandeServiceImpl;

@ExtendWith(MockitoExtension.class)
class CommandeServiceImplTest {
    @Mock private CommandeRepository commandeRepository;
    @Mock private PanierRepository panierRepository;
    @Mock private ProduitRepository produitRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private WaveService waveService;
    @Mock private OrangeMoneyService orangeMoneyService;
    private CommandeService commandeService;

    @BeforeEach
    void setUp() {
        commandeService = new CommandeServiceImpl(commandeRepository, panierRepository, produitRepository,
                utilisateurRepository, waveService, orangeMoneyService);
    }

    @Test
    void avanceUneCommandePayeeVersLaPreparation() {
        Commande commande = commande(StatutCommande.PAYEE);
        when(commandeRepository.findById(12L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(commande)).thenReturn(commande);

        assertThat(commandeService.modifierStatutLivraison(12L, StatutCommande.EN_PREPARATION).getStatut())
                .isEqualTo(StatutCommande.EN_PREPARATION);
    }

    @Test
    void refuseDeLivrerUneCommandeAvantExpedition() {
        Commande commande = commande(StatutCommande.PAYEE);
        when(commandeRepository.findById(12L)).thenReturn(Optional.of(commande));

        assertThatThrownBy(() -> commandeService.modifierStatutLivraison(12L, StatutCommande.LIVREE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Commande commande(StatutCommande statut) {
        Commande commande = new Commande();
        commande.setId(12L);
        commande.setStatut(statut);
        commande.setModePaiement(ModePaiement.ORANGE_MONEY);
        commande.setTotal(BigDecimal.TEN);
        return commande;
    }
}
