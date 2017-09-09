package com.pratice.domain.order;

public interface OrderFinder {
	boolean isExistPurchaseHistory(final Long memberId);
	boolean isExistOverseasPurchaseHistory(final Long memberId);
}
