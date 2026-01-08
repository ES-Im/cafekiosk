package sample.cafekiosk.spring.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailSendClient {

    public boolean sendEmail(String fromEmail, String toEmail, String title, String content) {
        log.info("메일 전송");
        throw new IllegalArgumentException("메일 전송");    // 메일이 전송됬다 가정
//        return true;
    }

    public void a() {
        log.info("a");
    }
    public void b() {
        log.info("b");
    }
    public void c() {
        log.info("c");
    }
}
