package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class ProductTypeTest {
    
    // csv 소스 이용
    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다")
    @CsvSource({"HANDMADE, false",
                "BOTTLE, true",
                "BAKERY, true"}) // given
    @ParameterizedTest(name = "{index} ==> Type: ''{0}'' 일때 재고타입은 {1}이다")
    void containsStockType1(ProductType productType, boolean expected) {
        // when
        boolean result = ProductType.containsStockType(productType);

        // then
        assertThat(result).isEqualTo(expected);
    }

    // method 소스 사용
    private static Stream<Arguments> provideProductTypesForCheckingStockType() {    // given
        return Stream.of(
                    Arguments.of(ProductType.HANDMADE, false),
                    Arguments.of(ProductType.BAKERY, true),
                    Arguments.of(ProductType.BOTTLE, true)
                );
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다")
    @ParameterizedTest
    @MethodSource("provideProductTypesForCheckingStockType")
    void containsStockType2(ProductType productType, boolean expected) {
        // when
        boolean result = ProductType.containsStockType(productType);
        // then
        assertThat(result).isEqualTo(expected);
    }

/*
 * 리팩토링 ↑ ParameterizedTest : 한 주제에 대해 값을 바꿔가면서 체크하고싶을 때 사용
 */
//    @Test
//    @DisplayName("상품 타입이 재고와 관련없는 HANDMADE 타입이면 False를 반환한다")
//    void containsStockType() {
//        // given
//        ProductType givenType = ProductType.HANDMADE;
//
//        // when
//        boolean result = ProductType.containsStockType(givenType);
//
//        // then
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    @DisplayName("상품 타입이 재고 관련한 BAKERY 타입이면 True를 반환한다.")
//    void containsStockType2() {
//        // given
//        ProductType givenType = ProductType.BAKERY;
//
//        // when
//        boolean result = ProductType.containsStockType(givenType);
//
//        // then
//        assertThat(result).isTrue();
//    }
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