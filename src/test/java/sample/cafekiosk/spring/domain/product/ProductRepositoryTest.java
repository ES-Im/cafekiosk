package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;


@DataJpaTest
class ProductRepositoryTest {

    @Autowired ProductRepository productRepository;

    @Disabled
    @Test
    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    void findAllBySellingStatusIn() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, 4000, "아메리카노");
        Product product2 = createProduct("002", HANDMADE, HOLD, 4000, "카페라떼");
        Product product3 = createProduct("003", HANDMADE, STOP_SELLING, 8000, "팥빙수");

        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = productRepository.findAllBySellingTypeIn(List.of(SELLING, HOLD));

        // then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "sellingType")
                .containsExactly(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );     // containsExactly 순서까지 일치, containsExactlyInAnyOrder 순서 상관없이
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어온다.")
    void findLatestProductNumber() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, 4000, "아메리카노");
        Product product2 = createProduct("002", HANDMADE, HOLD, 4000, "카페라떼");

        String  targetProductNumber = "003";
        Product product3 = createProduct(targetProductNumber, HANDMADE, STOP_SELLING, 8000, "팥빙수");
        productRepository.saveAll(List.of(product1, product2, product3));
        // when
        String  latestProductNumber = productRepository.findLatestProductNumber();

        // then
        assertThat(latestProductNumber).isEqualTo(targetProductNumber);
    }

    @Disabled
    @Test
    @DisplayName("가장 마지막으로 저장한 상품을 읽어올 때 상품이 하나도 없는 경우에는 null을 반환한다.")
    void findLatestProductNumber2() {
        // when
        String  latestProductNumber = productRepository.findLatestProductNumber();

        // then
        assertThat(latestProductNumber).isNull();
    }

    private static Product createProduct(String productNumber, ProductType productType, ProductSellingStatus productSellingStatus, int price, String name) {
        Product product = Product.builder()
                .productNumber(productNumber)
                .type(productType)
                .name(name)
                .sellingType(productSellingStatus)
                .price(price)
                .build();
        return product;
    }
}