package com.example.pantryinventory;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDBHelper {

    private FirebaseDatabase db;
    private DatabaseReference ref;
    private List<ItemData> itemDataList = new ArrayList<>();

    public FirebaseDBHelper() {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("items");
    }

    public interface DataStatus {
        void DataIsLoaded(List<ItemData> itemDataList, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public void readItems(final DataStatus dataStatus) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemDataList.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Log.d("FirebaseData", "KeyNode value: " + keyNode.toString());
                    try {
                        ItemData itemData = keyNode.getValue(ItemData.class);
                        itemDataList.add(itemData);
                    } catch (Exception e) {
                        Log.e("FirebaseData", "Error in parsing: " + e.getMessage());
                    }
                }
                dataStatus.DataIsLoaded(itemDataList, keys);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error here
            }
        });
    }
}