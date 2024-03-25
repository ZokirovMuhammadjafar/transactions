package uz.psb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.psb.entity.Account;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String fromAccountNumber);
}
