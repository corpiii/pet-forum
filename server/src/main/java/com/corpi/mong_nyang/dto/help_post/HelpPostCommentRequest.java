package com.corpi.mong_nyang.dto.help_post;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class HelpPostCommentRequest {
    private String content;
}
