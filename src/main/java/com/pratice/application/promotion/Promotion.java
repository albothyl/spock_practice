package com.pratice.application.promotion;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class Promotion {
	private Long id;
	private String name;
	private Boolean used;
	private LocalDateTime startAt;
	private LocalDateTime endAt;

	public boolean isPromotionAvailable() {
		if (this.getUsed() == false) {
			return false;
		}

		final LocalDateTime now = LocalDateTime.now();

		if (now.isBefore(this.startAt)) {
			return false;
		}

		if (now.isAfter(this.endAt)) {
			return false;
		}

		return true;
	}
}
