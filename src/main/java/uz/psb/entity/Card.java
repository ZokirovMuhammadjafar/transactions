package uz.psb.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Card extends AbstractEntity {
    //    kartani pani
    private String pan;
    //    karta nomi HUMO,UZCARD, YOKI VIZA
    private String cardName;
    //    qachongancha amal qilishi 12/26
    private String cardValidationDate;
    //    shot raqami
    private String accountNumber;
    //    telephone Number
    private String phoneNumber;
    //    cartani pin code
    private String pin;
}
