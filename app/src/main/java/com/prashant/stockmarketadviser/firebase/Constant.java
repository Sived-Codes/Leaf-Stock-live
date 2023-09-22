package com.prashant.stockmarketadviser.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constant {

    public static String uid;

    static {
        String tempUid = AuthManager.getUid();
        uid = tempUid != null ? tempUid : "";
    }

    public static String TRIAL_NOTIFICATION_TOPIC = "trial";
    public static String PRIME_NOTIFICATION_TOPIC = "prime";
    public static String All_NOTIFICATION_TOPIC = "allUser";
    public static String WAITING_IMG_URL = "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/TARGET%20STATUS%20IMAGE%2Fwaiting_check.png?alt=media&token=e8ce1880-f4a2-4613-8ae2-f562ab4564d1";
    public static String ACHIEVED_IMG_URL = "https://firebasestorage.googleapis.com/v0/b/trade-master-edbae.appspot.com/o/TARGET%20STATUS%20IMAGE%2Fgreen_check.png?alt=media&token=d1bda48a-96b0-4c92-80a6-e5a1c769d2d4";
    public static DatabaseReference planDB = FirebaseDatabase.getInstance().getReference().child("Plans");
    public static DatabaseReference crashDB = FirebaseDatabase.getInstance().getReference().child("App Crashes");

    public static DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users");
    public static DatabaseReference performanceDB = FirebaseDatabase.getInstance().getReference().child("Performance");

    public static DatabaseReference feedDB = FirebaseDatabase.getInstance().getReference().child("Feeds");
    public static DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference().child("Notification");
    public static DatabaseReference scripDB = FirebaseDatabase.getInstance().getReference().child("Scrips");

    public static DatabaseReference tipGenDB = FirebaseDatabase.getInstance().getReference().child("TipGenerator");
    public static DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("Chats");
    public static DatabaseReference adminDB = FirebaseDatabase.getInstance().getReference().child("Admin");
}