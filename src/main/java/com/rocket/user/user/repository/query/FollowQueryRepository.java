package com.rocket.user.user.repository.query;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.rocket.user.user.entity.QFollow.follow;
import static com.rocket.user.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class FollowQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public void deleteByFollowerAndFollowing(User follower, User following) {
        jpaQueryFactory
                .delete(follow)
                .where(
                        follow.follower.eq(follower)
                                .and(follow.following.eq(following))
                )
                .execute();
    }

    public Page<Follow> findByFollower(User follower, Pageable pageable) {
        List<Follow> content = jpaQueryFactory
                .select(follow)
                .from(follow)
                .innerJoin(follow.following, user).fetchJoin()
                .where(follow.follower.eq(follower))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.follower.eq(follower));

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    public Page<Follow> findByFollowing(User following, Pageable pageable) {
        List<Follow> content = jpaQueryFactory
                .select(follow)
                .from(follow)
                .innerJoin(follow.follower, user).fetchJoin()
                .where(follow.following.eq(following))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(follow.count())
                .from(follow)
                .where(follow.following.eq(following));

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }
}
