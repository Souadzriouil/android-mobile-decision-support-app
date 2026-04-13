package com.example.alomrane;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class VideActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView priceTextView,descriptionEditText,textView,textView11,prixt,textView3,textView4,textView5, textView7;
    private ImageView imageView;
    private FirebaseFirestore db;
    private String appartement_id;

    private Button buttonOk;

    private static final String TAG = "VideActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vide);

        // Initialize the views
        descriptionEditText=findViewById(R.id.descriptionEditText);
        nameTextView = findViewById(R.id.textView2);
        textView = findViewById(R.id.textView);
        textView11=findViewById(R.id.textView11);
        prixt=findViewById(R.id.prix);
        imageView = findViewById(R.id.imageView2);
        textView3=findViewById(R.id.textView3);
        textView4=findViewById(R.id.textView4);
        textView5=findViewById(R.id.textView5);
        textView7=findViewById(R.id.textView7);
        buttonOk=findViewById(R.id.buttonOk);
        // Get the project ID from the intent
        Intent intent = getIntent();
        appartement_id = intent.getStringExtra("appartement_id");


        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get the project details from Firestore
        db.collection("appartement").document(appartement_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                // Récupérer les données de l'appartement
                                String imageUrl = document.getString("image_url");
                                double superficie = document.getDouble("superficie");
                                double nbChambres = document.getDouble("nbChambres");
                                double nbEtages = document.getDouble("nbEtages");
                                String name = document.getString("name");
                                String prix = document.getString("price");
                                boolean hasCuisine = document.getBoolean("cuisine");
                                boolean hasParking = document.getBoolean("parking");
                                boolean hasBalcon = document.getBoolean("balcon");

                                // Convertir les Number en Integer
                                int nbChambresInt = (int) nbChambres;
                                int nbEtagesInt = (int) nbEtages;
                                if(nbEtagesInt==1){
                                    String nbEtagesIntText =  String.valueOf(nbEtagesInt) +"er Étage";
                                    textView11.setText(String.valueOf(nbEtagesIntText));

                                }
                                if(nbEtagesInt>1){
                                    String nbEtagesIntText =  String.valueOf(nbEtagesInt) +"ème Étage";
                                    textView11.setText(String.valueOf(nbEtagesIntText));

                                }

                                if (hasCuisine) {

                                    textView5.setText(" Cuisine équipée");
                                }else {
                                    textView5.setText("n'existe pas");

                                }
                                if (hasParking) {
                                    textView4.setText("Parking");
                                }else {
                                    textView4.setText("n'existe pas");
                                }
                                if (hasBalcon) {
                                    textView7.setText("Balcon");
                                }else {
                                    textView7.setText("n'existe pas");
                                }
                                // Afficher les données de l'appartement
                                String superficieText = String.valueOf(superficie) + " m²";
                                String nbChambresIntText =  String.valueOf(nbChambresInt) +"Chambres";
                                String prixtText =  String.valueOf(prix) + ".0 MAD";
                                nameTextView.setText(name);
                                Glide.with(getApplicationContext())
                                        .load(imageUrl)
                                        .into(imageView);
                                textView.setText(String.valueOf(superficieText));
                                prixt.setText(String.valueOf(prixtText));
                                textView3.setText(String.valueOf(nbChambresIntText));

                            }  else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }

                        // Créer une référence pour le document dans la collection "Spinbou"
                        db.collection("room")
                                .whereEqualTo("appartement_id", appartement_id)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Log.w(TAG, "Listen failed.", error);
                                            return;
                                        }
                                        if (value != null) {
                                            for (QueryDocumentSnapshot document : value) {
// Récupérer les données de Spinbou
                                                String spinner = document.getString("typeofroom");
                                                String direction = document.getString("direction");

                                                String idappartement = document.getString("appartement_id");
                                                // Afficher les données de Spinbou
                                                StringBuilder builde= new StringBuilder(descriptionEditText.getText());
                                                builde.append("Type de chambre : ").append(spinner).append("\n");
                                                builde.append("Direction : ").append(direction).append("\n");
                                                builde.append("\n");
                                                descriptionEditText.setText(builde);
                                            }
                                        }
                                    }
                                });

                    }
                });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aller vers l'activité de détails de l'appartement avec l'identifiant correspondant
                Intent intent = new Intent(VideActivity.this, MainActivity2   .class);
                intent.putExtra("appartement_id", appartement_id);
                startActivity(intent);
            }
        });
    }
}
