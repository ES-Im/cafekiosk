# Mock / MockMvc / Mockito 정리

## Mock

---
### MockMvc란?
  * 실제 서버를 띄우지 않고 Mock 객체를 사용해 Spring MVC 요청/응답 흐름을 재현하는 테스트 프레임워크
  * `내가 제어할 수 없는 행위` 에 대해서 mocking을 사용한다. 이부분은 [성공 or 실패]를 가정(stubbing)하고 테스트를 작성한다

### Mock이란
* 실제 객체를 대신하는 가짜 객체
* 실제 로직을 실행하지 않고, 테스트에서 내가 정의한 동작만 수행한다.
* 의존 관계가 테스트를 방해할 때<br>
  외부 시스템, 네트워크, DB의 실제 동작을 가정하지 않고, “정상적으로 동작한다”는 전제하에 테스트하기 위함
### Mock 사용 방법
`@WebMvcTest(controllers = ProductController.class)`

1. 컨트롤러 계층만 로딩하는 테스트 어노테이션
2. 테스트 대상 컨트롤러를 명시해야 한다.
3. Service, Repository 등 하위 레이어 빈은 자동으로 로딩되지 않는다.

### @ MockBean 
`import org.springframework.boot.test.mock.mockito.MockBean;`
  * Spring Context에 Mock 객체를 Bean으로 등록하기 위해 필요
  * 기존 Bean이 있다면 제거하고 Mock 객체로 대체한다.
  * [Mockito 라이브러리](site.mockito.org) 사용 : spring-boot-starter-test 의존성에 포함됨

### @WebMvcTest + @MockBean 예시
```aiignore
@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean;
    private ProductService productService;
```
  * Presentation Layer 테스트에서는
    하위 레이어(Service 등)를 Mock 처리하여 단위 테스트 느낌으로 진행한다.
  * @MockBean을 사용하면 :
    * Spring Context에 등록된 실제 ProductService Bean을 제거하고
    * Mockito가 만든 Mock 객체를 대신 등록한다.
    * 따라서 아래 코드는 필드에 Mock 객체가 주입되어 @WebMvcTest가 가능해진다.<br>
     `@MockitoBean private ProductService productService;`

<br>

### @WebMvcTest 중 설정 Bean 오류 해결

---
  * 발생오류 예시<br>
  `Error creating bean with name 'jpaAuditingHandler': Cannot resolve reference to bean 'jpaMappingContext' while setting constructor argument`
  * 원인 
    * WebConfig 설정파일에 JPA Auditing(@EnableJpaAuditing) 설정이 존재하지만, 컨트롤러 전용 테스트라서 빈생성이 안됨
  * 해결
    * JPA Auditing 설정을 별도의 Configuration으로 분리
    ```
    @Configuration
    @EnableJpaAuditing
    public class JpaAuditionConfig {}
    ```
<br>

### ObjectMapper

---
#### Post 요청과 직렬화 / 역직렬화
* POST 요청은 객체를 그대로 보내지 않는다. 객체를 **직렬화된 데이터(JSON 등)**로 변환해 전송한다.

__직렬화 (Serialization)__
* 객체 → 문자열 / 바이트 (네트워크는 객체 자체를 전송할 수 없기 때문)
* 대표 포맷: JSON, XML, Binary

__역직렬화 (Deserialization)__

* 문자열 / 바이트 → 객체 (Spring이 자동 처리)

```
 [클라이언트]
   객체
    ↓ << 직렬화 >>
  JSON 문자열
    ↓ POST Body
 ==================== 네트워크 ====================
    ↓
 [서버]
  JSON 문자열
    ↓ << 역직렬화 >>
   객체
```
#### ObjectMapper를 쓰는 이유
* HTTP Body에는 직렬화된 값만 들어갈 수 있다. <br>
  -> ObjectMapper는 JSON ↔ Object 간의 직렬화 ↔ 역직렬화 변환을 도와주는 Jackson 라이브러리의 핵심 클래스
### 테스트 방법 - 예시
```
class ProductControllerTest {
    ...(생략)    
    @Autowired ObjectMapper objectMapper;               // Post방식 직렬화 <> 역질력화

    @Test
    void createProduct() throws Exception {
        // given - 객체 생성
        ProductCreateRequest request = ProductCreateRequest.builder()......

        // when & then  - Http Body에 데이터 JSON 형태로 직렬화
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/new")     // 앤드포인트
                .content(objectMapper.writeValueAsBytes(request))               // 직렬화
                .contentType(MediaType.APPLICATION_JSON)                        // JSON
        )
                .andDo(MockMvcResultHandlers.print())                           // 목에대한 자세한 로그를 볼 수 있다.
                .andExpect(MockMvcResultMatchers.status().isOk());              // 200 code 기대

    }
}
```


# Mockito로 Stubbing하기

---
## Stubbing 정의
* Mock 객체의 메서드 동작을 미리 정의하는 것
* 실제 메서드 로직은 실행되지 않는다.
* 테스트에서 원하는 결과를 안정적으로 얻기 위함

## Stubbing을 사용하는 이유 
* 메일 전송과 같은 로직은:
  <br>네트워크를 타고 외부 시스템에 의존하며 테스트 대상의 핵심이 아니다.
  <br>따라서 해당 컴포넌트를 Mock 처리하고, 메서드 호출 결과만 정의하여 테스트한다.

## Stubbing 예시
『 OrderStatisticsServiceTest 』
```
@MockBean
private MailSendClient mailSendClient;

Mockito.when(
mailSendClient.sendEmail(
any(String.class),
any(String.class),
any(String.class),
any(String.class)
)
).thenReturn(true);
```
1. any(String.class) : ArgumentMatchers 제공, String 타입의 어떤 값이 와도 매칭된다 (null 포함)
2. thenReturn(true) : 실제 구현과 무관하게, sendEmail() 메서드가 호출하면 항상 `true`를 반환

# Test Double

---
## Test Double 종류 - [Mocks Aren't Stubs - Martin Fowler](https://martinfowler.com/articles/mocksArentStubs.html)
* Dummy : 아무 것도 하지 않는 깡통 객체
* Fake : 단순한 형태로 동일한 기능은 수행하나, 프로덕션에서 쓰기에는 부족한 객체 (ex. FakeRepository)
* Stub : 테스트에서 요청한 것에 대해 미리 준비한 결과를 제공하는 객체 그 외에는 응답하지 않는다.
* Spy : Stub이면서 호출된 내용을 내부적으로 기록하여 보여줄 수 있는 객체<br> 일부는 실제 객체처럼 동작시키고 일부만 Stubbing할 수 있다.
* Mock : 행위에 대한 기대를 정의를 하고 그에 따라 동작하도록 만들어진 객체

## Stub vs Mock
* 공통점
  * 둘 다 실제 객체를 대신하는 가짜 객체(Test Double)
* 차이점 : Stub은 “요청 결과로 상태가 어떻게 변했는지”를 검증하고, Mock은 “어떤 요청이 실제로 발생했는지”를 검증한다.

  | 구분 | Stub | Mock |
  |-----|------|------|
  | 기본 역할 | 요청에 대해 **미리 정해진 결과를 반환** | 요청에 대해 **미리 정해진 결과를 반환** |
  | 검증 목적 | **상태(State) 검증** | **행위(Behavior) 검증** |
  | 검증 초점 | 메서드 호출 이후 객체의 **내부 상태가 어떻게 변했는가** | 어떤 메서드가 **호출되었는지 / 몇 번 / 어떤 인자로** |
  | 검증 방식 | get() 등으로 값 조회 | verify()로 호출 여부 검증 |
  | 관심사 | 결과 데이터 | 상호작용(Interaction) |
  | 예시 설명 | 요청 후 Stub의 상태가 바뀌었는지 확인 | sendMail이 실제로 호출되었는지 확인 |

### Stub 예제 : “어떤 기능을 요청했고, 그 결과 스텁의 상태가 어떻게 바뀌었는지 get으로 상태를 검증”

[Stub 클래스]
```
- 인터페이스
public interface MailService {      
  public void send (Message msg);
}

- Stub 구현체
public class MailServiceStub implements MailService {
  private List<Message> messages = new ArrayList<Message>();
  public void send (Message msg) {
    messages.add(msg);
  }
  public int numberSent() {
    return messages.size();
  }
} 
```
[테스트 코드]
```
public void testOrderSendsMailIfUnfilled() {
    Order order = new Order(TALISKER, 51);      -- given
    MailServiceStub mailer = new MailServiceStub();
    order.setMailer(mailer);
    order.fill(warehouse);                      -- when
    assertEquals(1, mailer.numberSent());       -- then (상태 검증)
}
```
### Mock 예제 : “when으로 sendMail 했을 때 어떤 값을 리턴할 거야, 이런 행위 자체를 중심으로 검증”

[Mock 테스트 코드]

```
public void testOrderSendsMailIfUnfilled() {
    Order order = new Order(TALISKER, 51);
    Mock warehouse = mock(Warehouse.class);
    Mock mailer = mock(MailService.class);
    order.setMailer((MailService) mailer.proxy());

    mailer.expects(once()).method("send");              -- 행위검증 : "send"문자열을 담아 매서드를 한번(once)부를 때
    warehouse.expects(once()).method("hasInventory")
      .withAnyArguments()
      .will(returnValue(false));

    order.fill((Warehouse) warehouse.proxy());
}
```

[Mock + Stubbing]
```
@MockBean
MailSendClient mailSendClient;
void sendOrderStatisticsMail() {
    ....(생략)
    Mockito.when(
        mailSendClient.sendEmail(
            any(String.class),
            any(String.class),
            any(String.class),
            any(String.class)
        )
    ).thenReturn(true);
}
```

# 순수 Mockito로 검증해보기

---
Spring Context를 띄우지 않고, Mockito만 사용해서 의존성을 제어하고 행위를 검증하는 단위 테스트를 작성한다.
* 대상 : `MailServiceForMockSpyTest` 
  * `MailService` : 메일 전송 흐름을 조율, 성공 시 히스토리 저장
  * `MailSendClient` : 실제 메일 전송 (외부 API)
  * `MailSendHistoryRepository` : 메일 전송 이력 저장

### Mock 객체는 아무 설정을 하지 않으면 기본 반환값을 반환한다
| 타입         | 기본 반환 |
| ---------- | ----- |
| boolean    | false |
| int        | 0     |
| 객체         | null  |      
| Collection | empty |
→ save()는 null반환, 예외 발생치 않음, 테스트는 통과함. 그러므로 행위 검증(verify)이 중요해진다 <br>
👉 Mockito 테스트의 핵심은 verify
```
verify(mailSendHistoryRepository, times(1))
.save(any(MailSendHistory.class));
```
[위 코드의 의미]
- Mockito의 verify를 사용하여  `verify()`
- mailSendHistoryRepository Mock 객체의 save() 메서드가 `verify(mailSendHistoryRepository,`
- MailSendHistory 타입의 어떤 객체를 인자로 받아 `.save(any(MailSendHistory.class))`
- 정확히 1번 호출되었는지를 검증한다. `times(1)`


## 1단계 : Mockito를 직접 사용하기 
### 의존성 직접 주입 후 Mock verify 검증
```
MailSendClient mailSendClient = mock(MailSendClient.class);
MailSendHistoryRepository mailSendHistoryRepository = mock(MailSendHistoryRepository.class);

MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository);
```

## 2단계: `@Mock` / `@InjectMocks`로 리팩토링
### Mockito Extension 등록을 해야 `@Mock` `@InjectMocks`이 동작
```
@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    @Mock private MailSendClient mailSendClient;                        -- @Mock으로 가짜 객체 생성
    @Mock private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks private MailService mailService;                       -- @InjectMocks로 DI
}

```
## 3단계 : `@Spy` test double 사용
| 구분       | Mock    | Spy           |
| -------- | ------- | ------------- |
| 실제 객체 기반 | ❌       | ⭕             |
| 기본 동작    | 가짜      | 실제            |
| 로그 확인    | ❌       | ⭕             |
| 권장 사용    | 대부분의 경우 | 일부 메서드만 추적할 때 |
* 사실상 spy라는 TestDouble은 Mock + verify()와의 기능이 유사하다.
* 다만 로그를 확인하고 싶거나, 추적대상 클래스내에 기능이 많을 경우는 spy를 사용하는 것이 유리하다.
* “일부만 실제, 일부만 가짜”가 필요할 때 사용


* 추적 대상 객체는 @Spy로 실제 빈을 등록한다.
```
@Spy private MailSendClient mailSendClient;         ← 해당 빈의 특정 메서드를 추적
@Mock private MailSendHistoryRepository mailSendHistoryRepository;
@InjectMocks private MailService mailService;       ← DI는 Mock방식과 같다.
```
* doReturn(value).when(spy).method(); 방식으로 사용한다
    - 실제 메서드 실행 x
    - 원하는 값만 반환
```
@Test
@DisplayName("메일 전송 테스트")
void sendMail() {
    // given
    doReturn(true)
    .when(mailSendClient)
    .sendEmail(anyString(), anyString(), anyString(), anyString());

    // when
    boolean result = mailService.sendMail("", "", "", "");

    // then
    assertThat(result).isTrue();
    verify(mailSendHistoryRepository, times(1))
            .save(any(MailSendHistory.class));
}
```

# BDDMockito

---
* 테스트 코드 : `MailServiceForBDDTest`
* BDDMockito는 Mockito의 다른 API 표현일 뿐, 내부 동작은 Mockito와 완전히 동일
* 테스트 흐름을 given / when / then 구조에 맞게 표현하기 위한 문법 차이만 있다.
* 즉, 기능적 차이는 없고 가독성과 의도 표현을 위한 선택지이다.
* verify, then 영역은 기존 Mockito와 동일


* //given 줄에서의 Mockito방식 : `when`으로 시작하고 `thenReturn`으로 끝난다
    ```
    Mockito.when(mailSendClient.sendEmail(
            anyString(), anyString(), anyString(), anyString())
    ).thenReturn(true);
    ```
* //given 줄에서의 BBDMockito방식 : `given`으로 시작하고 `willReturn`으로 끝난다.
    ```
    BDDMockito.given(
            mailSendClient.sendEmail(
                    anyString(), anyString(), anyString(), anyString())
    ).willReturn(true);
    ```
  
# Classicist VS. Mockist

---
## Mockist

---
### 정의
* Mock 중심 테스트 전략
* 하위 의존성을 대부분 Mock 처리
* 빠르고 가벼운 테스트를 선호
* 테스트 대상만 “잘렸는지”를 검증
### 관점 정리
* 이미 하위 레이어는 단위테스트로 검증
* 전체를 엮어서 테스트할 필요가 없으므로 필요한 부분만 잘라서 빠르게 테스트 하자
### 한계
* 실제 객체 간 연동 시 발생하는 문제는 잡기 어려움
* “실제 프로덕션 환경과 동일하다”고 말하기 어려움

## Classicist 

---
### 정의
* 실제 객체 중심 테스트 전략
* 최대한 실제 구현체를 사용
* 객체 간 협업을 중요하게 봄
* 통합 관점에서의 안정성을 중시

### 관점 정리
* 실제 프로덕션에서는: A 객체 + B 객체 + C 객체가 같이 협업
* 단위 테스트는 "조합 안정성"을 보장하지 않는다
* 사이드 이펙트를 고려해야한다. 비용은 들지라도 운영 리스크를 줄여야한다.

### 한계
* 설정 복잡
* 실패 시 원인 추적 어려움

## 그럼 Mock을 언제 써야하는가? (Classicist 관점)
### 반드시 Mock 해야 하는 대상 
 * 외부 API : 메인전송, 결제 시스템, 외부 메시지 큐 등
 * 외부에서 만든 시스템이며, 테스트에서 실제 호출이 불가능한 모듈
 * 즉, 외부 의존성은 Mock으로 
### Mock을 피해야하는 대상
 * 내부 도메인 / 내부 로직 : 도메인 객체 및 비즈니스 로직
 * 이들은 실제 객체로 테스트 가능하며 내부에서 제어가 가능하다.
### 레이어별 테스트 전략
| 레이어        | 테스트 방식           |
| ---------- | ---------------- |
| Repository | 실제 DB 기반 테스트     |
| Service    | 실제 하위 레이어 포함 테스트 |
| Controller | 하위 레이어 Mock 처리   |
👉 프레젠테이션 레이어만 Mock 위주