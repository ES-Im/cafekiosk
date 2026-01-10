package sample.cafekiosk.spring.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .apply(documentationConfiguration(provider))
                .build();
    }

    protected abstract Object initController();
}
/*
 * MockMVC 통합 테스트 시에는 스프링에서 만들어진 MockMVC를 자동으로 주입해줬지만 해당 support에서는 빌더를 통해서 만들 것. 이 빌드 방식에서 두가지 메서드가 있음
 * 1. MockMvcBuilders.webAppContextSetup(WebApplicationContext, RestDocumentationContextProvider) -> WebApplicationContext를 쓰므로 @SpringBootTest를 띄우게 된다
 *    → 스프링 한번 띄우는데 시간이 몇 초 더 소모 되는데, 굳이 문서를 만들때도 스프링을 띄울 필욘 없음. 그래서 2번을 선호
 * 2. MockMvcBuilders.standaloneSetup(문서로 만들고 싶은 컨트롤러) : 스프링 의존성 없이 MockMVC를 스탠드 언론으로 만들어서 띄우므로 각 컨트롤러에 대한 단위 테스트로 테스팅하게 된다.
 *    → 상속받는 클래스에서 위 컨트롤러를 주입하면 된다. → initController()
 */