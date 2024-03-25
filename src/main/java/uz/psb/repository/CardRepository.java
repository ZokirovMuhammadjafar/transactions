package uz.psb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.psb.entity.Card;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByPanAndDeletedFalse(String pan);

    List<Card> findByPhoneNumberAndDeletedFalse(String phone);

    Optional<Card> findByPan(String cardPan);
}
