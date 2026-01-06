package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
@SpringBootTest
class ProductServiceTest {
    @Autowired
    ProductService  productService;
    @Autowired
    ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 쵯근 상품의 상품번호에서 1 증가한 값이다.")
    void createProduct() {
        // given
        Product product1 = createProduct("001", HANDMADE, SELLING, 4000, "아메리카노");
        productRepository.save(product1);

        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .price(5000)
                .name("카푸치노")
                .sellingType(SELLING)
                .build();

        // when
        ProductResponse productResponse = productService.createProduct(request.toProductCreateServiceRequest());

        // then
        assertThat(productResponse)
                .extracting(ProductResponse::getProductNumber, ProductResponse::getName, ProductResponse::getPrice, ProductResponse::getSellingType, ProductResponse::getType)
                .contains("002","카푸치노", 5000, SELLING, HANDMADE);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2)
                .extracting(Product::getProductNumber, Product::getName, Product::getPrice, Product::getSellingType, Product::getType)
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", 4000, SELLING, HANDMADE),
                        tuple("002", "카푸치노", 5000, SELLING, HANDMADE)
                );
    }

    @Test
    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록할때, 상품번호는 001이다")
    void createProductWhenProductsIsEmpty() {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .price(5000)
                .name("카푸치노")
                .sellingType(SELLING)
                .build();

        // when
        ProductResponse productResponse = productService.createProduct(request.toProductCreateServiceRequest());

        // then
        assertThat(productResponse)
                .extracting(ProductResponse::getProductNumber, ProductResponse::getName, ProductResponse::getPrice, ProductResponse::getSellingType, ProductResponse::getType)
                .contains("001","카푸치노", 5000, SELLING, HANDMADE);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1)
                .extracting(Product::getProductNumber, Product::getName, Product::getPrice, Product::getSellingType, Product::getType)
                .containsExactlyInAnyOrder(
                        tuple("001", "카푸치노", 5000, SELLING, HANDMADE)
                );
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