package no.java.pigeon;

import com.sendgrid.*;

import java.io.IOException;

public class SendGridEmailSender implements DoMailSend {

    private final String sendgridKey;

    public SendGridEmailSender() {
        this.sendgridKey = Setup.sendGridKey();
    }

    @Override
    public void send(String from, String to, String subject, String htmlContent) {
        SendGrid sg = new SendGrid(this.sendgridKey);

        Request request = new Request();
        Email fromEmail = new Email(from);
        Content content = new Content("text/html", htmlContent);

        Mail mail = new Mail();

        mail.setFrom(fromEmail);
        mail.setSubject(subject);
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(to));
        mail.addPersonalization(personalization);
        mail.addContent(content);

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
