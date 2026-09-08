package sn.aziz.ecommerce.exception;

public class RessourceNonTrouveeException extends RuntimeException {

    public RessourceNonTrouveeException(String message) {
        super(message);
    }
}