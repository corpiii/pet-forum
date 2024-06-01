package com.corpi.mong_nyang.domain.help;

import com.corpi.mong_nyang.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class HelpPostComments {
    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @NonNull
    private String content;

    @Column(name = "created_at")
    @NonNull
    private LocalDateTime createdAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private HelpPosts post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NonNull
    private User author;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private HelpPostComments parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<HelpPostComments> children;

    static HelpPostComments of(String content, User author) {
        return new HelpPostComments(content, LocalDateTime.now(), author);
    }

    public void replyComment(HelpPostComments comment) {
        this.children.add(comment);
        comment.parent = this;
    }
}
