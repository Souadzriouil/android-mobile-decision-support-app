package com.example.alomrane;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ChambreActivity extends AppCompatActivity {
    private String appartement_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chambre);

        // Récupérer l'id de l'appartement depuis les paramètres
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appartement_id = extras.getString("appartement_id");
        }

// Trouver le GridLayout dans la vue
        GridLayout gridLayout = findViewById(R.id.gridLayout);

// Récupérer les documents de la collection Spinbou qui ont l'attribut idappartement égal à idAppartement
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("room")
                .whereEqualTo("appartement_id", appartement_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Erreur lors de la récupération des documents de la collection Spinbou", error);
                            return;
                        }
                        // Supprimer tous les cercles précédents
                        gridLayout.removeAllViews();
                        // Créer un cercle pour chaque document récupéré
                        for (QueryDocumentSnapshot document : value) {
                            String idroom = document.getString("idroom");
                            String spinnerValue = document.getString("typeofroom");
                            String direction=document.getString("direction");
                            // Créer un nouvel identifiant unique pour chaque cercle
                            // Vérifier si l'état du cercle est stocké dans Firebase
                            Boolean isCircleSelected = document.getBoolean("isSelected");
                            // Si l'état n'est pas stocké, définir isSelected sur false
                            if (isCircleSelected == null) {
                                isCircleSelected = false;
                            }

                            // Créer un nouveau TextView pour chaque cercle
                            TextView circleTextView = new TextView(ChambreActivity.this);
                            circleTextView.setId(View.generateViewId()); // Ajouter un ID unique
                            if (isCircleSelected) {
                                circleTextView.setBackgroundResource(R.drawable.kkkk);
                            } else {
                                circleTextView.setBackgroundResource(R.drawable.pppp);
                            }
                            circleTextView.setGravity(Gravity.CENTER);
                            circleTextView.setTextColor(Color.BLACK);
                            circleTextView.setText(spinnerValue + "\n" +direction);
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width =350;
                            params.height = 350;
                            params.setMargins(5, 5, 5, 5);
                            circleTextView.setLayoutParams(params);

                            // Ajouter un click listener au cercle
                            circleTextView.setOnClickListener(new View.OnClickListener() {
                                boolean isCircleSelected = false;
                                @Override
                                public void onClick(View v) {

                                    // Inverser l'état du cercle
                                    isCircleSelected = !isCircleSelected;
                                    // Enregistrer l'état du cercle dans Firebase
                                    db.collection("room").document(document.getId())
                                            .update("isSelected", isCircleSelected)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });

                                    // Changer le background du cercle
                                    if (isCircleSelected) {
                                        circleTextView.setBackgroundResource(R.drawable.kkkk);
                                    } else {
                                        circleTextView.setBackgroundResource(R.drawable.pppp);
                                    }
                                    Intent intent = new Intent(ChambreActivity.this, CompassActivity.class);
                                    intent.putExtra("appartement_id", appartement_id);
                                    intent.putExtra("idroom", idroom);
                                    startActivity(intent);
                                }
                            });
                            gridLayout.addView(circleTextView);
                        }


                    }

                });


        // Trouver le bouton "btnComplete" dans la vue
        Button btnComplete = findViewById(R.id.btnComplete);

        // Ajouter un click listener au bouton
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChambreActivity.this, VideActivity.class);
                intent.putExtra("appartement_id", appartement_id);
                startActivity(intent);
            }
        });

    }
}