package com.corpi.mong_nyang.domain.Help;

import com.corpi.mong_nyang.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class HelpPostComments {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private HelpPosts post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private HelpPostComments parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<HelpPostComments> children;
}
