package com.rocket.user.user.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rocket.user.user.dto.QUserMypageDto;
import com.rocket.user.user.dto.UserMypageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.rocket.user.user.entity.QFollow.follow;
import static com.rocket.user.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<UserMypageDto> findById(Long userId) {
        return Optional.ofNullable(jpaQueryFactory
            .select(
                new QUserMypageDto(
                    user.id,
                    user.username,
                    user.email,
                    user.nickname.coalesce(""),
                    user.profileImage,
                    select(follow.count())
                        .from(follow)
                        .where(follow.follower.id.eq(user.id)),
                    select(follow.count())
                        .from(follow)
                        .where(follow.following.id.eq(user.id))
                )
            )
            .from(user)
            .where(
                user.id.eq(userId),
                user.deletedAt.isNull()
            )
            .fetchOne()
        );
    }
}
