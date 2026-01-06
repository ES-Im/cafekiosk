package sample.cafekiosk;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.CafeKiosk;
import sample.cafekiosk.unit.beverages.Americano;
import sample.cafekiosk.unit.beverages.Latte;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class CafekioskApplicationTests {

    @Test
    void add() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano(), 3);

        System.out.println("담긴 음료 수 : " + cafeKiosk.getBeverages().size() );
        System.out.println("담긴 음료 : " + cafeKiosk.getBeverages().get(0).getName());
    }
    @Test
    void addSeveralBeverage() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano, 3);

        System.out.println("담긴 음료 수 : " + cafeKiosk.getBeverages().size() );
        System.out.println("담긴 음료 : " + cafeKiosk.getBeverages().get(0).getName());
    }

    @Test
    void addZeroBeverage() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        assertThatThrownBy(
                () -> cafeKiosk.add(americano, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1잔 이상 주문 가능");
    }

    @Test
    void price() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano(), 1);

        assertThat(cafeKiosk.getBeverages().size()).isEqualTo(1);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }

    @Test
    void remove() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        cafeKiosk.add(americano, 1);
        cafeKiosk.remove(americano);

        assertThat(cafeKiosk.getBeverages()).isEmpty();

    }

    @Test
    void clear() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        cafeKiosk.add(americano, 3);
        cafeKiosk.clear();
        assertThat(cafeKiosk.getBeverages()).isEmpty();

    }

    @Test
    void createOrder() {
        Americano americano = new Americano();
        CafeKiosk cafeKiosk = new CafeKiosk();

        cafeKiosk.add(americano, 2);
        Order order = cafeKiosk.createOrder(LocalDateTime.of(2025, 1, 17, 10, 0));

        assertThat(order.getBeverages()).hasSize(2);
        assertThat(order.getBeverages().get(0).getName()).isEqualTo("아메리카노");

    }

    @Test
    void createOrderOutsideOpenTime() {
        Americano americano = new Americano();
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(americano, 2);
        assertThatThrownBy(() ->
                cafeKiosk.createOrder(LocalDateTime.of(2025, 1, 17, 9, 59)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 목록에 담긴 상품들의 총 금액을 계산할 수 있다.")
    void calculateTotalPrice() {
        // given
        Americano americano = new Americano();
        CafeKiosk cafeKiosk = new CafeKiosk();
        Latte latte = new Latte();

        cafeKiosk.add(americano, 1);
        cafeKiosk.add(latte, 1);

        // when
        int totalPrice = cafeKiosk.calculateTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(8500);
    }


}
