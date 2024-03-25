package uz.psb.endpoint;

import com.provider.uws.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uz.psb.exceptions.ExceptionResolver;
import uz.psb.services.TransactionService;
import uz.psb.services.UserService;

import java.lang.reflect.InvocationTargetException;

@Endpoint
@RequiredArgsConstructor
/**
 * soap requestlari qabul qilib javob beruchi pointlar bitta wsdl uchun bu api.wsdl
 */
public class Endpoints {

    private final TransactionService transactionService;
    private final UserService userService;


    private static final String NAMESPACE_URI = "http://uws.provider.com/";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetStatementArguments")
    @ResponsePayload
    public GetStatementResult getStatementResult(@RequestPayload GetStatementArguments request) {
//      bunda ham ishlaydi lekin exeptionni soap fault qilib jo'natadi
//        return transactionService.getStatementRequest(request);
        return ExceptionResolver.resolve(GetStatementResult.class, request, transactionService, "getStatementRequest");
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetInformationArguments")
    @ResponsePayload
    public GetInformationResult getInformation(@RequestPayload GetInformationArguments request) {
        //      bunda ham ishlaydi lekin exeptionni soap fault qilib jo'natadi
//        return transactionService.getInformation(request);
        return ExceptionResolver.resolve(GetInformationResult.class, request, transactionService, "getInformation");
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PerformTransactionArguments")
    @ResponsePayload
    public PerformTransactionResult performTransactionRequest(@RequestPayload PerformTransactionArguments request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //      bunda ham ishlaydi lekin exeptionni soap fault qilib jo'natadi
        //        transactionService.performTransaction(request);
        return ExceptionResolver.resolve(PerformTransactionResult.class, request, transactionService, "performTransaction");


    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ChangePasswordArguments")
    @ResponsePayload
    public ChangePasswordResult changePassword(@RequestPayload ChangePasswordArguments request) {
        //      bunda ham ishlaydi lekin exeptionni soap fault qilib jo'natadi
//        return userService.changePassword(request);
        return ExceptionResolver.resolve(ChangePasswordResult.class, request, userService, "changePassword");
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CancelTransactionArguments")
    @ResponsePayload
    public CancelTransactionResult cancelTransaction(@RequestPayload CancelTransactionArguments request) {
        //      bunda ham ishlaydi lekin exeptionni soap fault qilib jo'natadi
//        return transactionService.cancelTransaction(request);
        return ExceptionResolver.resolve(CancelTransactionResult.class, request, transactionService, "cancelTransaction");
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CheckTransactionArguments")
    @ResponsePayload
    public CheckTransactionResult checkTransactionArguments(@RequestPayload CheckTransactionArguments request) {
        //      bunda ham ishlaydi lekin exeptionni soap fault qilib jo'natadi
//        return transactionService.checkTransactionResult(request);
        return ExceptionResolver.resolve(CheckTransactionResult.class, request, transactionService, "checkTransactionResult");
    }
}
