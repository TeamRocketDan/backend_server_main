package com.rocket.user.userfeed.entity;

import com.rocket.config.jpa.entitiy.BaseEntity;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "feed")
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // FK

    private String title; // 제목
    private String content; // 내용
    private String rcate1; // 지역 1 Depth, 시도 단위
    private String rcate2; // 지역 2 Depth, 구 단위
    private String longitude; // 경도
    private String latitude; // 위도

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feed", fetch = FetchType.LAZY)
    private List<FeedImage> feedImage = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feed", fetch = FetchType.LAZY)
    private List<FeedComment> feedComment = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feed", fetch = FetchType.LAZY)
    private List<FeedLike> feedLike = new ArrayList<>();
    private LocalDateTime deletedAt; // 삭제 날짜

    public void updateFeed(FeedDto feedDto) {
        this.title = feedDto.getTitle();
        this.content = feedDto.getContent();
        this.rcate1 = feedDto.getRcate1();
        this.rcate2 = feedDto.getRcate2();
        this.longitude = feedDto.getLongitude();
        this.latitude = feedDto.getLatitude();
    }

    // TODO: CASCADE 옵션 부여하기

    @Override
    public String toString() {
        return "Feed{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", rcate1='" + rcate1 + '\'' +
            ", rcate2='" + rcate2 + '\'' +
            ", longitude='" + longitude + '\'' +
            ", latitude='" + latitude + '\'' +
//            ", feedImage='" + feedImage.toString() + '\'' +
//            ", feedLike='" + feedLike.toString() + '\'' +
//            ", feedComment='" + feedComment.toString() + '\'' +
//            ", FeedCommentLike='" + FeedCommentLike.toString() + '\'' +
            ", deletedAt=" + deletedAt +
            '}';
    }
}
