package uz.psb.services;

import com.provider.uws.ChangePasswordArguments;
import com.provider.uws.ChangePasswordResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.psb.entity.Users;
import uz.psb.exceptions.ValidatorException;
import uz.psb.repository.UsersRepository;
import uz.psb.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;

    /**
     * bu yerda clientni user paroli almashadi
     * @param changePasswordArguments
     * @return
     */
    @Transactional
    public ChangePasswordResult changePassword(ChangePasswordArguments changePasswordArguments) {
        String password = changePasswordArguments.getPassword();
        String username = changePasswordArguments.getUsername();
        if (!StringUtils.hasText(username)) {
            throw new ValidatorException("username", "bu field null kelgan");
        }
        Optional<Users> optionalUser = usersRepository.findByUsernameAndDeletedFalse(username);
        if (optionalUser.isEmpty())
            throw new ValidatorException("username", "bu foydalanuvchi mavjud emas : " + username);
        Users users = optionalUser.get();
        if (!users.getPassword().equals(password))
            throw new ValidatorException("password", "eski password xato kelgan");
        if (!StringUtils.hasText(changePasswordArguments.getNewPassword()))
            throw new ValidatorException("newPassword", "yangi parol kiritilmagan");
        users.setPassword(changePasswordArguments.getNewPassword());
        usersRepository.save(users);
        ChangePasswordResult changePasswordResult = new ChangePasswordResult();
        changePasswordResult.setStatus(200);
        changePasswordResult.setTimeStamp(DateTimeUtils.parseGregorian(LocalDateTime.now()));
        return changePasswordResult;
    }
}
