package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostImages;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.repository.HelpPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HelpPostService {
    private final int pageLimit = 50;
    private final HelpPostRepository helpPostRepository;
    private final ImageStoreService imageStoreService;

    public Long createPost(String title, String content, User user, List<HelpPostImages> images) {
        HelpPosts post = HelpPosts.of(title, content, user, images);

        helpPostRepository.save(post);

        return post.getId();
    }

    public void deletePost(Long id) {
        helpPostRepository.deleteById(id);
    }

    public void updatePost(Long id, String title, String content, List<MultipartFile> images) throws IOException {
        Optional<HelpPosts> foundedPost = helpPostRepository.findById(id);

        if (foundedPost.isEmpty()) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }

        HelpPosts post = foundedPost.get();

        // 기존 이미지 삭제.
        for (HelpPostImages originImages : post.getImages()) {
            imageStoreService.removeImage(originImages.getUrl());
        }

        List<HelpPostImages> updatedImages = new ArrayList<>();

        // 업데이트 이미지 저장.
        for (MultipartFile image : images) {
            String url = imageStoreService.saveImage(image);
            updatedImages.add(HelpPostImages.of(url));
        }

        post.update(title, content, updatedImages);
    }

    public void removePost(Long id) throws IOException {
        // 이미지 삭제
        HelpPosts foundedPost = helpPostRepository.findById(id).get();

        for (HelpPostImages image : foundedPost.getImages()) {
            imageStoreService.removeImage(image.getUrl());
        }

        helpPostRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<HelpPosts> findById(Long id) {
        return helpPostRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<HelpPosts> fetchPostList(int page) {
        Pageable pageable = PageRequest.of(page, pageLimit);

        List<HelpPosts> findedList = helpPostRepository.findAll(pageable).getContent();

        return findedList;
    }

    public void addImage(Long postId, List<HelpPostImages> images) {
        HelpPosts posts = helpPostRepository.findById(postId).get();

        for (HelpPostImages image : images) {
            posts.addImage(image);
        }
    }

    public void removeImage(Long postId, int index) {
        HelpPosts posts = helpPostRepository.findById(postId).get();

        posts.removeImageAt(index);
    }
}
