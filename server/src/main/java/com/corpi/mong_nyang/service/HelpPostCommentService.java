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

@Service
@Transactional
@RequiredArgsConstructor
public class HelpPostCommentService {
    private final HelpPostRepository helpPostRepository;
    private final HelpPostCommentRepository helpPostCommentRepository;
    private final UserRepository userRepository;

    public Long createCommentByUserInPost(Long postId, Long userId, String content) {
        HelpPosts foundedPost = helpPostRepository.findById(postId).get();
        User foundedUser = userRepository.findById(userId).get();
        HelpPostComments comment = HelpPostComments.of(content, foundedUser);

        foundedPost.replyComment(comment);
        helpPostCommentRepository.save(comment);

        return comment.getId();
    }

    public void updateComment(Long commentId, String content) {
        HelpPostComments foundedComment = helpPostCommentRepository.findById(commentId).get();

        foundedComment.changeContent(content);
    }

    public void deleteComment(Long commentId) {
        HelpPostComments foundedComment = helpPostCommentRepository.findById(commentId).get();

        foundedComment.clearComment();
    }
}
