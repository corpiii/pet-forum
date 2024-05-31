package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.repository.HelpPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelpPostService {
    private final HelpPostRepository helpPostRepository;

    public Long createPost(String title, String content, User user) {
        HelpPosts post = HelpPosts.of(title, content, user);

        helpPostRepository.save(post);

        return post.getId();
    }
}
