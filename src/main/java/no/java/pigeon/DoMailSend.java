package no.java.pigeon;

public interface DoMailSend {
    void send(String from,String to,String subject,String htmlContent);
}
