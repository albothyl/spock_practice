package com.pratice.application.promotion

import com.pratice.domain.member.MemberFinder
import com.pratice.domain.order.OrderFinder
import com.pratice.domain.promotion.PromotionFinder
import io.github.benas.randombeans.api.EnhancedRandom
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDateTime

import static com.pratice.application.promotion.PromotionType.*
import static java.time.LocalDateTime.now

class AvailablePromotionCheckerTest extends Specification {

	Long memberId
	Long promotionId

	def setup() {
		memberId = EnhancedRandom.random(Long.class)
		promotionId = EnhancedRandom.random(Long.class)
	}

	@Unroll("Promotion Condition [USED : #USED | PromotionPeriod : #START_AT ~ #END_AT] Member and Order Condition[LOGIN : #LOGIN | PURCHASE_HISTORY : #PURCHASE_HISTORY | #OVERSEAS_PURCHASE_HISTORY] 이면 #RESULT 에 해당하는 Promotion의 혜택을 받을 수 있다.")
	def "회원이 혜택을 받을 수 있는 프로모션의 리스트를 확인한다."() {
		given: "promotion, member, order Mock을 sut에 inject한다."
		def promotionFinderMock = Mock(PromotionFinder) {
			find(_) >> getPromotion(USED, START_AT, END_AT)
		}
		def memberFinderMock = Mock(MemberFinder) {
			isLogin(_) >> LOGIN
		}
		def orderFinderMock = Mock(OrderFinder) {
			isExistPurchaseHistory(_) >> PURCHASE_HISTORY
			isExistOverseasPurchaseHistory(_) >> OVERSEAS_PURCHASE_HISTORY
		}

		@Subject
		def sut = new AvailablePromotionChecker(
				memberFinder: memberFinderMock,
				orderFinder: orderFinderMock,
				promotionFinder: promotionFinderMock
		)

		when: "getAvailablePromotionList를 호출하여 사용자가 어떤 프로모션 혜택을 받을 수 있는지 확인한다."
		List<PromotionType> checkResult = sut.getAvailablePromotionList(memberId, promotionId)

		then:
		checkResult.containsAll(RESULT)

		where: "체크 결과와 where의 RESULT를 비교한다."
//		Promotion Conditions                                        | Member & Order Conditions
		USED  | START_AT                 | END_AT                   | LOGIN | PURCHASE_HISTORY | OVERSEAS_PURCHASE_HISTORY || RESULT
		false | now().minusDays(1) | now().plusDays(1)  | false | true             | true                      || []
		true  | now().plusDays(1)  | now().plusDays(1)  | false | true             | true                      || []
		true  | now().minusDays(2) | now().minusDays(1) | false | true             | true                      || []
		true  | now().minusDays(1) | now().plusDays(1)  | false | true             | true                      || []
		true  | now().minusDays(1) | now().plusDays(1)  | false | true             | true                      || []

		true  | now().minusDays(1) | now().plusDays(1)  | true  | true             | true                      || [COMMON]
		true  | now().minusDays(1) | now().plusDays(1)  | true  | false            | true                      || [COMMON, FIRST_PURCHASE]
		true  | now().minusDays(1) | now().plusDays(1)  | true  | false            | false                     || [COMMON, FIRST_PURCHASE, FIRST_OVERSEAS]
	}

	@Unroll("구매한 이력이 #PURCHASE_HISTORY 이면 결과는 #RESULT 이다.")
	def "첫 구매 프로모션 대상인지 확인한다."() {
		given:
		def orderFinderMock = Mock(OrderFinder) {
			isExistPurchaseHistory(_) >> PURCHASE_HISTORY
		}

		@Subject
		def sut = new AvailablePromotionChecker(
				orderFinder: orderFinderMock
		)

		when:
		def checkResult = sut.isFirstPurchase(memberId)

		then:
		checkResult == RESULT

		where:
		PURCHASE_HISTORY || RESULT
		true             || false
		false            || true
	}

	@Unroll("해외구매한 이력이 #PURCHASE_HISTORY 이면 결과는 #RESULT 이다.")
	def "첫 해외구매 프로모션 대상인지 확인한다."() {
		given:
		def orderFinderMock = Mock(OrderFinder) {
			isExistOverseasPurchaseHistory(_) >> PURCHASE_HISTORY
		}

		@Subject
		def sut = new AvailablePromotionChecker(
				orderFinder: orderFinderMock
		)

		when:
		def checkResult = sut.isFirstOverseas(memberId)

		then:
		checkResult == RESULT

		where:
		PURCHASE_HISTORY || RESULT
		true             || false
		false            || true
	}

	def getPromotion(Boolean used, LocalDateTime startAt, LocalDateTime endAt) {
		def promotionRandom = EnhancedRandom.random(Promotion.class, "used", "startAt", "endAt")
		promotionRandom.used = used
		promotionRandom.startAt = startAt
		promotionRandom.endAt = endAt

		return promotionRandom
	}
}
