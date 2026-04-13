package com.example.alomrane;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity3 extends AppCompatActivity {
    String name;
    double prix;
    Number Superficier;

    TextView nomAppartementTextView,prixAppartementTextView,SuperficierTextView,nbrEtageTextView,nbrChmbreTextView,balconTextView,ParkingTextView,CuisineTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        nomAppartementTextView=findViewById(R.id.nomAppartementTextView);
        prixAppartementTextView=findViewById(R.id.prixAppartementTextView);
        SuperficierTextView=findViewById(R.id.SuperficierTextView);
        nbrEtageTextView=findViewById(R.id.nbrEtageTextView);
        nbrChmbreTextView=findViewById(R.id.nbrChmbreTextView);
        balconTextView=findViewById(R.id.balconTextView);
        nbrEtageTextView=findViewById(R.id.nbrEtageTextView);
        ParkingTextView=findViewById(R.id.ParkingTextView);
        CuisineTextView=findViewById(R.id.CuisineTextView);



        String appartementId = getIntent().getStringExtra("appartement_id");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the project details from Firestore
        db.collection("appartement").document(appartementId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                double superficie = document.getDouble("superficie");
                                double nbChambres = document.getDouble("nbChambres");
                                double nbEtages = document.getDouble("nbEtages");
                                String name = document.getString("name");
                                String prix = document.getString("price"); // corrected here
                                boolean hasCuisine = document.getBoolean("cuisine");
                                boolean hasParking = document.getBoolean("parking");
                                boolean hasBalcon = document.getBoolean("balcon");

                                CuisineTextView.setText(hasCuisine ? "Existe" : "n'existe pas");
                                ParkingTextView.setText(hasParking ? "Existe" : "n'existe pas");
                                balconTextView.setText(hasBalcon ? "Existe" : "n'existe pas");

                                nomAppartementTextView.setText(name);
                                prixAppartementTextView.setText(prix);
                                SuperficierTextView.setText(String.valueOf(superficie));
                                nbrEtageTextView.setText(String.valueOf(nbEtages));
                                nbrChmbreTextView.setText(String.valueOf(nbChambres));



                            }
                        }
                    }
                });
    }
}