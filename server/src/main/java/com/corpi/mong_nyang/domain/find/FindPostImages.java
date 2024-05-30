package com.corpi.mong_nyang.domain.find;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class FindPostImages {
    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    private FindPosts post;
}
