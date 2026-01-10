package sample.cafekiosk.spring.docs.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import sample.cafekiosk.spring.api.controller.product.ProductController;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.docs.RestDocsSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

public class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @Test
    @DisplayName("신규 상품을 등록하는 API")
    void createProduct() throws Exception {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingType(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

         given(productService.createProduct(any(ProductCreateServiceRequest.class)))
                 .willReturn(ProductResponse.builder()
                         .id(1L)
                         .productNumber("001")
                         .type(HANDMADE)
                         .sellingType(SELLING)
                         .name("아메리카노")
                         .price(4000)
                         .build()
                 );


        mockMvc.perform(
                        post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())     // 여기까지는 일반 테스트 로직과 같음

                .andDo(document("product-create",
                    preprocessRequest(prettyPrint()),       // JSON을 이쁘게 만들어줌
                    preprocessResponse(prettyPrint()),

                    requestFields(
                        // ProductCrateRequest 부분
                        fieldWithPath("type").type(JsonFieldType.STRING)
                                .description("상품 타입"),
                        fieldWithPath("sellingType").type(JsonFieldType.STRING)
                            .optional()                                             // request-fields.snippet에서 커스텀 한 컬럼
                            .description("상품 판매상태"),
                        fieldWithPath("name").type(JsonFieldType.STRING)
                            .description("상품 이름"),
                        fieldWithPath("price").type(JsonFieldType.NUMBER)
                            .description("상품 가격")
                    ),

                    responseFields(
                        // ApiResponse 부분
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                            .description("코드"),
                        fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("HTTP상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("HTTP상태메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답데이터"),

                        // ProductResponse 부분   - path중 data 하위에 들어간다
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                .description("상품식별자"),
                        fieldWithPath("data.productNumber").type(JsonFieldType.STRING)
                                .description("상품넘버"),
                        fieldWithPath("data.type").type(JsonFieldType.STRING)
                                .description("상품타입"),
                        fieldWithPath("data.sellingType").type(JsonFieldType.STRING)
                                .description("상품 판매유형"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING)
                                .optional()                                             // response-fields.snippet에서 커스텀 한 컬럼
                                .description("상품명"),
                        fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                                .description("상품가격")

                    )

                ));


    }
}
