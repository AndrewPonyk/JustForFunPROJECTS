package com.ap.utils;

import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import pl.allegro.finance.tradukisto.ValueConverters;

import javax.mail.*;
import javax.mail.internet.*;
import java.time.LocalDateTime;
import java.util.*;

//basic usage of sonarqube
//mvn clean package sonar:sonar  -Dsonar.host.url=http://localhost:9000 -Dsonar.login=a6038e62294db47baf288d5eb4ea0083bd78ff01
public class JavaCoreSendMailUtils {

    static BetRepo betRepo = new BetRepoJdbc();
    static ValueConverters converter = ValueConverters.ENGLISH_INTEGER;
    public static void main(String[] args) throws MessagingException {
        sendHtmlTable(Constants.BET_EMAIL, "many tables", Arrays.asList(Arrays.asList("1","2"), Arrays.asList("3","4")),
                Constants.BET_EMAIL, Constants.BET_PASSWORD);
    }

    public static void sendHtmlTableWithUserData(String to, String subject, LinkedList<List<String>> tablesText, String from, String password) throws MessagingException {
        Double currentAmount=0.0;
        Double threePercent = 0.0;
        List<String> betMetadata = betRepo.getLastBetInfo();
        LocalDateTime now = LocalDateTime.now();
        try {
            currentAmount = Double.valueOf(betMetadata.get(2));
            threePercent = currentAmount*0.03;
            betMetadata.add("TIME:" + converter.asWords(now.getHour())+
                    ":"+converter.asWords(now.getMinute()));
            betMetadata.add(currentAmount+": 3%=" + threePercent);
            betMetadata.add(betRepo.stagesCount());
        }catch (Exception e){
            System.out.println("cann not parse current amount");
        }
        betMetadata.set(0, "id:" +betMetadata.get(0));
        betMetadata.set(1, "Status:" +betMetadata.get(1));
        betMetadata.set(2, "Amount:" +betMetadata.get(2));
        tablesText.addFirst(betMetadata);
        sendHtmlTable(to, subject, tablesText, from, password);
    }

    public static void sendHtmlTable(String to, String subject, List<List<String>> tablesText, String from, String password) throws MessagingException {
        // worked example from here (7 votes)
        //http://stackoverflow.com/questions/10509699/must-issue-a-starttls-command-first

        Session session = getSession(from, password);

        // can be added "session.setDebug(true);"
        Transport transport = session.getTransport();
        InternetAddress addressFrom = new InternetAddress(from);


        MimeMessage messageWithHtml = new MimeMessage(session);
        messageWithHtml.setSubject(subject);
        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();

        final StringBuilder tableHtml = new StringBuilder();

        tablesText.stream().forEach(table ->{
            tableHtml.append("<table style='border-collapse:collapse; border: 2px solid grey'>");
            table.stream().forEach(e -> tableHtml.append(getTableRow(e)));
            tableHtml.append("</table> <br/>");

        });

        htmlPart.setContent(tableHtml.toString(), "text/html");
        mp.addBodyPart(htmlPart);
        messageWithHtml.setContent(mp);
        messageWithHtml.addRecipient(Message.RecipientType.TO, new InternetAddress(to));


        Transport.send(messageWithHtml);
        transport.close();
    }

    public static void sendTwoDimTables(String to, String subject, List<List<List<String>>> tablesText, String from, String password) throws MessagingException {
// can be added "session.setDebug(true);"
        Session session = getSession(from, password);
        Transport transport = session.getTransport();
        InternetAddress addressFrom = new InternetAddress(from);


        MimeMessage messageWithHtml = new MimeMessage(session);
        messageWithHtml.setSubject(subject);
        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();

        final StringBuilder tableHtml = new StringBuilder();

        tablesText.stream().forEach(table ->{
            tableHtml.append("<table style='border-collapse:collapse; border: 2px solid grey'>");
            table.stream().forEach(e -> tableHtml.append(getMultiCellRow(e)));
            tableHtml.append("</table> <br/>");

        });

        htmlPart.setContent(tableHtml.toString(), "text/html");
        mp.addBodyPart(htmlPart);
        messageWithHtml.setContent(mp);
        messageWithHtml.addRecipient(Message.RecipientType.TO, new InternetAddress(to));


        Transport.send(messageWithHtml);
        transport.close();
    }

    private static String getTableRow(String text) {
        return "<tr><td style='border-collapse: collapse;border: 1px solid grey;padding:5px'>" + text + "</td></tr>";
    }

    private static String getMultiCellRow(List<String> row){
        StringBuilder result = new StringBuilder("<tr>");
        row.forEach(cell->{
            result.append("<td>").append(cell).append("</td>");
        });
        return result.toString();
    }

    private static Session getSession(String from, String password) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.debug", "false");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        return Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });
    }


////////////////////////////////////////////
// Good utils classes, and different security way: 1) No auth 2)TLS auth 3)SSL auth
//http://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp
}
