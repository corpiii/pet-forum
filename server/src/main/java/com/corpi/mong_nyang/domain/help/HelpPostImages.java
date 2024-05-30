package com.corpi.mong_nyang.domain.help;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class HelpPostImages {
    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    private HelpPosts post;
}
