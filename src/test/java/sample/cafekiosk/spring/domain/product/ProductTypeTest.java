package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ProductTypeTest {
    
    @Test
    @DisplayName("상품 타입이 재고와 관련없는 HANDMADE 타입이면 False를 반환한다")
    void containsStockType() {
        // given
        ProductType givenType = ProductType.HANDMADE;

        // when
        boolean result = ProductType.containsStockType(givenType);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("상품 타입이 재고 관련한 BAKERY 타입이면 True를 반환한다.")
    void containsStockType2() {
        // given
        ProductType givenType = ProductType.BAKERY;

        // when
        boolean result = ProductType.containsStockType(givenType);

        // then
        assertThat(result).isTrue();
    }
/*
 * 리팩토링 ↑ : 한문단에는 한 주제를 넣는다.
 */
//    @Test
//    @DisplayName("상품 타입이 재고 관련 타입인지 체크한다,")
//    void containsStockTypeEx() {
//        // given
//        ProductType[] productTypes = ProductType.values();
//
//        for (ProductType productType : productTypes) {
//            if(productType == ProductType.BAKERY || productType == ProductType.BOTTLE) {
//                // when
//                boolean result = ProductType.containsStockType(productType);
//
//                // then
//                assertThat(result).isTrue();
//            }
//            if(productType == ProductType.HANDMADE) {
//                // when
//                boolean result = ProductType.containsStockType(productType);
//
//                // then
//                assertThat(result).isFalse();
//            }
//        }
//    }

}