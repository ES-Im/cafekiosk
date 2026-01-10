package sample.cafekiosk.spring.api.controller.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.spring.ControllerTestSupport;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("주문을 생성한다")
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001"))
                .build();

        // when // then
        mockMvc.perform(
                post("/api/v1/orders/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문을 생성할때 상품번호는 필수이다.")
    void createOrderWithOutProductNumber() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of())
                .build();

        // when // then
        mockMvc.perform(
                post("/api/v1/orders/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 번호 리스트는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }



}

// OrderControllerService를 ProductControllerTest 보고 혼자 만들어 보고, 강의 마저 보기