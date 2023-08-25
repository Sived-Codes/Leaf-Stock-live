package com.prashant.stockmarketadviser.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constant {
    public static DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users");
    public static DatabaseReference performanceDB = FirebaseDatabase.getInstance().getReference().child("Performance");
    public static DatabaseReference feedDB = FirebaseDatabase.getInstance().getReference().child("Feeds");
    public static DatabaseReference primeTipsDB = FirebaseDatabase.getInstance().getReference().child("PrimeTips");
    public static DatabaseReference trialTipsDB = FirebaseDatabase.getInstance().getReference().child("TrialTips");
    public static DatabaseReference tipGenDB = FirebaseDatabase.getInstance().getReference().child("TipGenerator");
}