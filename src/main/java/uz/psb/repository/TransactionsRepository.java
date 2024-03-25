package uz.psb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.psb.entity.Transactions;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Stream<Transactions> findAllByTransactionDateBetween(LocalDateTime from, LocalDateTime to);
    boolean existsByProviderTrnId(long providerTrnId);
}
