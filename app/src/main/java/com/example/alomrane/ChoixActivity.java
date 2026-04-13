package com.example.alomrane;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoixActivity extends AppCompatActivity {

    private EditText superficieEditText;
    private Spinner etageSpinner;
    private Spinner chambreSpinner;
    private CheckBox parkingCheckbox;
    private CheckBox balconCheckbox;
    private CheckBox cuisineCheckbox;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    TextView mTextViewLocation;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix);

        mTextViewLocation = findViewById(R.id.text_view_location);

        // Vérifiez si l'application a la permission d'accéder à la localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Demander la permission d'accéder à la localisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Obtenir la localisation actuelle
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                // Stocker la latitude et la longitude dans les variables globales
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                // Afficher la localisation actuelle dans le TextView
                mTextViewLocation.setText("Your location: " + "Lat: " + mLatitude + ", Long: " + mLongitude);
            } else {
                // Aucune localisation trouvée
                Toast.makeText(this, "Impossible de trouver la localisation actuelle", Toast.LENGTH_SHORT).show();
            }
        }


        // Récupérer les éléments de la vue par leur ID
        superficieEditText = findViewById(R.id.superficieInput);
        etageSpinner = findViewById(R.id.etageSpinner);
        chambreSpinner = findViewById(R.id.chambreSpinner);
        parkingCheckbox = findViewById(R.id.parkingCheckbox);
        balconCheckbox = findViewById(R.id.balconCheckbox);
        cuisineCheckbox = findViewById(R.id.cuisineCheckbox);
        Button okButton = findViewById(R.id.okButton);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Remplir le spinner d'étage avec des nombres de 0 à 50
        List<String> etageList = new ArrayList<>();
        etageList.add("Number of floors");
        for (int i = 0; i <= 50; i++) {
            etageList.add("Floor " + i);
        }


        ArrayAdapter<String> etageAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, etageList);
        etageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etageSpinner.setAdapter(etageAdapter);


        List<String> chambreList = new ArrayList<>();
        chambreList.add("Number of rooms");
        for (int i = 1; i <= 10; i++) {
            chambreList.add(String.valueOf(i));
        }
        ArrayAdapter<String> chambreAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, chambreList);
        chambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chambreSpinner.setAdapter(chambreAdapter);


        // Ajouter un listener sur le bouton OK
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();

                // Vérifier que les champs sont remplis
                if (superficieEditText.getText().toString().isEmpty()) {
                    Toast.makeText(ChoixActivity.this, "Please enter the area", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etageSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(ChoixActivity.this, "Please choose the number of floors", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (chambreSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(ChoixActivity.this, "Please choose the number of rooms", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Récupérer l'ID de l'utilisateur à partir de Firebase Auth
                String userId = user.getUid();
                String appartementId = mFirestore.collection("appartement").document().getId();
                // Obtenir la localisation actuelle

                // Stocker les valeurs dans un objet Map
                Map<String, Object> appartement = new HashMap<>();
                appartement.put("userId", userId);
                appartement.put("idappartement", appartementId);
                appartement.put("superficie", superficieEditText.getText().toString());
                appartement.put("nbEtages", etageSpinner.getSelectedItem().toString());
                appartement.put("nbChambres", chambreSpinner.getSelectedItem().toString());
                appartement.put("parking", parkingCheckbox.isChecked());
                appartement.put("balcon", balconCheckbox.isChecked());
                appartement.put("cuisine", cuisineCheckbox.isChecked());
                appartement.put("latitude", mLatitude);
                appartement.put("longitude", mLongitude);

                // Récupérer l'instance de FirebaseFirestore
                int nbChambres = Integer.parseInt(chambreSpinner.getSelectedItem().toString());

                // Boucle pour ajouter les cercles dans la collection Spindou
                for (int i = 0; i < nbChambres; i++) {
                    // Générer l'ID du cercle
                    String  idCercle = appartementId + "" + i + "" + (int) (Math.random() * 1000000);

                    // Créer un objet Spindou avec les attributs souhaités
                    Map<String, Object> spindou = new HashMap<>();
                    spindou.put("degre", 0);
                    spindou.put("direction", "");
                    spindou.put("idSpinner", idCercle);
                    spindou.put("idappartement", appartementId);
                    spindou.put("spinner", "room" + (i + 1));
                    spindou.put("userId", userId);

                    // Ajouter l'objet Spindou à la collection Spindou
                    mFirestore.collection("Spinbou").document(idCercle).set(spindou)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ChoixActivity.this, "chambre added successfully"+idCercle , Toast.LENGTH_SHORT).show();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Erreur lors de l'ajout du cercle", e);
                                }
                            });
                }



                // Ajouter l'appartement à la collection "appartements" dans Firestore
                mFirestore.collection("appartement").document(appartementId).set(appartement)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChoixActivity.this, "Apartment added successfully"+appartementId, Toast.LENGTH_SHORT).show();

                                // Rediriger l'utilisateur vers la liste d'appartements
                                Intent intent = new Intent(ChoixActivity.this, ChambreActivity.class);
                              //  Intent inte = new Intent(ChoixActivity.this, VideActivity.class);
                                // Passer le nombre de chambres sélectionné en tant que paramètre à l'activité suivante
                                intent.putExtra("nbChambres", chambreSpinner.getSelectedItem().toString());
                                intent.putExtra("idAppartement", appartementId); // mettre l'ID de l'appartement dans l'intent
                                startActivity(intent);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChoixActivity.this, "Error adding apartment", Toast.LENGTH_SHORT).show();
                            }
                        });



            }
        });
    }
}