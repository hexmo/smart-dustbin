package com.example.smartdustbin.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.smartdustbin.R;
import com.example.smartdustbin.userManagement.UserModel;
import com.example.smartdustbin.utils.SplashScreenActivity;
import com.example.smartdustbin.wasteManagement.DustbinModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyBroadCastReceiver extends BroadcastReceiver {

    FirebaseFirestore db;

    @Override
    public void onReceive(Context context, Intent intent) {

        db = FirebaseFirestore.getInstance();

//        Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
        Log.d("Broadcast: ", "It was called. ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ID001", "Rentals notification.", importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = ContextCompat.getSystemService(context, NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        Intent intent2 = new Intent(context, SplashScreenActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);

        DocumentReference docRef1 = db.collection("dustbins").document("aa45zx");
        docRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("Broadcast: ", "Dustbin 1 read. ");
                DustbinModel dustbinModel = documentSnapshot.toObject(DustbinModel.class);
                //populate text views

                int wastePct = dustbinModel.getWasteLevel() * 4;
                Log.d("Broadcast: ", "Dustbin 1 read. " + wastePct);
                if (wastePct < 20) {
                    Log.d("Broadcast: ", "Dustbin 2 greater than 80. ");
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ID001").setSmallIcon(R.drawable.logo).
                            setContentTitle("Dustbin level " + (100 - wastePct) + " %")
                            .setContentText("Please collect waste from " + dustbinModel.getDustbinRoom())
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(5678, builder.build());
                }
            }
        });

        DocumentReference docRef2 = db.collection("dustbins").document("b456x");
        docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Log.d("Broadcast: ", "Dustbin 2 read. ");
                DustbinModel dustbinModel2 = documentSnapshot.toObject(DustbinModel.class);
                //populate text views

                int wastePct2 = dustbinModel2.getWasteLevel() * 4;

                Log.d("Broadcast: ", "Dustbin 2 read. " + wastePct2);
                if (wastePct2 < 20) {
                    Log.d("Broadcast: ", "Dustbin 2 greater than 80. ");
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ID001").setSmallIcon(R.drawable.logo).
                            setContentTitle("Dustbin level " + (100 - wastePct2) + " %")
                            .setContentText("Please collect waste from " + dustbinModel2.getDustbinRoom())
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(1234, builder.build());
                }
            }
        });


    }
}
