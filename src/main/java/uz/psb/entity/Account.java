package uz.psb.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account extends AbstractEntity {
    //    qaysi bankda turgani shotini
    private String bankMfo;
    private String pinfl;
    //    shot raqam kartani
    private String accountNumber;
    //    shotning balansi
    private long balance;
}
