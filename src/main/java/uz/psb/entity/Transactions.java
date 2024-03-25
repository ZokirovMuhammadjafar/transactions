package uz.psb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Transactions extends AbstractEntity {
    //    bu qaysi accountdan boshqa accontga pul tashlanayotgani
    private String fromAccountNumber;
    private String toAccountNumber;
    //    qaysi vaqtda bajarilgani
    private LocalDateTime transactionDate;
    //    bu transaksiya otkazib xato bo'lgandan so'ng qayta joyiga solgan vaqti yoziladi
    private LocalDateTime transactionCancelDate;
    @Enumerated(value = EnumType.STRING)
    private TransactionsStatus transactionsStatus;
    //    bir kartadan boshqa kartaga boshqa karta yoziladi
    private String fromCardPan;
    private String toCardPan;
    //bu bank transaksiya idsi bu ham bitta yagona bo'ladi
    @Column(unique = true,nullable = false)
    private long providerTrnId;
    //    miqdori
    private long amount;
    //    transaksiyani nima sababdan o'tkazayotgani
    private String transactionCause;
    //    transaksiya nima sababdan bekor bo'lgani
    private String transactionCancelCause;
}
