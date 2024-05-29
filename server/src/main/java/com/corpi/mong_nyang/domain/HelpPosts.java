package com.corpi.mong_nyang.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Getter
public class HelpPosts {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user_id;

    private String title;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
