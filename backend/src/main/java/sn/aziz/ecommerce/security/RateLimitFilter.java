package sn.aziz.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import java.util.concurrent.ConcurrentHashMap;

/** Limite les tentatives sensibles sans dépendance externe ; Redis est recommandé pour plusieurs instances. */
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final ConcurrentHashMap<String, Fenetre> tentatives = new ConcurrentHashMap<>();
    @Value("${security.rate-limit.auth-max:10}") private int maxTentatives;
    @Value("${security.rate-limit.window-seconds:60}") private long fenetreSecondes;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) return true;
        String chemin = request.getRequestURI();
        return !"/api/auth/connexion".equals(chemin) && !"/api/auth/mot-de-passe-oublie".equals(chemin);
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String cle = request.getRemoteAddr(); long maintenant = System.currentTimeMillis();
        Fenetre fenetre = tentatives.compute(cle, (k, ancienne) -> ancienne == null || ancienne.fin < maintenant
                ? new Fenetre(maintenant + fenetreSecondes * 1000, 1) : new Fenetre(ancienne.fin, ancienne.compteur + 1));
        if (fenetre.compteur > maxTentatives) {
            response.setStatus(429); response.setHeader("Retry-After", String.valueOf(fenetreSecondes));
            response.setContentType("application/json"); response.getWriter().write("{\"message\":\"Trop de tentatives, réessayez plus tard\"}"); return;
        }
        chain.doFilter(request, response);
    }
    private record Fenetre(long fin, int compteur) { }
}
