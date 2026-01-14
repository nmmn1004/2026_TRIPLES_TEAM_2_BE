package com.team2.fabackend.domain.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    List<Ledger> findAllByUserId(Long userId);
}