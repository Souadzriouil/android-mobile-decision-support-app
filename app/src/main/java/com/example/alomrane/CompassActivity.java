package com.example.alomrane;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    // device sensor manager
    private SensorManager SensorManage;

    // define the compass picture that will be use
    private ImageView compassimage;

    // record the angle turned of the compass picture
    private float DegreeStart = 0f;
    private String idroom = null;


    TextView DegreeTV;

    Button btnko, btnok;
    Spinner spinner;
    Member member;
    // Global variable for degree
    private float degree = 0f;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        //
        compassimage = (ImageView) findViewById(R.id.imageView7);

        // TextView that will display the degree
        DegreeTV = (TextView) findViewById(R.id.textView6);

        //button ko
        btnko = findViewById(R.id.btn_cancel);
        btnko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        btnok = findViewById(R.id.btn_ok);
        spinner = findViewById(R.id.spinner);
        member = new Member();
        List<String> Categories = new ArrayList<>();
        Categories.add(0, "Sélectionner le type");
        Categories.add("chambre à coucher");
        Categories.add("Salon");
        Categories.add("Cuisine");
        Categories.add("Salle à manger");

// Création de l'adaptateur pour le spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.WHITE);
                }

                if (parent.getItemAtPosition(position).equals("Sélectionner le type")) {
                    // Ne rien faire si "Sélectionner le type" est sélectionné
                } else {
                    // Stocker le choix de l'utilisateur dans l'objet Member
                    member.setSpinner(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Gérer l'événement lorsqu'aucun élément n'est sélectionné
            }
        });



        // Retrieve the ID of the spinner from the intent
        String idroom = getIntent().getStringExtra("idroom");

// Retrieve the data for the spinner from Firebase
        FirebaseFirestore.getInstance().collection("room").document(idroom).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // If data already exists for this spinner, retrieve the "spinner" value and set it as the first item in the spinner
                            Map<String, Object> userData = documentSnapshot.getData();
                            String spinnerValue = userData.get("typeofroom").toString();
                            int spinnerIndex = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(spinnerValue);
                            spinner.setSelection(spinnerIndex);
                        }
                    }
                });

        // Gestion du clic sur le bouton "OK"
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the user is logged in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // Redirect to the login page if the user is not logged in
                    startActivity(new Intent(CompassActivity.this, MainActivity.class));
                    finish();
                    return;
                }

                // Check if the user has selected an option in the spinner
                if (spinner.getSelectedItem().toString().equals("Sélectionner le type")) {
                    Toast.makeText(getApplicationContext(), "veuillez sélectionner une option", Toast.LENGTH_SHORT).show();
                } else {
                    // Retrieve the user ID from Firebase Auth
                    String userId = user.getUid();

                    // Retrieve the value of "idappartement" from the "appartement" collection
                    FirebaseFirestore.getInstance().collection("appartement")
                            .whereEqualTo("user_id", userId)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        // Get the first document from the results
                                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                        // Retrieve the value of "idappartement" from the document
                                        String appartement_id = getIntent().getStringExtra("appartement_id");


                                        // Save the user and their choice in Firebase
                                        String selectedType = spinner.getSelectedItem().toString();
                                        String direction = String.valueOf(getDirection((int) degree));
                                        //  String idSpinner = UUID.randomUUID().toString(); // Generate a unique ID for each user choice

                                        final String[] idroom = {getIntent().getStringExtra("idroom")};

                                        FirebaseFirestore.getInstance().collection("room").document(idroom[0]).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            // Si des données existent déjà pour ce cercle, récupérez-les
                                                            Map<String, Object> userData = documentSnapshot.getData();
                                                            // Mettre à jour les données existantes avec les nouvelles valeurs
                                                            userData.put("typeofroom", selectedType);
                                                            userData.put("degre", degree);
                                                            userData.put("direction", direction);
                                                            // Mettre à jour les données dans Firebase
                                                            FirebaseFirestore.getInstance().collection("room").document(idroom[0]).update(userData)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            // Les données ont été mises à jour avec succès
                                                                            Toast.makeText(getApplicationContext(), "Choice updated", Toast.LENGTH_SHORT).show();
                                                                            // Enregistrer les données et terminer l'activité
                                                                            Intent intent = new Intent();
                                                                            intent.putExtra("typeofroom", selectedType);
                                                                            intent.putExtra("direction", direction);
                                                                            setResult(RESULT_OK, intent);
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            // code to handle data saving failure
                                                                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                        } else {
                                                            // Si aucune donnée n'existe encore pour ce cercle, enregistrez les nouvelles données
                                                            Map<String, Object> userData = new HashMap<>();
                                                            userData.put("user_id", userId);
                                                            userData.put("typeodroom", selectedType);
                                                            userData.put("degre", degree);
                                                            userData.put("direction", direction);
                                                            userData.put("appartement_id", appartement_id);
                                                            if (idroom[0] == null) {
                                                                idroom[0] = UUID.randomUUID().toString();
                                                            } else {
                                                                userData.put("idroom", idroom[0]);
                                                            }

                                                            FirebaseFirestore.getInstance().collection("room").document(idroom[0]).set(userData)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(getApplicationContext(), "Choice saved", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent();
                                                                            intent = new Intent(CompassActivity.this, ChambreActivity.class);
                                                                            intent.putExtra("typeofroom", selectedType);
                                                                            intent.putExtra("direction", direction);
                                                                            setResult(RESULT_OK, intent);
                                                                            finish();
                                                                        }
                                                                    })


                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            // Handle the case where the query fails
                                                                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });


                                                        }
                                                    }

                                                });
                                    }
                                }
                            });
                }
            }
        });

        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {

        // get angle around the z-axis rotated
        degree = Math.round(event.values[0]);

        DegreeTV.setText( degree + "°");

        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ra.setDuration(210);

        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
    private String getDirection(float degree){
        String[] directions = {"N","NE","E","SE","S","SW","W","NW","N"};
        int index = Math.round(degree/45f);
        index = index < 0 ? 0 : index; // vérifier que l'index n'est pas négatif
        index = index > 8 ? 8 : index; // vérifier que l'index ne dépasse pas la limite
        return directions[index];
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onStop() {
        super.onStop();
        idroom = null;
    }



}