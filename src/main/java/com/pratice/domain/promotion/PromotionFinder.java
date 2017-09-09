package com.pratice.domain.promotion;

import com.pratice.application.promotion.Promotion;

public interface PromotionFinder {
	Promotion find(final Long id);
}
