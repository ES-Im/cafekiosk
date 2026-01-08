package sample.cafekiosk.spring.api.service.order;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sample.cafekiosk.spring.client.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.order.OrderStatus;
import sample.cafekiosk.spring.domain.orderProduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@SpringBootTest
class OrderStatisticsServiceTest {

    @Autowired
    OrderStatisticsService orderStatisticsService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    MailSendHistoryRepository mailSendHistoryRepository;
    @Autowired
    OrderProductRepository orderProductRepository;
    @MockitoBean
    MailSendClient  mailSendClient;


    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }
    
    @Test
    @DisplayName("메출 통계 메일을 전송한다.")
    void sendOrderStatisticsMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2026,1,1,0,0);

        Stock stock1 = Stock.create("001", 3);
        Stock stock2 = Stock.create("002", 2);
        Stock stock3 = Stock.create("003", 2);
        stockRepository.saveAll(List.of(stock1, stock2, stock3));

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 2000);
        Product product3 = createProduct(HANDMADE, "003", 3000);
        List<Product>  products = List.of(product1, product2, product3);

        productRepository.saveAll(products);

        Order order1 = createPaymentCompltedOrder(LocalDateTime.of(2026,1, 1, 0, 0,1), products);   // 히스토리 기록1
        Order order2 = createPaymentCompltedOrder(LocalDateTime.of(2026,1,1,1,0,0), products);  // 히스토리 기록2
        Order order3 = createPaymentCompltedOrder(LocalDateTime.of(2026,1,3,0,0), products);
        Order order4 = createPaymentCompltedOrder(LocalDateTime.of(2026,1,4,0,0), products);

        // any -> ArgumentMatchers 라이브러리 사용 any(String.class) = String값인 어떤 값이든 좋다
        // 목객체의 행위를 정의 : sendMail을 호출했을때 String값 네개를 받을거고 true를 반환할거야   → 실제 메서드 결과와 상관없이 여기서 정의한 대로 객체가 설정이 된다.
        Mockito.when(mailSendClient.sendEmail(any(String.class), any(String.class),any(String.class),any(String.class)))
                .thenReturn(true);

        // when
        boolean result = orderStatisticsService.sendOrderStatisticsMail("test@cafekiost.com", LocalDate.of(2026, 1, 1));

        // then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting(MailSendHistory::getContent)
                .contains("총 매출 합계는 12000원 입니다");


    }

    private Order createPaymentCompltedOrder(LocalDateTime registeredDateTime, List<Product> list) {
        return orderRepository.save(
                Order.builder()
                    .products(list)
                    .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                    .registeredDateTime(registeredDateTime)
                    .build()
        );
    }

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingType(SELLING)
                .name("메뉴 이름")
                .build();
    }
}