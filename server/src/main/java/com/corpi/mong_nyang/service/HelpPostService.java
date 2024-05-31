package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.repository.HelpPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HelpPostService {
    private final int pageLimit = 50;
    private final HelpPostRepository helpPostRepository;

    public Long createPost(String title, String content, User user) {
        HelpPosts post = HelpPosts.of(title, content, user);

        helpPostRepository.save(post);

        return post.getId();
    }

    public void deletePost(Long id) {
        helpPostRepository.deleteById(id);
    }

    public void updatePost(Long postId, String title, String content) {
        Optional<HelpPosts> foundedPost = helpPostRepository.findById(postId);

        if (foundedPost.isEmpty()) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }

        HelpPosts post = foundedPost.get();

        post.update(title, content);
    }

    @Transactional(readOnly = true)
    public Optional<HelpPosts> findById(Long id) {
        return helpPostRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<HelpPosts> fetchPostList(int page) {
        Pageable pageable = PageRequest.of(page, pageLimit);
        List<HelpPosts> findedList = helpPostRepository.findHelpPosts(pageable).getContent();

        return findedList;
    }
}
