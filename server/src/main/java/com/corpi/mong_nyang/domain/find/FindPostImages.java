package com.corpi.mong_nyang.domain.find;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class FindPostImages {
    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    private FindPosts post;
}
