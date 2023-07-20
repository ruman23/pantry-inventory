package com.example.pantryinventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFoods extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText editText;
    private DatePicker datePicker;
    private Uri imageUri;
    private ProgressBar progressBar;
    private ItemData itemData;
    private String key;

    private String originalFoodName;
    private String originalExpDate;
    private String originalImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_foods);

        imageView = findViewById(R.id.image_view);
        ImageButton addImageButton = findViewById(R.id.add_image_button);
        editText = findViewById(R.id.edit_text);
        datePicker = findViewById(R.id.date_picker);
        progressBar = findViewById(R.id.progressBar);

        itemData = (ItemData) getIntent().getSerializableExtra("itemData");
        key = getIntent().getStringExtra("key");

        if (itemData != null) {
            // This is an existing item. Display its data.
            editText.setText(itemData.getFoodName());
            Picasso.get().load(itemData.getImageUrl()).into(imageView);
            // You need to convert itemData's expiration date to Calendar and set it to the datePicker

            // Save the original values
            originalFoodName = itemData.getFoodName();
            originalExpDate = itemData.getExpDate();
            originalImageUrl = itemData.getImageUrl();
        } else {
            // This is a new item. Initialize itemData and key.
            itemData = new ItemData();
            key = FirebaseDatabase.getInstance().getReference().child("items").push().getKey();
        }

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                // Check if any data has been changed
                if (!foodName.equals(originalFoodName) || !expDate.equals(originalExpDate) || (imageUri != null && !imageUri.toString().equals(originalImageUrl))) {
                    // If so, update the item data and save it to Firebase
                    itemData.setFoodName(foodName);
                    itemData.setExpDate(expDate);

                    progressBar.setVisibility(View.VISIBLE);
                    // Upload the selected image to Firebase Storage
                    if (imageUri != null) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("uploads").child(imageUri.getLastPathSegment());
                        UploadTask uploadTask = storageReference.putFile(imageUri);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the download URL of the uploaded image
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();
                                itemData.setImageUrl(downloadUrl.toString());

                                // Update the item in Firebase
                                updateItemInFirebase();
                            }
                        });
                    } else {
                        // No new image, just update the item in Firebase
                        updateItemInFirebase();
                    }
                }
            }
        });
    }

    private void updateItemInFirebase() {
        // Get a reference to the Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Update the item in Firebase
        databaseReference.child("items").child(key).setValue(itemData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        // Return to the previous screen
                        onBackPressed();
                    }
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }
}
