package com.example.pantryinventory;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFoods extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_foods);

        imageView = findViewById(R.id.image_view);
        editText = findViewById(R.id.edit_text);
        datePicker = findViewById(R.id.date_picker);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform save action here

                // Get the Drawable's ID
                Drawable drawable = imageView.getDrawable();
                int imageResourceId = getResources().getIdentifier(drawable.toString(), "drawable", getPackageName());

                // Get the food name
                String foodName = editText.getText().toString();

                // Get the expiration date
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String expDate = format.format(calendar.getTime());

                // Create an ItemData object
                ItemData itemData = new ItemData(imageResourceId, foodName, expDate);

                // Get a reference to the Realtime Database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                // Push a new item to the "items" child node
                databaseReference.child("items").push().setValue(itemData);

                // Return to the previous screen
                onBackPressed();
            }
        });
    }
}