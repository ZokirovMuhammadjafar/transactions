package uz.psb.exceptions;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
/**
 * bu kelgan requestlarni chek qilganda
 * biron xatolik kuzatilsa shunda throw bo'ladigan exception
 */
public class ValidatorException extends RuntimeException {
    //    bu qaysi fielda hatolik kelgani
    private final String paramKey;
    //    qanday xatolik kuzatilgani yoritish uchun
    private final String paramValue;

    public ValidatorException(String paramKey, String paramValue) {
        super();
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    public ValidatorException(String paramKey, String paramValue, String message) {
        super(message);
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }
}
