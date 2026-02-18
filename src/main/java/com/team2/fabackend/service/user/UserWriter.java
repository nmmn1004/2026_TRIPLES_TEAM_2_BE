package com.team2.fabackend.service.user;

import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserDeleteReason;
import com.team2.fabackend.domain.user.UserDeleteReasonRepository;
import com.team2.fabackend.domain.user.UserRepository;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Transactional
public class UserWriter {
    private final UserRepository userRepository;
    private final UserDeleteReasonRepository userDeleteReasonRepository;

    public void create(User user) {
        validateUserNotNull(user);
        userRepository.save(user);
    }

    public void createReason(UserDeleteReason reason) {
        userDeleteReasonRepository.save(reason);
    }

    public void updatePassword(User user, String encodedPassword) {
        validateUserNotNull(user);
        user.updatePassword(encodedPassword);
    }

    public void updateProfile(User user, String name, String nickName, LocalDate birth) {
        validateUserNotNull(user);

        if (hasText(name)) {
            user.updateName(name);
        }
        if (hasText(nickName)) {
            user.updateNickName(nickName);
        }
        if (birth != null) {
            user.updateBirth(birth);
        }
    }

    public void delete(User user) {
        validateUserNotNull(user);
        userRepository.delete(user);
    }

    private void validateUserNotNull(User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private boolean hasText(String str) {
        return str != null && !str.isBlank();
    }
}
