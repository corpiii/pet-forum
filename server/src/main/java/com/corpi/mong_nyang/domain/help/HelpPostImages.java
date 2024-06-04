package com.corpi.mong_nyang.domain.help;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HelpPostImages {
    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    @NonNull
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private HelpPosts post;

    public static HelpPostImages of(String url) {
        return new HelpPostImages(url);
    }
}
