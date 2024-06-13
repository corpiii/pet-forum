package com.corpi.mong_nyang.dto.help_post;

import com.corpi.mong_nyang.domain.help.HelpPostComments;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class HelpPostCommentDTO {
    private final String authorName;
    private final String content;
    private final LocalDateTime createdAt;
    private final List<HelpPostCommentDTO> children;

    public static HelpPostCommentDTO from(HelpPostComments comment) {
        List<HelpPostComments> childrenOrigin = comment.getChildren();
        List<HelpPostCommentDTO> childrenDTO = null;

        if (!childrenOrigin.isEmpty()) {
            childrenDTO = childrenOrigin.stream().map(HelpPostCommentDTO::from).collect(Collectors.toList());
        }

        return new HelpPostCommentDTO(comment.getAuthor().getName(),  comment.getContent(), comment.getCreatedAt(), childrenDTO);
    }
}
