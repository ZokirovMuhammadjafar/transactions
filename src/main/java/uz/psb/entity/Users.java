package uz.psb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
public class Users extends AbstractEntity {
    @Column( unique = true, nullable = false)
    //    username yagona va yaratilishi kerak bo'ladi
    private String username;
    //    bu yerda agar parol hashlangan bo'lsa hash boyich tekshirish kerak
    @Column(nullable = false)
    private String password;
    //    pinf yagona va doimiy bo'lishi kerak
    @Column( unique = true, nullable = false)
    private String pinfl;
    private String fullName;
}
