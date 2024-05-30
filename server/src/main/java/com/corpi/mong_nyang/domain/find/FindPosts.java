package com.corpi.mong_nyang.domain.find;

import com.corpi.mong_nyang.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class FindPosts {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JoinColumn(name = "user_id")
    private User author;
}
