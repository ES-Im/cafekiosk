package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

//class MailServiceTest {               --  1단계 : Mockito를 직접 사용하기
//
//    @Test
//    @DisplayName("메일 전송 테스트")
//    void sendMail() {
//        // given
//        MailSendClient mailSendClient = mock(MailSendClient.class);
//        MailSendHistoryRepository mailSendHistoryRepository = mock(MailSendHistoryRepository.class);
//
//        MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository);
//
//        when(mailSendClient.sendEmail(
//                anyString(), anyString(), anyString(), anyString())
//        ).thenReturn(true);
//
//        // when
//        boolean result = mailService.sendMail("", "", "", "");
//
//        // then
//        assertThat(result).isTrue();
//        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
//        //Mockito의 verify를 사용하여
//        //mailSendHistoryRepository Mock 객체의 save() 메서드가
//        //어떤 MailSendHistory 객체를 인자로 하여
//        //정확히 1번 호출되었는지를 검증한다.
//
//    }
//
//
//}

/*
 * ↓ 리팩토링
 */

//@ExtendWith(MockitoExtension.class)     // @Mock과 @InjectMocks를 위한 익스텐션
//class MailServiceTest {
//
//    @Mock private MailSendClient  mailSendClient;       // 가짜 Bean 등록
//    @Mock private MailSendHistoryRepository mailSendHistoryRepository;      // 가짜 Bean 등록
//    @InjectMocks private MailService mailService;       // DI 역할
//
//    @Test
//    @DisplayName("메일 전송 테스트")
//    void sendMail() {
//        // given
//        when(mailSendClient.sendEmail(
//                anyString(), anyString(), anyString(), anyString())
//        ).thenReturn(true);
//
//        // when
//        boolean result = mailService.sendMail("", "", "", "");
//
//        // then
//        assertThat(result).isTrue();
//        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
//
//    }
//
//
//}
/*
 * 사실상 spy라는 TestDouble은 Mock + verify()와의 기능이 유사하다.
 * 다만 로그를 확인하고 싶거나, 추적대상 클래스내에 기능이 많을 경우는 spy를 사용하는 것이 유리하다.
 * ↓ @spy 사용 - 클래스 내의 특정 메서드만 추적하고싶을때
 */

@ExtendWith(MockitoExtension.class)     // @Mock과 @InjectMocks를 위한 익스텐션
class MailServiceForMockSpyTest {

    @Spy private MailSendClient  mailSendClient;       // 가짜 Bean 등록
    @Mock private MailSendHistoryRepository mailSendHistoryRepository;      // 가짜 Bean 등록
    @InjectMocks private MailService mailService;       // DI 역할

    @Test
    @DisplayName("메일 전송 테스트")
    void sendMail() {
        // given
//        when(mailSendClient.sendEmail(              // spy는 실제 객체를 기반으로 만들어지므로 when절을 쓸 수 없다.
//                anyString(), anyString(), anyString(), anyString())
//        ).thenReturn(true);
        doReturn(true)
                .when(mailSendClient)
                .sendEmail(anyString(), anyString(), anyString(), anyString());
        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));

    }

  }
