package com.corpi.mong_nyang.domain.help;

import com.corpi.mong_nyang.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class HelpPosts {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<HelpPostComments> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<HelpPostImages> images;
}
