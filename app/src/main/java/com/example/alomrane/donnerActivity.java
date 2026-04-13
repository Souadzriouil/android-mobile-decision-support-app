package com.example.alomrane;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class donnerActivity extends AppCompatActivity {

    private EditText apportInput;
    private EditText dureeInput;
    private Spinner tauxCreditSpinner;
    private Button next_button;
    private FirebaseAuth mAuth;

    private EditText tauxCreditManualInput;
    private String appartementId; // Variable to store the apartment ID

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donner);

        // Retrieve the apartment ID from the intent
        appartementId = getIntent().getStringExtra("appartement_id");

        // Initialize the views
        apportInput = findViewById(R.id.apportInput);
        dureeInput = findViewById(R.id.dureeInput);
        tauxCreditSpinner = findViewById(R.id.tauxCreditSpinner);
        tauxCreditManualInput = findViewById(R.id.tauxCreditManualInput);
        next_button = findViewById(R.id.next_button);

        // Set the range for the down payment input
        apportInput.setFilters(new InputFilter[]{new InputFilterMinMax(0, 2500000)});

        // Set the range for the loan duration input
        dureeInput.setFilters(new InputFilter[]{new InputFilterMinMax(1, 30)});

        // Populate the credit rate spinner with current rates in Morocco
        List<String> creditRates = new ArrayList<>();
        creditRates.add("4%");
        creditRates.add("5%");
        creditRates.add("6%");
        creditRates.add("Manuel");

        ArrayAdapter<String> creditRatesAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, creditRates);
        creditRatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tauxCreditSpinner.setAdapter(creditRatesAdapter);

        // Show/hide the manual input field based on the selected spinner item
        tauxCreditSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == creditRates.size() - 1) {
                    tauxCreditManualInput.setVisibility(View.VISIBLE);
                } else {
                    tauxCreditManualInput.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        mAuth = FirebaseAuth.getInstance();

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apportValue = apportInput.getText().toString();
                String dureeValue = dureeInput.getText().toString();
                String tauxCreditValue;

                if (apportValue.isEmpty() || dureeValue.isEmpty() ||
                        (tauxCreditSpinner.getSelectedItemPosition() == creditRates.size() - 1 && tauxCreditManualInput.getText().toString().isEmpty())) {
                    Toast.makeText(donnerActivity.this,"Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tauxCreditSpinner.getSelectedItemPosition() == creditRates.size() - 1) {
                    tauxCreditValue = tauxCreditManualInput.getText().toString();
                } else {
                    tauxCreditValue = tauxCreditSpinner.getSelectedItem().toString();
                }

                // Save the data to the "frais" collection
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String fraisId = db.collection("frais").document().getId();

                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    // Check if the user has entered a "%" sign, if not, add it
                    if (!tauxCreditValue.contains("%")) {
                        tauxCreditValue = tauxCreditValue + "%";
                    }

                    Map<String, Object> fraisData = new HashMap<>();
                    fraisData.put("fraisId", fraisId);
                    fraisData.put("appartement_id", appartementId);
                    fraisData.put("user_id", userId);
                    fraisData.put("apport", apportValue);
                    fraisData.put("duree", dureeValue);
                    fraisData.put("taux_credit", tauxCreditValue);
                    fraisData.put("mensualite", 0);

                    // Add the frais data to Firestore
                    db.collection("frais")
                            .add(fraisData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Success, move to the next activity
                                    Intent intent = new Intent(donnerActivity.this, CalculeActivity.class);
                                    intent.putExtra("appartement_id", appartementId);
                                    intent.putExtra("fraisId", fraisId);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failure, handle the error
                                    Toast.makeText(donnerActivity.this, "Failed to save frais data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(donnerActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
