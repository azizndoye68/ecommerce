package sn.aziz.ecommerce.service;

import sn.aziz.ecommerce.dto.AuthResponse;
import sn.aziz.ecommerce.dto.ConnexionRequest;
import sn.aziz.ecommerce.dto.InscriptionRequest;

public interface AuthService {

    AuthResponse inscrire(InscriptionRequest request);

    AuthResponse connecter(ConnexionRequest request);
}