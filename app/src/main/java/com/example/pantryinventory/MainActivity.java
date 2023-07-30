package com.example.pantryinventory;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemListener {
    FirebaseAuth auth;
    FirebaseUser user;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView recyclerView;
    Button addButton;

    AtomicBoolean dataLoaded = new AtomicBoolean(false);
    List<ItemData> itemDataList;
    List<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recycler_view);
        addButton = findViewById(R.id.add_button);

        scheduleAlarm();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            MenuItem userDetailsItem = navigationView.getMenu().findItem(R.id.nav_user_details);
            userDetailsItem.setTitle(user.getEmail());
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.nav_notification:
                    Intent notification_intent = new Intent(getApplicationContext(), NotificationList.class);
                    startActivity(notification_intent);
                    finish();
                    break;
            }
            drawerLayout.closeDrawers();
            return true;
        });

        loadData();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFoods.class);
                startActivity(intent);
            }
        });
    }

    private void scheduleAlarm() {
        ComponentName componentName = new ComponentName(this, DatabaseCheckJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("MainActivity", "Job scheduled");
        } else {
            Log.d("MainActivity", "Job scheduling failed");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Reload the data whenever MainActivity is resumed
    }

    private void loadData() {
        new FirebaseDBHelper().readItems(new FirebaseDBHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<ItemData> itemDataList, List<String> keys) {
                MainActivity.this.itemDataList = itemDataList;
                MainActivity.this.keys = keys;
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemDataList, false,MainActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                dataLoaded.set(true);
            }

            @Override
            public void DataIsInserted() { }

            @Override
            public void DataIsUpdated() { }

            @Override
            public void DataIsDeleted() { }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, AddFoods.class);
        intent.putExtra("itemData", itemDataList.get(position));
        intent.putExtra("key", keys.get(position));  // You need the key to update the item in Firebase
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteClick(int position) {
        new FirebaseDBHelper().deleteItem(keys.get(position), new FirebaseDBHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<ItemData> itemDataList, List<String> keys) { }

            @Override
            public void DataIsInserted() { }

            @Override
            public void DataIsUpdated() { }

            @Override
            public void DataIsDeleted() {
                // re-load the data
                loadData();
            }
        });
    }

}