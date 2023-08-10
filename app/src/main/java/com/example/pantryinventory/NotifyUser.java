package com.example.pantryinventory;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifyUser extends JobService {
    private static final String CHANNEL_ID = "DatabaseCheckChannel";
    private static final String TAG = "NotifyUser";

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();
        checkDatabase();

        return true; // If job is executed in a separate thread.
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Implement your logic if the job needs to be stopped prematurely.
        return false;
    }

    private void checkDatabase() {
        Log.d(TAG, "Checking database");
        new FirebaseDBHelper().readItems(new FirebaseDBHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<ItemData> itemDataList, List<String> keys) {
                Log.d(TAG, "Data loaded");
                for (ItemData item : itemDataList) {
                    if (isToday(item.getExpDate())) {
                        Log.d(TAG, "Item expires today: " + item.getFoodName());
                        showNotification(item);
                    }
                }
            }

            @Override
            public void DataIsInserted() { }

            @Override
            public void DataIsUpdated() { }

            @Override
            public void DataIsDeleted() { }
        });
    }

    private boolean isToday(String dateString) {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date date;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            return false;
        }

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        Calendar calendar2 = Calendar.getInstance();

        boolean isToday = calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);

        if (isToday) {
            Log.d(TAG, "Date is today: " + dateString);
        } else {
            Log.d(TAG, "Date is not today: " + dateString);
        }

        return isToday;
    }

    private void showNotification(ItemData item) {
        Log.d(TAG, "Showing notification for item: " + item.getFoodName());
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.burger)
                .setContentTitle("New Database Item")
                .setContentText(item.toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Log.d(TAG, "Create notification manager");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(hashStringToInt(item.getFoodName()), builder.build());
    }

    public static int hashStringToInt(String s) {
        int hash = 7;
        for (int i = 0; i < s.length(); i++) {
            hash = hash*31 + s.charAt(i);
        }
        return hash;
    }

    private void createNotificationChannel() {
        Log.d(TAG, "Creating notification channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DatabaseCheckChannel";
            String description = "Channel for Database Check";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}