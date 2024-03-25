package uz.psb.services;

import com.provider.uws.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uz.psb.entity.*;
import uz.psb.exceptions.ValidatorException;
import uz.psb.repository.AccountRepository;
import uz.psb.repository.CardRepository;
import uz.psb.repository.TransactionsRepository;
import uz.psb.repository.UsersRepository;
import uz.psb.utils.CheckUtils;
import uz.psb.utils.DateTimeUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service(value = "transactionService")
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionsRepository transactionsRepository;
    private final AccountRepository accountRepository;
    private final UsersRepository usersRepository;
    private final CardRepository cardRepository;

    /**
     * bu yerda transaksiya yaratilib keyin u amalga oshirib yuboriladi
     *
     * @param request bunda service id kelyabdi agan bu service transaksiya banklar aro pullarni otkazadigan bo'lsa shu service murojaat qilinadi
     *                lekin hozir defaul o'zimda saqlangan shu orqali ishlayabman
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PerformTransactionResult performTransaction(PerformTransactionArguments request) throws ValidatorException {
        Users users = checkUsername(request);
        String fromPan = null;
        String toPan = null;
        String toPhone = null;
        String comment = "";
        String pin = null;
        for (GenericParam parameter : request.getParameters()) {
            if (parameter.getParamKey().equals("fromPan")) {
                fromPan = parameter.getParamValue();
            } else if (parameter.getParamKey().equals("toPan")) {
                toPan = parameter.getParamValue();
            } else if (parameter.getParamKey().equals("toPhone")) {
                toPhone = parameter.getParamValue();
            } else if (parameter.getParamKey().equals("pin")) {
                pin = parameter.getParamValue();
            } else if (parameter.getParamKey().equals("comment")) {
                comment = parameter.getParamValue();
            }
        }
        if (transactionsRepository.existsByProviderTrnId(request.getTransactionId())) {
            throw new ValidatorException("providerTrnId", "bu allaqachon olingan!!!");
        }
        if (fromPan != null && CheckUtils.checkCardNumber(fromPan)) {
            Card fromCard = cardRepository.findByPanAndDeletedFalse(fromPan).orElseThrow(() -> {
                throw new ValidatorException("fromPan", "bu card number tog'ri emas");
            });

            Account fromAccount = accountRepository.findByAccountNumber(fromCard.getAccountNumber());
            if (!Objects.equals(fromAccount.getPinfl(), users.getPinfl())) throw new ValidatorException("fromPan", "xato kelgan");
            if (!ObjectUtils.nullSafeEquals(fromCard.getPin(), pin))
                throw new ValidatorException("pin", "card pin xato kelgan");
            if (fromAccount.getBalance() < request.getAmount())
                throw new ValidatorException("balance", "balanceda mablag yetarli emas");

            Card toCard = null;
            if (toPan != null && CheckUtils.checkCardNumber(toPan)) {
                toCard = cardRepository.findByPanAndDeletedFalse(toPan).orElseThrow(() -> {
                    throw new ValidatorException("toPan", "bu card number tog'ri emas");
                });
            } else if (toPhone != null && CheckUtils.checkPhoneNumber(toPhone)) {
                List<Card> cards = cardRepository.findByPhoneNumberAndDeletedFalse(toPhone);
                if (cards.size() < 1)
                    throw new ValidatorException("phoneNumber", "telephone nomerga ulangan karta topilmadi");
                toCard = cards.get(0);
                if(toCard.getAccountNumber()==fromCard.getAccountNumber()){
                    if (cards.size() > 1)   toCard=cards.get(1);
                    else throw new ValidatorException("transaction","amalga oshirib bolmaydigan transaksiya!!!");
                }
            } else {
                throw new ValidatorException("karta", "xato kelgan");
            }
            Account toAccount = accountRepository.findByAccountNumber(toCard.getAccountNumber());
            Transactions transaction = createTransaction(request, fromPan, toPan, comment, fromAccount, toAccount);
            PerformTransactionResult performTransactionResult = new PerformTransactionResult();
            performTransactionResult.setProviderTrnId(transaction.getId());
            performTransactionResult.setStatus(200);
            performTransactionResult.setTimeStamp(DateTimeUtils.parseGregorian(LocalDateTime.now()));
            List<GenericParam> parameters = performTransactionResult.getParameters();
            GenericParam param1 = new GenericParam();
            param1.setParamKey("balance");
            param1.setParamValue("" + fromAccount.getBalance());
            parameters.add(param1);
            return performTransactionResult;
        }
        throw new ValidatorException("pan", "karta panini jonatuvchi xato kiritgan");

    }

    /**
     * bu requestda shu user bormi yoki yoqmi tekshiradi
     *
     * @param request ichida username, password keladi
     */
    private Users checkUsername(GenericArguments request) {
        Users users = usersRepository.findByUsernameAndDeletedFalse(request.getUsername()).orElseThrow(() -> {
            throw new ValidatorException("username", "username xato kelgan");
        });
        if (!ObjectUtils.nullSafeEquals(users.getPassword(), request.getPassword())) {
            throw new ValidatorException("password", "password xato kelgan");
        }
        return users;
    }

    private Transactions createTransaction(PerformTransactionArguments request, String fromPan, String toPan, String comment, Account fromAccount, Account toAccount) {
        Transactions transactions = new Transactions();
        transactions.setFromCardPan(fromPan);
        transactions.setToCardPan(toPan);
        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
        toAccount.setBalance(toAccount.getBalance() + request.getAmount());
        transactions.setFromAccountNumber(fromAccount.getAccountNumber());
        transactions.setToAccountNumber(toAccount.getAccountNumber());
        transactions.setAmount(request.getAmount());
        transactions.setTransactionDate(LocalDateTime.now());
        transactions.setTransactionsStatus(TransactionsStatus.FINISHED);
        transactions.setTransactionCause(comment);
//        bu berilayotgan id transaksiya id providerniki deb olindi
        transactions.setProviderTrnId(request.getTransactionId());
        transactionsRepository.save(transactions);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        return transactions;
    }

    @Transactional(readOnly = true)
    /**
     * client ni balanceda qancha mablag' borligini chiqarib beradi
     * o'ylab ko'rsa clientda bir nechta shotlarida pul bo'ladi shundan
     * cartani pani kelishi kerak aks holda umumiy balance ko'rsatishga majbu bo'lar edik
     */
    public GetInformationResult getInformation(GetInformationArguments request) {
        checkUsername(request);
        Long clientId = null;
        String cardPan = null;
        for (GenericParam parameter : request.getParameters()) {
            if (parameter.getParamKey().equals("clientId")) {
                clientId = Long.valueOf(parameter.getParamValue());
            } else if (parameter.getParamKey().equals("pan")) {
                cardPan = parameter.getParamValue();
            }
        }
        if (clientId == null) throw new ValidatorException("clientId", "client Id null kelgan");
        Users client = usersRepository.findById(clientId).orElseThrow(() -> {
            throw new ValidatorException("clientId", "bunday client id topilmadi");
        });
        if (cardPan == null) throw new ValidatorException("pan", "card pan kelmagan!!!");
        Card card = cardRepository.findByPan(cardPan).orElseThrow(() -> {
            throw new ValidatorException("pan", "keltirilgan karta mavjud emas");
        });
        Account account = accountRepository.findByAccountNumber(card.getAccountNumber());
        if (!Objects.equals(client.getPinfl(), account.getPinfl()))
            throw new ValidatorException("pan", "pan xato berilgan!!!");
        GetInformationResult getInformationResult = new GetInformationResult();
        getInformationResult.setStatus(0);
        getInformationResult.setTimeStamp(DateTimeUtils.parseGregorian(new Date()));
        getInformationResult.getParameters().add(new GenericParam() {{
            setParamKey("balance");
            setParamValue(account.getBalance() + "");
        }});
        getInformationResult.getParameters().add(new GenericParam() {{
            setParamKey("name");
            setParamValue(client.getFullName());
        }});
        return getInformationResult;
    }


    @Transactional
    /**
     * bu yerda transacksiyani bekor qiladi
     * bekor qilish sababi korsatiladi
     */
    public CancelTransactionResult cancelTransaction(CancelTransactionArguments request) {
        checkUsername(request);
        Optional<Transactions> optionalTransaction = transactionsRepository.findById(request.getTransactionId());
        if (optionalTransaction.isEmpty()) {
            throw new ValidatorException("transactionId", "transaction topilmadi id : " + request.getTransactionId());
        }
        Transactions transactions = optionalTransaction.get();
        String fromAccountNumber = transactions.getFromAccountNumber();
        String toAccountNumber = transactions.getToAccountNumber();
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber);

        long balance = fromAccount.getBalance();
        balance = balance + transactions.getAmount();
        fromAccount.setBalance(balance);

        long toAccountBalance = toAccount.getBalance();
        toAccountBalance -= transactions.getAmount();
        toAccount.setBalance(toAccountBalance);
        transactions.setTransactionsStatus(TransactionsStatus.CANCEL);
        for (GenericParam parameter : request.getParameters()) {
            if (parameter.getParamKey().equals("comment")) {
                transactions.setTransactionCause(parameter.getParamValue());
            }
        }
        transactions.setTransactionCancelDate(LocalDateTime.now());
        CancelTransactionResult cancelTransactionResult = new CancelTransactionResult();
        cancelTransactionResult.setTransactionState(TransactionsStatus.CANCEL.ordinal());
        cancelTransactionResult.setStatus(200);
        cancelTransactionResult.setTimeStamp(DateTimeUtils.parseGregorian(transactions.getTransactionCancelDate()));
        return cancelTransactionResult;
    }

    @Transactional(readOnly = true)
    /**
     * bu transaksoya bekor bo'lganmi yoki yoqmi shuni tekshirish imkonini beradi
     */
    public CheckTransactionResult checkTransactionResult(CheckTransactionArguments request) {
        long transactionId = request.getTransactionId();
        Transactions transactions = transactionsRepository.findById(transactionId).orElseThrow(() -> {
            throw new ValidatorException("transactionId", "transaction topilmadi!!!");
        });
        CheckTransactionResult checkTransactionResult = new CheckTransactionResult();
        checkTransactionResult.setTransactionState(transactions.getTransactionsStatus().ordinal());
        checkTransactionResult.setProviderTrnId(transactions.getProviderTrnId());
        checkTransactionResult.setTimeStamp(DateTimeUtils.parseGregorian(transactions.getTransactionDate()));
        return checkTransactionResult;
    }

    @Transactional(readOnly = true)
    /**
     * bu shu vaqt davomida qanday transaksiyar otganini chiqarib beradi
     */
    public GetStatementResult getStatementRequest(GetStatementArguments request) {
        checkUsername(request);
        XMLGregorianCalendar from = request.getDateFrom();
        XMLGregorianCalendar to = request.getDateTo();
        LocalDateTime fromDate = from.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
        LocalDateTime toDate = to.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
        GetStatementResult getStatementResult = new GetStatementResult();
        List<TransactionStatement> collect = transactionsRepository.findAllByTransactionDateBetween(fromDate, toDate).map(transactions -> {
            TransactionStatement statement = new TransactionStatement();
            statement.setTransactionId(transactions.getId());
            statement.setTransactionTime(DateTimeUtils.parseGregorian(transactions.getTransactionDate()));
            if (request.isOnlyTransactionId()) {
                return statement;
            }
            statement.setProviderTrnId(transactions.getProviderTrnId());
            statement.setAmount(transactions.getAmount());
            return statement;
        }).toList();
        getStatementResult.getStatements().addAll(collect);
        getStatementResult.setTimeStamp(DateTimeUtils.parseGregorian(LocalDateTime.now()));
        return getStatementResult;
    }
}
