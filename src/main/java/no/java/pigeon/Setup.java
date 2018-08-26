package no.java.pigeon;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Setup {
    private static final ConcurrentHashMap<String,String> values = new ConcurrentHashMap<>();

    public static void readConfig(String configfile) {
        String configcontent;
        try {
            configcontent = Utils.toString(new FileInputStream(configfile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String line : configcontent.split("\n")) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }
            int pos = line.indexOf("=");
            values.put(line.substring(0,pos),line.substring(pos+1));
        }
    }

    private static String readRequredValue(String key) {
        return Optional.ofNullable(values.get(key)).orElseThrow(() -> new RuntimeException("Config missing setup key " + key));
    }

    private static String readValue(String key,String defaultValue) {
        return Optional.ofNullable(values.get(key)).orElse(defaultValue);
    }

    public static String templateFile() {
        return readRequredValue("templateFile");
    }

    public static String subject() {
        return readRequredValue("subject");
    }

    public static DoMailSend emailSender() {
        if ("sendgrid".equals(readValue("emailSender","dummy"))) {
            return new SendGridEmailSender();
        }
        return new DummyEmailSender();
    }

    public static String sendFrom() {
        return readValue("sendFrom","program@java.no");
    }

    public static String sendingInfoFile() {
        return readRequredValue("sendingInfoFile");
    }

    public static String sendGridKey() {
        return readRequredValue("sendGridKey");
    }
}
