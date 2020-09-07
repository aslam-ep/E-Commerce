package com.hector.e_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Global variables
    String TAG = "E-Commerce-MainActivity";
    boolean doubleBackToExitPressedOnce = false;
    String userId="123456";
    String url,name, brand, quantity, price, pid;
    List<Product> productList;
    ProductAdapter productAdapter;

    // UI Variables
    FloatingActionButton addItem;
    ProgressBar homeProgress;
    LinearLayout homeLayout;
    TextView textView;
    RecyclerView recyclerView;

    // Firebase instances
    FirebaseFirestore db;
    FirebaseStorage fib_ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_shopping_cart_24);

        // Pointing UI elements
        addItem = findViewById(R.id.addItem);
        homeProgress = findViewById(R.id.homeProgress);
        homeLayout = findViewById(R.id.homeLayout);
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        fib_ref = FirebaseStorage.getInstance();
        productList = new ArrayList<Product>();

        db.collection("Products").whereEqualTo("VendorID", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                Log.d(TAG, "No Data Available");
                                textView.setVisibility(View.VISIBLE);
                                homeProgress.setVisibility(View.INVISIBLE);
                            }else{
                                for(QueryDocumentSnapshot document:task.getResult()) {

                                    name = document.getString("Name");
                                    brand = document.getString("Company");
                                    quantity = document.getString("Quantity");
                                    price = document.getString("Price");
                                    pid = document.getId();

                                    new ImageReader(name, brand, quantity, price, pid).execute();

                                }
                            }
                        }else{
                            Log.d(TAG, "Connection Error");
                            textView.setVisibility(View.VISIBLE);
                            homeProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        // On click listener for the button
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddItems.class);
                startActivity(i);
                finish();
            }
        });
    }

    class ImageReader extends AsyncTask{
        String name, brand, quantity, price, pid;

        public ImageReader(String name, String brand, String quantity, String price, String pid) {
            this.name = name;
            this.brand = brand;
            this.quantity = quantity;
            this.price = price;
            this.pid = pid;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            StorageReference ref = fib_ref.getReference().child("Images/" + pid + "/1");
            ref.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Log.d(TAG, name + brand + price );

                            url = String.valueOf(uri);
                            productList.add(new Product(
                                    name,
                                    brand,
                                    quantity,
                                    price + " INR",
                                    url
                            ));
                            productAdapter = new ProductAdapter(getApplicationContext(), productList);
                            recyclerView.setAdapter(productAdapter);
                            homeProgress.setVisibility(View.INVISIBLE);
                            homeLayout.setVisibility(View.VISIBLE);
                        }
                    });
            return null;
        }
    }


    // Notification icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_for_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //logout action
            case R.id.item1:
                startActivity(new Intent(MainActivity.this, BargainAcceptor.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Double back to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}