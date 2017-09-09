package com.pratice.application.promotion;

import com.google.common.collect.Lists;
import com.pratice.domain.member.MemberFinder;
import com.pratice.domain.order.OrderFinder;
import com.pratice.domain.promotion.PromotionFinder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;

import static com.pratice.application.promotion.PromotionType.*;

public class AvailablePromotionChecker {

	@Autowired
	private MemberFinder memberFinder;

	@Autowired
	private OrderFinder orderFinder;

	@Autowired
	private PromotionFinder promotionFinder;


	public List<PromotionType> getAvailablePromotionList(@Nonnull final Long memberId, final Long promotionId) {

		List<PromotionType> promotionList = Lists.newArrayList();

		final Promotion promotion = promotionFinder.find(promotionId);
		if (!(promotion.isPromotionAvailable())) {
			return promotionList;
		}

		if (memberFinder.isLogin(memberId)) {
			promotionList.add(COMMON);
		}

		if (isFirstPurchase(memberId)) {
			promotionList.add(FIRST_PURCHASE);
		}

		if (isFirstOverseas(memberId)) {
			promotionList.add(FIRST_OVERSEAS);
		}

		return promotionList;
	}

	private boolean isFirstPurchase(final Long memberId) {
		if (orderFinder.isExistPurchaseHistory(memberId)) {
			return false;
		}

		return true;
	}

	private boolean isFirstOverseas(final Long memberId) {
		if (orderFinder.isExistOverseasPurchaseHistory(memberId)) {
			return false;
		}

		return true;
	}
}
