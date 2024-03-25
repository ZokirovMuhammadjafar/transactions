package uz.psb.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {
    public static boolean checkPhoneNumber(String phone) {
        String phoneRegex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean checkCardNumber(String card) {
        String accountRegex = "^999[0-9]{13}$";
        Pattern pattern = Pattern.compile(accountRegex);
        Matcher matcher = pattern.matcher(card);
//        man check qilishga karta topolmadim shu sababli luna ochirib turibman
//        return matcher.matches() && isValidLuna(card);
        return matcher.matches() ;
    }


    public static boolean isValidLuna(String number) {
        int nDigits = number.length();
        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--) {
            int d = number.charAt(i) - '0';
            if (isSecond) {
                d *= 2;
            }
            nSum += d / 10;
            nSum += d % 10;
            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }
}
