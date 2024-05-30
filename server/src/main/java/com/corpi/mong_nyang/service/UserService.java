package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long join(String name, String email, String password) throws IllegalArgumentException {
        User user = User.of(name, email, password);

        if (findOne(email) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다. 다른 이메일을 사용해주세요.");
        }

        userRepository.save(user);

        return user.getId();
    }

    public User findOne(String email) {
        return userRepository.findByEmail(email);
    }
}
