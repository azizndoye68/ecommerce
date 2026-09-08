package sn.aziz.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sn.aziz.ecommerce.entity.JournalAudit;
import sn.aziz.ecommerce.repository.JournalAuditRepository;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final JournalAuditRepository repository;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void journaliser(Long acteurId, String action, String ressource) {
        repository.save(new JournalAudit(acteurId, action, ressource));
    }
}
