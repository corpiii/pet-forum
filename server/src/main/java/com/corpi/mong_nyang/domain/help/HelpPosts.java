package com.corpi.mong_nyang.domain.help;

import com.corpi.mong_nyang.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HelpPosts {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String content;

    @Column(name = "created_at")
    @NonNull
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NonNull
    private User userId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<HelpPostComments> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<HelpPostImages> images = new ArrayList<>();

    public static HelpPosts of(String title, String content, User user, List<HelpPostImages> images) {
        LocalDateTime current = LocalDateTime.now();
        HelpPosts posts = new HelpPosts(title, content, current, user);

        for (HelpPostImages image : images) {
            posts.addImage(image);
        }

        return posts;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void replyComment(HelpPostComments comments) {
        this.comments.add(comments);
        comments.setPost(this);
    }

    public void addImage(HelpPostImages image) {
        this.images.add(image);
    }

    public void removeImageAt(int index) {
        this.images.remove(index);
    }
}
