package no.java.pigeon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyEmailSender implements DoMailSend {
    private static final Logger logger = LoggerFactory.getLogger(DummyEmailSender.class);
    @Override
    public void send(String from, String to, String subject, String htmlContent) {
        logger.debug(String.format("EmailFrom: %s, To: %s, Subject:%s, Content: %s",from,to,subject,htmlContent));
    }
}
