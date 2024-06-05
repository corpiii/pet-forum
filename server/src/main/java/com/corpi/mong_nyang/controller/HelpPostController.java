package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.dto.help_post.HelpPostListResponse;
import com.corpi.mong_nyang.dto.help_post.HelpPostSummaryDTO;
import com.corpi.mong_nyang.service.HelpPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/helpPostList")
@RequiredArgsConstructor
public class HelpPostController {
    private final HelpPostService helpPostService;

    @GetMapping
    public HelpPostListResponse helpPostList(@RequestParam(defaultValue = "0") int page) {
        List<HelpPosts> helpPosts = helpPostService.fetchPostList(page);
        List<HelpPostSummaryDTO> postSummaries = helpPosts.stream()
                .map(post -> new HelpPostSummaryDTO(post.getTitle()))
                .toList();

        return new HelpPostListResponse(postSummaries.size(), postSummaries);
    }
}