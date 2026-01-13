package com.team2.fabackend.service.user;

import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class UserWriter {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public void updatePassword(User user, String encodedPassword) {
        user.changePassword(encodedPassword);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}
