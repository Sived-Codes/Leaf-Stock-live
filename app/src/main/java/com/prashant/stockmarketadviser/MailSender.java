package com.prashant.stockmarketadviser;

import android.content.Context;


public class MailSender {

//    public static void toMeetingMember(Context context, MemoMailModel model){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final String username = "kiranfuse52@gmail.com";
//                final String password = "eckehcapttcigple";
//
//                Properties props = new Properties();
//                props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
//                props.put("mail.smtp.port", "587"); //TLS Port
//                props.put("mail.smtp.auth", "true"); //enable authentication
//                props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
//
//                Session session = Session.getInstance(props,
//                        new javax.mail.Authenticator() {
//                            protected PasswordAuthentication getPasswordAuthentication() {
//                                return new PasswordAuthentication(username, password);
//                            }
//                        });
//
//                try {
//                    Message message = new MimeMessage(session);
//                    message.setFrom(new InternetAddress(username));
//                    for (String recipient : model.getMemberMail()) {
//                        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
//                    }
//                    message.setSubject("Meeting Details : " +model.getMeetingSubject());
//                    message.setText("");
//
//                    Transport.send(message);
//
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            CProgressDialog.mDismiss();
////                            Toast.makeText(context, "Emails sent successfully", Toast.LENGTH_SHORT).show();
////                        }
////                    });
//
//                } catch (MessagingException e) {
//                    // Handle the exception, display an error toast or UI message
//                    e.printStackTrace();
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            CProgressDialog.mDismiss();
////                            Toast.makeText(context, "Error sending emails" +e.toString(), Toast.LENGTH_SHORT).show();
////                        }
////                    });
//                }
//            }
//        }).start();
//
//    }

}
