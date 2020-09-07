package com.hector.e_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BargainAcceptor extends AppCompatActivity {

    // Debug TAG
    String TAG = "BargainAcceptor";

    // Global variables
    String userId = "123456";
    String acceptedPrice, giftString;
    List<BargainProduct> products;

    // Firebase instances
    FirebaseFirestore db;

    // Layout elements
    ListView listView;
    ProgressBar progressBar;
    TextView noData;

    // Popup
    Dialog bargainPopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bargain_acceptor);

        // Action bar costume
        setTitle("BARGAIN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // UI element Pointing
        listView = findViewById(R.id.bargainList);
        progressBar = findViewById(R.id.bargainProgress);
        noData = findViewById(R.id.bargainNoData);

        bargainPopUp = new Dialog(BargainAcceptor.this);

        // Firebase instance
        db = FirebaseFirestore.getInstance();

        // Array initializing
        products = new ArrayList<>();

        // Reading data from the FireStore and setting to list view
        readData();

        // On item select action listener for listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final TextInputEditText req_price, accept_price, gift;
                Button submit;
                TextView closeButton;

                bargainPopUp.setContentView(R.layout.bargain_data_collector);
                closeButton = bargainPopUp.findViewById(R.id.closeText);
                req_price = bargainPopUp.findViewById(R.id.bargainCollectorRequestedPrice);
                accept_price = bargainPopUp.findViewById(R.id.bargainCollectorAcceptedPrice);
                gift = bargainPopUp.findViewById(R.id.bargainCollectorGift);
                submit = bargainPopUp.findViewById(R.id.bargainCollectorSubmit);

                req_price.setText((products.get(position)).getRequestedPrice());
                accept_price.setText((products.get(position)).getCurrentPrice());

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bargainPopUp.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptedPrice = accept_price.getText().toString();
                        giftString = gift.getText().toString();

                        if(acceptedPrice.isEmpty())
                            accept_price.setError("Accepted Price Required");
                        else{
                            progressBar.setVisibility(View.VISIBLE);
                            db.collection("Bargain").document((products.get(position)).getBargainId())
                                .update(
                                  "acceptedPrice",acceptedPrice,
                                        "gift", giftString,
                                        "isResponded",true
                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    bargainPopUp.dismiss();
                                    readData();
                                    Snackbar.make(findViewById(android.R.id.content), "Responded Successfully", Snackbar.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Snackbar.make(findViewById(android.R.id.content), "Check Your Connection!", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });

                // Calling the dialog box
                bargainPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bargainPopUp.show();
            }
        });
    }

    private void readData() {
        listView.setAdapter(null);
        db.collection("Bargain").whereEqualTo("vendorID", userId).whereEqualTo("isResponded",false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (!task.getResult().isEmpty()){
                                for(QueryDocumentSnapshot document: task.getResult()) {
                                    String bargainId, productId, requestedPrice, requestedQuantity, buyerId, productName, currentPrice;
                                    bargainId = document.getId();
                                    productId = document.getString("productID");
                                    productName = document.getString("productName");
                                    currentPrice = document.getString("currentPrice");
                                    buyerId = document.getString("buyerID");
                                    requestedPrice = document.getString("requestedPrice");
                                    requestedQuantity = document.getString("requestedQuantity");

                                    products.add(new BargainProduct(bargainId, productId, requestedPrice, requestedQuantity, buyerId, productName, currentPrice));

                                    if (products.size() == task.getResult().size()){
                                        BargainProductAdapter bargainProductAdapter = new BargainProductAdapter((ArrayList<BargainProduct>) products, BargainAcceptor.this);
                                        listView.setAdapter(bargainProductAdapter);

                                        progressBar.setVisibility(View.INVISIBLE);
                                        listView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }else{
                                Log.d(TAG, "Response :: No data");
                                noData.setText("No Bargain's!");
                                whenFailed();
                            }
                        }else{
                            Log.d(TAG, "Response :: Error");
                            noData.setText("Check Your Connection!");
                            whenFailed();
                        }
                    }
                });

    }

    private void whenFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        noData.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}