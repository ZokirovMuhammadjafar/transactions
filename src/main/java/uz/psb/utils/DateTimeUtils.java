package uz.psb.utils;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {
    private static DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    public static XMLGregorianCalendar parseGregorian(LocalDateTime localDateTime) {
        DatatypeFactory dataTypeFactory = null;
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);
        return dataTypeFactory.newXMLGregorianCalendar(gregorianCalendar);
    }

    public static XMLGregorianCalendar parseGregorian(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    public static String writeAsStringDate(LocalDateTime localDateTime){
        return dateTimeFormatter.format(localDateTime);
    }
}
