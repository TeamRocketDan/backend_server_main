package com.rocket.user.userfeed.repository.query;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.querydsl.jpa.JPAExpressions.selectOne;
import static com.rocket.user.user.entity.QFollow.follow;
import static com.rocket.user.user.entity.QUser.user;
import static com.rocket.user.userfeed.entity.QFeed.feed;
import static com.rocket.user.userfeed.entity.QFeedComment.feedComment;
import static com.rocket.user.userfeed.entity.QFeedCommentLike.feedCommentLike;
import static com.rocket.user.userfeed.entity.QFeedImage.feedImage;
import static com.rocket.user.userfeed.entity.QFeedLike.feedLike;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedCommentQDto;
import com.rocket.user.userfeed.dto.FeedListDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.dto.QFeedCommentQDto;
import com.rocket.user.userfeed.dto.QFeedListDto;
import com.rocket.user.userfeed.entity.FeedImage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
public class FeedQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<FeedListDto> findByRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc
        (FeedSearchCondition searchCondition,
            User userEntity,
            Pageable pageable,
            boolean isMain) {

        List<FeedListDto> content = jpaQueryFactory
            .select(
                new QFeedListDto(
                    user.id,
                    feed.id,
                    user.profileImage,
                    user.username,
                    user.nickname.coalesce(""),
                    user.email,
                    feed.title,
                    feed.content,
                    feed.rcate1,
                    feed.rcate2,
                    feed.longitude,
                    feed.latitude,
                    isLike(userEntity),
                    isFollow(userEntity),
                    select(feedLike.count())
                        .from(feedLike)
                        .where(feedLike.feed.eq(feed)),
                    select(feedComment.count())
                        .from(feedComment)
                        .where(feedComment.feed.eq(feed))
                )
            )
            .from(feed)
            .innerJoin(feed.user, user)
            .where(
                eqRcate(searchCondition),
                isMain(isMain, userEntity),
                feed.deletedAt.isNull()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(feed.id.desc())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(feed.count())
            .from(feed)
            .where(
                eqRcate(searchCondition),
                isMain(isMain, userEntity),
                feed.deletedAt.isNull()
            );

        return PageableExecutionUtils.getPage(
            content,
            pageable,
            countQuery::fetchOne
        );
    }

    public List<FeedImage> findByAllFeedIn(List<Long> feedsId) {
        return jpaQueryFactory
            .select(feedImage)
            .from(feedImage)
            .innerJoin(feedImage.feed, feed)
            .where(feedImage.feed.id.in(feedsId))
            .orderBy(feedImage.feed.id.desc())
            .fetch();
    }

    public Page<FeedCommentQDto> feedCommentFindByFeedId(
        Long feedId,
        User userEntity,
        Pageable pageable) {

        List<FeedCommentQDto> content = jpaQueryFactory
            .select(
                new QFeedCommentQDto(
                    user.id,
                    user.profileImage,
                    user.nickname,
                    user.username,
                    user.email,
                    feedComment.id,
                    feedComment.feed.id,
                    feedComment.comment,
                    feedComment.createdAt,
                    feedComment.updatedAt,
                    feedCommentLike.count(),
                    selectOne()
                        .from(feedCommentLike)
                        .where(
                            feedCommentLike.feedComment.eq(feedComment),
                            feedCommentLike.user.eq(userEntity)
                        ).exists()
                )
            )
            .from(feedComment)
            .innerJoin(feedComment.user, user)
            .leftJoin(feedCommentLike).on(feedComment.id.eq(feedCommentLike.feedComment.id))
            .where(
                feedComment.feed.id.eq(feedId),
                feedComment.deletedAt.isNull()
            )
            .groupBy(feedComment)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(feedComment.createdAt.desc())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(feedComment.count())
            .from(feedComment)
            .where(
                feedComment.feed.id.eq(feedId),
                feedComment.deletedAt.isNull()
            );

        return PageableExecutionUtils.getPage(
            content,
            pageable,
            countQuery::fetchOne
        );
    }


    private Expression<Boolean> isLike(User user) {
        if (ObjectUtils.isEmpty(user)) {
            return Expressions.FALSE;
        }

        return selectOne()
            .from(feedLike)
            .where(
                feedLike.feed.eq(feed),
                feedLikeUserEqUser(user)
            ).exists();
    }

    private Expression<Boolean> isFollow(User user) {
        if (ObjectUtils.isEmpty(user)) {
            return Expressions.FALSE;
        }
        return selectOne()
            .from(follow)
            .where(
                follow.follower.eq(feed.user)
                , followUserEqFeedUser(user)
            ).exists();
    }

    private BooleanExpression feedUserEqUser(User user) {
        return user == null ? null : feed.user.eq(user);
    }

    private BooleanExpression isMain(boolean isMain, User user) {
        return isMain ? null : feedUserEqUser(user);
    }

    private BooleanExpression feedLikeUserEqUser(User user) {
        return user == null ? null : feedLike.user.eq(user);
    }

    private BooleanExpression followUserEqFeedUser(User user) {
        return user == null ? null : follow.following.eq(user);
    }

    private BooleanExpression eqRcate(FeedSearchCondition feedSearchCondition) {

        if (ObjectUtils.isEmpty(feedSearchCondition.getRcate1())) {
            return null;
        } else if (!ObjectUtils.isEmpty(feedSearchCondition.getRcate1())
            && ObjectUtils.isEmpty(feedSearchCondition.getRcate2())) {
            return feed.rcate1.eq(feedSearchCondition.getRcate1());
        }

        return feed.rcate1.eq(feedSearchCondition.getRcate1())
            .and(feed.rcate2.eq(feedSearchCondition.getRcate2()));
    }
}