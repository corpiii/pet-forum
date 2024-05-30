package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    public Long join(String name, String email, String password) {
        User user = User.of(name, email, password);

        userRepository.save(user);

        return user.getId();
    }
}
