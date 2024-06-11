package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostComments;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.repository.HelpPostCommentRepository;
import com.corpi.mong_nyang.repository.HelpPostRepository;
import com.corpi.mong_nyang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final HelpPostRepository helpPostRepository;
    private final HelpPostCommentRepository helpPostCommentRepository;

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

    public User login(String email, String password) {
        User foundedUser = userRepository.findByEmail(email);

        if (foundedUser.getPassword().equals(password)) {
            return foundedUser;
        }

        return null;
    }

    public void delete(Long id) {
        List<HelpPostComments> allCommentByUser = helpPostCommentRepository.findAllByUser(id);
        List<HelpPosts> allByUser = helpPostRepository.findAllByUser(id);

        for (HelpPostComments helpPostComments : allCommentByUser) {
            helpPostCommentRepository.deleteById(helpPostComments.getId());
        }

        for (HelpPosts posts : allByUser) {
            helpPostRepository.deleteById(posts.getId());
        }

        userRepository.deleteById(id);
    }
}
