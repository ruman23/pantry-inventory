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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddFoods extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText editText;
    private DatePicker datePicker;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_foods);

        imageView = findViewById(R.id.image_view);
        ImageButton addImageButton = findViewById(R.id.add_image_button);
        editText = findViewById(R.id.edit_text);
        datePicker = findViewById(R.id.date_picker);

        applyBlur(this, imageView, 5f);

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
                // Perform save action here

                // Get the Drawable's ID
                Drawable drawable = imageView.getDrawable();
                String drawableString = drawable.toString();
                int imageResourceId = getResources().getIdentifier(drawableString.substring(drawableString.lastIndexOf('/') + 1, drawableString.lastIndexOf('.')), "drawable", getPackageName());

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

    public void applyBlur(Context context, ImageView imageView, float blurRadius) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(blurRadius); // Set the blur radius
        script.setInput(input);
        script.forEach(output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        script.destroy();
        rs.destroy();

        imageView.setImageBitmap(bitmap);
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