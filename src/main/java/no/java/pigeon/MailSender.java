package no.java.pigeon;

import org.logevents.LogEventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MailSender {
    private static Logger logger;
    private final String template;
    private final String subject;
    private final DoMailSend sender;
    private final String sendFrom;

    private MailSender() throws IOException {
        this.template = Utils.toString(new FileInputStream(Setup.templateFile()));
        this.subject = Setup.subject();
        this.sender = Setup.emailSender();
        this.sendFrom = Setup.sendFrom();
    }
    public static void main(String[] args) throws Exception {
        LogEventFactory factory = LogEventFactory.getInstance();
        factory.setRootLevel(Level.DEBUG);
        logger = LoggerFactory.getLogger(MailSender.class);
        logger.info("Starting");
        if (args.length < 1) {
            System.out.println("Usage MailSender <configfile>");
            return;
        }
        Setup.readConfig(args[0]);
        MailSender mailSender = new MailSender();
        mailSender.send();
    }

    private void send() throws IOException {
        String sendingInfo = Utils.toString(new FileInputStream(Setup.sendingInfoFile()));
        List<String> fieldnames = null;
        String[] allLines = sendingInfo.split("\r?\n");
        int emailindex = -1;
        int numMessages = allLines.length -1;
        logger.info("Sending " + numMessages + " messages");
        for (String line : allLines) {
            if (fieldnames == null) {
                fieldnames = readFieldnames(line);
                emailindex = fieldnames.indexOf("email");
                continue;
            }
            List<String> values = Arrays.asList(line.split(";"));
            if (values.size() != fieldnames.size()) {
                throw new RuntimeException("All lines must have all field error : " + line);
            }
            StringBuilder content = new StringBuilder(template);
            for (int i=0;i<fieldnames.size();i++) {
                if (i == emailindex) {
                    continue;
                }
                String key = "#" + fieldnames.get(i) + "#";
                for (int pos = content.indexOf(key);pos != -1; pos = content.indexOf(key) ) {
                    content.replace(pos,pos+key.length(),values.get(i));
                }
            }
            sender.send(sendFrom,values.get(emailindex),subject,content.toString());
        }
        logger.info("Done");

    }

    private List<String> readFieldnames(String line) {
        List<String> values = Arrays.asList(line.split(";"));
        if (!values.contains("email")) {
            throw new RuntimeException("Missing column email");
        }
        return values;

    }
}
