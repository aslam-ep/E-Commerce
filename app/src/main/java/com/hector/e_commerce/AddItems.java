package com.hector.e_commerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddItems extends AppCompatActivity {

    // Global variables
    private static final int PICK_IMAGE_REQUEST = 1;
    private int IMAGE_COUNT = 0;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();

    // Debug tag
    private String TAG = "E-Commerce";

    // UI elements
    LinearLayout addItemLayout;
    ProgressBar addItemProgressBar;
    TextInputEditText name, company, spec, details, item_quantity, price;
    Button chooseImage, postAd;

    // Image upload instances
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db;

    // Item data
    Map<String, Object> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        // Action bar costume
        setTitle("ADD PRODUCT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Pointing the variables
        addItemLayout = findViewById(R.id.addItemsLayout);
        addItemProgressBar = findViewById(R.id.addItemsProgressBar);

        name = findViewById(R.id.itemName);
        company = findViewById(R.id.itemCompany);
        spec = findViewById(R.id.itemSpec);
        details = findViewById(R.id.itemDetails);
        item_quantity = findViewById(R.id.itemQuantity);
        price = findViewById(R.id.itemPrice);

        chooseImage = findViewById(R.id.itemImageButton);
        postAd = findViewById(R.id.itemPostButton);

        // Getting firebase instances
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        //Button on click listeners
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        postAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId = "123456";
                if(dataCheck()) {
                    // Map for the data
                    item = new HashMap<>();
                    item.put("VendorID", userId);
                    item.put("Name", name.getText().toString());
                    item.put("Company",company.getText().toString());
                    item.put("Specification", spec.getText().toString());
                    item.put("Details", details.getText().toString());
                    item.put("Quantity",item_quantity.getText().toString());
                    item.put("Price", price.getText().toString());
                    item.put("ImageCount", String.valueOf(IMAGE_COUNT));


                    if (isNetworkAvailable(getApplicationContext())) {
                        // Hiding the form and displaying the progress bar
                        addItemLayout.setVisibility(View.INVISIBLE);
                        addItemProgressBar.setVisibility(View.VISIBLE);

                        db.collection("Products")
                                .add(item)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        new DataUpload(documentReference.getId()).execute();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        addItemProgressBar.setVisibility(View.INVISIBLE);
                                        addItemLayout.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else
                        Toast.makeText(getApplicationContext(), "Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST){
            if(resultCode == RESULT_OK){
                if(data.getClipData() != null){
                    IMAGE_COUNT = Math.min(4,data.getClipData().getItemCount());

                    for (int i = 0; i < IMAGE_COUNT; i++){
                        Uri filePath = data.getClipData().getItemAt(i).getUri();
                        ImageList.add(filePath);
                    }

                }else if(data.getData() != null){
                    IMAGE_COUNT = 1;
                    ImageList.add(data.getData());
                }else{
                    Toast.makeText(AddItems.this, "Choose Image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(AddItems.this, "Choose Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Data check
    private boolean dataCheck(){
        if((name.getText().toString()).isEmpty()) {
            name.setError("Name Required");
            return false;
        }
        else if(company.getText().toString().isEmpty()){
            company.setError("Brand name Required");
            return false;
        }
        else if(spec.getText().toString().isEmpty()){
            spec.setError("Specification Required");
            return false;
        }
        else if(item_quantity.getText().toString().isEmpty()){
            item_quantity.setError("Item Count Required");
            return false;
        }
        else if(price.getText().toString().isEmpty()){
            price.setError("Price Required");
            return false;
        }
        else if(IMAGE_COUNT == 0){
            Toast.makeText(AddItems.this, "Minimum One Image Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Network check
    private static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Our Async class
    class DataUpload extends AsyncTask{
        String productID;

        DataUpload(String productID){
            this.productID = productID;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("Images");
            for (int i=0; i < IMAGE_COUNT; i++){
                Uri Image = ImageList.get(i);
                final StorageReference imageName = ImageFolder.child(productID+"/"+(i+1));
                final int finalI = i+1;
                imageName.putFile(Image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            //                                @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Image upload successfully
                                Log.d(TAG, "Image "+ finalI +" added." );

                                if(finalI == IMAGE_COUNT){
                                    Toast.makeText(AddItems.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                                    toHome();
                                }
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error, Image not uploaded
                                addItemProgressBar.setVisibility(View.INVISIBLE);
                                addItemLayout.setVisibility(View.VISIBLE);
                                Log.d(TAG, "Error : "+e);
                                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            return null;
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        toHome();
        return true;
    }

    @Override
    public void onBackPressed() {
        toHome();
    }

    void toHome(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}