package com.corpi.mong_nyang.dto.help_post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HelpPostListResponse {
    private int size;
    private List<HelpPostSummaryDTO> posts;
}
