package com.example.pantryinventory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationList extends AppCompatActivity implements RecyclerViewAdapter.OnItemListener {
    FirebaseAuth auth;
    FirebaseUser user;
    RecyclerView recyclerView;

    AtomicBoolean dataLoaded = new AtomicBoolean(false);
    List<ItemData> itemDataList;
    List<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
    }

    @Override
    public void onBackPressed() {
        Intent new_intent = new Intent(this, MainActivity.class);
        this.startActivity(new_intent);
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
                NotificationList.this.itemDataList = itemDataList;
                NotificationList.this.keys = keys;
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemDataList, NotificationList.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(NotificationList.this));
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
//        Intent intent = new Intent(NotificationList.this, AddFoods.class);
//        intent.putExtra("itemData", itemDataList.get(position));
//        intent.putExtra("key", keys.get(position));  // You need the key to update the item in Firebase
//        startActivity(intent);
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