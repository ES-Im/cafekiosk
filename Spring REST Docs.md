API를 설계할 때 프론트엔드 개발자가 함께 있다고 가정하면, <br>
프론트엔드 개발자는 주문 화면, 어드민 화면, 특정 기능 화면 등을 구현하고, <br>
백엔드에서는 그 화면을 뒷받침하는 API를 설계하게 된다.

이때 백엔드 API 설계 시에는
* 어떤 엔드포인트가 있는지 
* 어떤 **요청(Request)**을 보내야 하는지
* 어떤 **응답(Response)**을 내려주는지

와 같은 내용을 정리한 API 스펙 기록이 필요하다.

즉, 프론트엔드 개발자와 요청·응답을 정확히 맞추기 위한 단계가 바로 API 문서화이며, <br>
이 API 문서를 만들 수 있는 도구는 Spring REST Docs와 Swagger가 있다.

# Spring REST Docs

---
## 개요

---
### 정의
* **테스트 코드를 통한 API 문서 자동화 도구**
* API 명세를 문서로 만들고 외부에 제공함으로써 협업을 원활하게 한다
* 기본적으로 AsciiDoc을 사용하여 문서를 작성한다. (AsciiDoc : 마크다운 같은 작성 문법)

### 장점
* 테스트를 통과해야 문서가 만들어져서 신뢰도가 높다.
* 프로덕션 코드에 비침투적이다. 

### 단점
* 코드 양이 많다.
* 설정이 어렵다.

## 설정하는 방법

---
### 1. build.gradle 설정  
※ [Asciidoctor](asciidoctor.org) : AsciiDoc ↔ HTML 파일로 변환 
* ① 플러그인 : `id 'org.asciidoctor.jvm.convert' version '4.0.2'`(Asciidoctor에 대한 플러그인 추가)
* ② configurations : `asciidoctorExt` 추가 (Asciidoctor extension에 대한 컴플리케이션 넣어준다)
* ③ dependencies :
    ```
    // RestDocs
    asciidoctorExt 'org.springframework.restdocs:spring-restdocs-asciidoctor'    // asciidoctor
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'    // MockMvc로 ‘API 테스트 코드’를 작성 이를 토대로 HTTP 요청/응답을 캡쳐해서 문서 생성된다.
    ```
* ④ tasks 추가
    ```
    // snippets(코드 조각)의 Dir(디렉토리)를 전역변수로 선언
    ext { 
        snippetsDir = file('build/generated-snippets')
    }
  
    // 테스트가 끝난 결과물(문서 파일)을 이 snippet 디렉토리로 넣도록 지정
    test {
        outputs.dir snippetsDir
    }
    
    // 테스트가 끝난 결과물을 받아서, 아스키독터라는 태스크에서 문서를 만든다. 
    asciidoctor {
        inputs.dir snippetsDir
        configurations 'asciidoctorExt'
    
        sources { // 특정 파일만 html로 만든다.
            include("**/index.adoc")
        }
        baseDirFollowsSourceFile() // 다른 adoc 파일을 include 할 때 경로를 baseDir로 맞춘다.
        dependsOn test    // 위 test 테스크(outputs.dir)를 의존한다 → 즉, test{}가 수행된 다음에 아스키독터가 실행된다.  
    
    }
    
    // Jar를 만드는 과정 
    bootJar {
        dependsOn asciidoctor    // 위 asciidoctor 태스크를 의존한다 → 즉, asciidoctor{}가 수행된 다음에 bootJar가 실행된다.
        from("${asciidoctor.outputDir}") {    // 문서가 나온 뒤, 정적파일을 보기 위해서 하위에다가 복사하는 과정을 거친다.
        into 'static/docs'
        }
    }
  
    // 즉 dependsOn에 따라 순서가 test → asciidoctor →  bootJar 순으로 실행된다.
    ```

### 2. IntelliJ plugIn 설치
* `AsciiDoc` 설치 : 마크다운 처럼 AsciiDoc 문법을 미리 보기 할 수 있다.

### 3. 이 후 test 패키지에서 진행 
#### 패키지 위치 : `java.sample.cafekiosk.spring.docs` 
#### 추상 클래스 설정 : `RestDocsSupport`
> - MockMvc 빌더 방식 중 스프링에 의존적이지 않은 standalonesetup() 이용하여 스프링 구동을 하지 않아도 됨<br> 
> - standaloneSetup(문서로 만들고 싶은 컨트롤러) : 스프링 의존성 없으므로 각 컨트롤러에 대한 단위 테스트로 테스팅하게 된다.<br>
> - support 추상메서드의 자식메서드에서 initController로 문서화 대상 컨트롤러 클래스 명시
> ```
>@ExtendWith(RestDocumentationExtension.class)
>public abstract class RestDocsSupport {
>
>    protected MockMvc mockMvc;
>    protected ObjectMapper objectMapper;
>
>    @BeforeEach
>    void setUp(RestDocumentationContextProvider provider) {
>        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())    
>                .apply(documentationConfiguration(provider))
>                .build();
>    }
>
>    protected abstract Object initController(); // 컨트롤러 테스트 클래스에서 
>}
>```
#### 자식 클래스 : `ProductControllerDocsTest`
1. 평소 @WebMvcTest 테스트 하듯이 따면 되는데, 문미에 `andDo(document())`설정을 하여 요청값, 응답값, 쿼리파라미터 등을 설정한다.
2. document('식별자(작성자가 설정)', 'snippet')

* snippet이란? 
  * API 테스트 실행 과정에서 추출된 "문서 조각". 즉 요청/응답의 한 부분을 설명하는 최소 단위 문서  <br>
    즉, 여러 스니펫을 조합해서 최종 문서를 만들게 되는 것.
  * snippet 부분에 어떤 요청을 넣고 어떤 응답을 넣을 것인가 정의를 해주면 된다.
    - 예시 : get요청인 경우 query 파라미터, post요청인 경우 request, response
  * 스니펫 부분에 아래 코드를 주게 되는데, 이는 asciidoc 문서에서 Json 코드를 가독성있게 꾸며주는 역할을 한다.
    `preprocessRequest(prettyPrint()),<br>
    preprocessResponse(prettyPrint()),`
  

* snippet 작성
  - `requestFields`,`responseFields` : DTO의 필드 수 = `requestFields` 또는 `responseFields`의 각각의 갯수 <br>
     예를들어, `ProductCreateRequest` DTO의 경우 필드가 4개 이므로 requestFields를 총 4개 만들어주면 된다. <br>
              `ApiResponse` 공통 Api Response의 필드가 4개 이므로 responseFields도 총 4개 만들어진다.
  - `requestFields`,`responseFields` 구성요소
    - `fieldWithPath`(필드 경로) : JSON 구조에서 문서화할 필드에 도달하기 위한 경로(path) 를 문자열로 표현한 것<br>
        (예: products.type, products[].type, product.detail.type 등)
    - `type`(타입) : 해당 필드의 타입(Json기준)을 기재 <br> 
      (예: string, number, array, object, boolean 등, Enum은 String)
    - `description` : 이 타입이 어떤건지 설명하는 란

* 테스트가 성공하고 Gradle.documentation.asciidoctor 을 실행하면
  * 『build.generated-snippets.document의 0번 인덱스에서 지정한 디렉토리 명』 으로 snippet(코드 조각)들이 생긴다.
  * tip. 원치 않는 snippet들이 있다면 build.clean을 이용하면 빌드 디렉토리가 초기화 된다.

### 4. snippet들을 조합해서 하나의 문서로 만들기 
#### 패키지 위치 : `src.doc.asclidoc.index.adoc`
#### index.adoc 기본 형태
```
ifndef::snippets[]                          - 스니펫 경로 지정
:snippets: ../../build/generated-snippets   
endif::[]

= CafeKiosk REST API 문서             - 제목 
:doctype: book                       
:icons: font
:source-highlighter: highlightjs     - JSON 형태의 하이라이트
:toc: left                           - 목차 위치
:toclevels: 2                        - 목차 depth
:sectlinks:                          - 섹션에 대한 링크(각 목차에 대한 네비게이션 기능 적용)

[[Product-API]]                      - 대괄호 2개 : 링크
== Product API  

include::api/product/product.adoc[]  - 인클루드한 스니펫 파일 - + adoc 파일을 분리해서 빌드할때 include를 시킬 수 있다. 동문서의 목차 참고
```
#### `index.adoc`를 만든 후, gradle.build 실행 
#### 이후 `build.docs.asciidoc` 디렉터리가 생성된걸 확인할 수 있으며, `index.html`에서 하나의 문서가 된걸 확인할 수 있다. 

## 이외 아스키독 기능

---
### + HTML문서에서 JSON 포맷을 이쁘게 줄바꿈해줄수 있다.
- DocsTest에서 설정 : `ProductControllerDocsTest`
```
.andDo(document("product-create",
    preprocessRequest(prettyPrint()),       
    preprocessResponse(prettyPrint()),
```

### + 스니펫을 커스텀 마이징 할 수 있다
#### 커스텀하는 이유? 
* 예를 들어, Nullable을 표시하고 싶다. 라고 하면 새로운 컬럼을 추가해야된다.
* 이럴때 사용할 수 있는 방법
#### 패키지 위치 : `test.resources.org.springframework.restdocs.templates`
* 해당 패키지 하위에서 커스텀할 대상 디렉터리를 만든다.
* 예시 : request-fields.snippet : 기존 필드에 Null이 가능한지를 표시하기 위해 `Optional`컬럼을 추가
```
==== Request Fields
|===
|Path|Type|Optional|Description         - 커스텀한 컬럼

{{#fields}}                             - 반복문

|{{#tableCellContent}}`+{{path}}+`{{/tableCellContent}}
|{{#tableCellContent}}`+{{type}}+`{{/tableCellContent}}
|{{#tableCellContent}}{{#optional}}O{{/optional}}{{/tableCellContent}}  - Optional한 경우엔 O라고 표시해줘 (O부분도 자유롭게)
|{{#tableCellContent}}{{description}}{{/tableCellContent}}

{{/fields}}

|===
```
#### 위 컬럼 커스텀을 대상 DocsTest에도 맞춰야한다.

### + adoc 파일을 분리해서 빌드할때 include를 시킬 수 있다.
#### 대상 패키지 : `src.docs.asciidoc`
#### 왜쓰는가?
* api가 여러개하면 adoc에 들어갈 내용이 너무 많아 진다. → 만드는 입장에서 관리하기 어려움
* adoc 파일을 여러 개로 나눠서 빌드할 때 딱 하나로 합치는게 가능
#### 방법
* asciidoc 하위에 임의 패키지 생성 후 adoc 파일 생성
* index.adoc 파일에 include<br>
  `include::파일경로.파일명.adoc[]`
#### 미리보기에서는 잘되는데 html 파일에서 깨지는 경우? 
* build.gradle 확인
 * asciidoctor에 baseDirFollowsSourceFile() 추가
 * baseDirFollowsSourceFile : 다른 adoc 파일을 include 할 때 경로를 baseDir로 맞춘다.

## 이제 이 문서를 어떻게 남한테 보여주나?
* 관련 `build.gradle`설정
```aiignore
bootJar {
    dependsOn asciidoctor
    from("${asciidoctor.outputDir}") {
        into 'static/docs'
    }
}
```
* jar 위치 : `build.libs.cafekiosk-0.0.1-SNAPSHOT.jar`
* 터미널 실행 
  * `java -jar cafekiosk-0.0.1-SNAPSHOT.jar`
* http://localhost:8080/docs/index.html 접속