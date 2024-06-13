package com.corpi.mong_nyang.dto.help_post;

import com.corpi.mong_nyang.domain.help.HelpPostComments;
import com.corpi.mong_nyang.domain.help.HelpPostImages;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class HelpPostResponse {
    private final String title;
    private final String content;
    private final List<String> images;
    private final String authorName;
    private final List<HelpPostCommentDTO> comments;
    private final LocalDateTime createdAt;

    public static HelpPostResponse from(HelpPosts post) {
        List<String> images = post.getImages().stream().map(HelpPostImages::getUrl).collect(Collectors.toList());
        String authorName = post.getAuthor().getName();

        List<HelpPostCommentDTO> commentList = post.getComments().stream().map(HelpPostCommentDTO::from).collect(Collectors.toList());

        return new HelpPostResponse(post.getTitle(), post.getContent(), images, authorName, commentList, post.getCreatedAt());
    }
}