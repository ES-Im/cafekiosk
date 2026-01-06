package sample.cafekiosk.unit.beverages;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AmericanoTest {

    @Test
    void getName() {
        Americano americano = new Americano();
        assertEquals(americano.getName(), "아메리카노"); //  junit의 assert문
        assertThat(americano.getName()).isEqualTo("아메리카노"); // assertj의 assertThat문 → 메서드 체이닝으로 더 풍부한 검증 가능
    }


}