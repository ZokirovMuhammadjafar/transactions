package uz.psb.exceptions;

import com.provider.uws.GenericResult;
import uz.psb.utils.DateTimeUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 * bu exceptionni stack muammosini hal qilish uchun yozilgan class
 */
public class ExceptionResolver {
    /**
     * method orqali throw bo'ladigan custom exceptionlarni ushlab request ichiga qoshish imkoni beradi
     * @param response generik result fieldalar to'diriladi agar qandaydi custom exception qaytsa
     * @param request bu kelayotgan request
     * @param service refleksiyada ishlatish uchun service
     * @param methodName shu servise methodi name
     * @param <T> response tipi
     * @param <D> request tipi
     */
    public static <T extends GenericResult, D> T resolve(Class<T> response, D request, Object service, String methodName) {
        try {
            return (T) service.getClass().getDeclaredMethod(methodName, request.getClass()).invoke(service, request);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
//            bu yerda har qanday custom exception yozib ketilaveradi
            try {
                T responseObject = response.getConstructor().newInstance();
                responseObject.setTimeStamp(DateTimeUtils.parseGregorian(LocalDateTime.now()));
                if (targetException instanceof ValidatorException) {
                    ValidatorException validatorException = (ValidatorException) targetException;
                    responseObject.setStatus(400);
                    responseObject.setErrorMsg(validatorException.getParamKey()+" " +validatorException.getParamValue());
                } else if (targetException instanceof Exception) {
                    responseObject.setStatus(418);
                    responseObject.setErrorMsg(targetException.getMessage());
                }
                return responseObject;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
