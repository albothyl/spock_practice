package com.pratice.domain.member;

import org.springframework.stereotype.Repository;

@Repository
public interface MemberFinder {
	boolean isLogin(final Long memberId);
}
