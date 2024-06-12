package com.corpi.mong_nyang.dto.help_post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class HelpPostRequest {
    private String title;
    private String content;
    private List<MultipartFile> images;
}