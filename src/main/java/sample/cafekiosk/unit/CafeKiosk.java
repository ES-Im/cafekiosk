package sample.cafekiosk.unit;


import lombok.Getter;
import sample.cafekiosk.unit.beverages.Beverage;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/*
 * 음료 주문 및
 */

@Getter
public class CafeKiosk {

    private final List<Beverage> beverages =  new ArrayList<>();
    private static final LocalTime SHOP_OPEN_TIME = LocalTime.of(10, 0);
    private static final LocalTime SHOP_CLOSE_TIME = LocalTime.of(22, 0);



    public void add(Beverage beverage, int count) {
        if(count <= 0) {
            throw new IllegalArgumentException("1잔 이상 주문 가능");
        }
        for (int i = 0; i < count; i++) {
            beverages.add(beverage);
        }
    }

    public int calculateTotalPrice() {
        int totalPrice = 0;
        for (Beverage beverage : beverages) {
            totalPrice += beverage.getPrice();
        }
        return totalPrice;
    }

    public void clear() {
        beverages.clear();
    }

    public void remove(Beverage beverage) {
        beverages.remove(beverage);
    }


    public Order createOrder(LocalDateTime currentDateTime) {
        LocalTime currentTime = currentDateTime.toLocalTime();
        if(currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다");
        }

        return new Order(currentDateTime, beverages);
    }
}
