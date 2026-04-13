package com.example.alomrane;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class appartementActivity2 extends AppCompatActivity {
    private EditText superficieEditText;
    private Spinner etageSpinner;
    private Spinner chambreSpinner;

    private Button next_button;

    private String appartement_id;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appartement2);

        // Récupérer les éléments de la vue par leur ID
        superficieEditText = findViewById(R.id.superficieInput);
        etageSpinner = findViewById(R.id.etageSpinner);
        chambreSpinner = findViewById(R.id.chambreSpinner);
        next_button = findViewById(R.id.next_button);



        // Récupérer l'identifiant de l'appartement
        appartement_id = getIntent().getStringExtra("appartement_id");
        String user_id = getIntent().getStringExtra("user_id");


        List<String> etageList = new ArrayList<>();
        etageList.add("Ex: 3ème étage");
        for (int i = 0; i <= 50; i++) {
                etageList.add(String.valueOf(i));
            }
        


        ArrayAdapter<String> etageAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, etageList);
        etageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etageSpinner.setAdapter(etageAdapter);

        List<String> chambreList = new ArrayList<>();
        chambreList.add("EX: 3 chambres");
        for (int i = 1; i <= 10; i++) {
            chambreList.add(String.valueOf(i));
        }
        ArrayAdapter<String> chambreAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, chambreList);
        chambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chambreSpinner.setAdapter(chambreAdapter);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vérifier que les champs sont remplis
                if (superficieEditText.getText().toString().isEmpty()) {
                    Toast.makeText(appartementActivity2.this, "Please enter the area", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etageSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(appartementActivity2.this, "Please choose the number of floors", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (chambreSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(appartementActivity2.this, "Please choose the number of rooms", Toast.LENGTH_SHORT).show();
                    return;
                }
// Récupérer les valeurs saisies par l'utilisateur
                int superficie = Integer.parseInt(superficieEditText.getText().toString());
                int nbEtages = Integer.parseInt(etageSpinner.getSelectedItem().toString());
                int nbChambres = Integer.parseInt(chambreSpinner.getSelectedItem().toString());

// Supprimer les documents correspondant à l'ID appartement dans la collection "room"
                db.collection("room")
                        .whereEqualTo("appartement_id", appartement_id)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String documentId = documentSnapshot.getId();
                                    db.collection("room").document(documentId).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Document supprimé avec succès : " + documentId);
                                                }
                                            })
                                              .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Erreur lors de la suppression du document " + documentId, e);
                                                }
                                            });
                                }

                                // Boucle pour ajouter les cercles dans la collection "room"
                                for (int i = 0; i < nbChambres; i++) {
                                    // Générer l'ID du cercle
                                    String idCercle = appartement_id + "" + i + "" + (int) (Math.random() * 1000000);

                                    // Créer un objet Spindou avec les attributs souhaités
                                    Map<String, Object> spindou = new HashMap<>();
                                    spindou.put("degre", 0);
                                    spindou.put("direction", "");
                                    spindou.put("idroom", idCercle);
                                    spindou.put("appartement_id", appartement_id);
                                    spindou.put("typeofroom", "Chambre" + (i + 1));
                                    spindou.put("user_id", user_id);
                                   // spindou.put("nbChambres", nbChambres);

                                    // Ajouter l'objet Spindou à la collection "room"
                                    db.collection("room").document(idCercle).set(spindou)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                   // Toast.makeText(appartementActivity2.this, "Chambre ajoutée avec succès: " + idCercle, Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Erreur lors de l'ajout de la chambre " + idCercle, e);
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Erreur lors de la récupération des documents correspondants à l'ID appartement", e);
                            }
                        });



                // Mettre à jour les attributs de l'appartement
                DocumentReference appartementRef = db.collection("appartement").document(appartement_id);
                appartementRef.update("superficie", superficie);
                appartementRef.update("nbEtages", nbEtages);
                appartementRef.update("nbChambres", nbChambres);

                // Aller vers l'activité de détails de l'appartement avec l'identifiant correspondant
                Intent intent = new Intent(appartementActivity2.this, appartementActivity3.class);
                intent.putExtra("appartement_id", appartement_id);
                startActivity(intent);

            }
        });
    }
}
