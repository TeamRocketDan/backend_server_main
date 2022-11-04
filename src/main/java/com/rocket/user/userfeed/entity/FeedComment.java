package com.rocket.user.userfeed.entity;


import com.rocket.config.jpa.entitiy.BaseEntity;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedCommentDto;

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
@Table(name = "feed_comment")
public class FeedComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_comment_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed; // FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // FK

    private String comment; // 댓글

    private LocalDateTime deletedAt;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feedComment", fetch = FetchType.LAZY)
//    private List<FeedCommentLike> feedCommentLike = new ArrayList<>();

    public void updateFeedComment(FeedCommentDto feedCommentDto) {
        this.comment = feedCommentDto.getComment();
    }

    @Override
    public String toString() {
        return "FeedComment{" +
            "user=" + user.getId() +
            ", comment=" + comment +
            '}';
    }
}
