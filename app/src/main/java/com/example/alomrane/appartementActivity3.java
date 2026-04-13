package com.example.alomrane;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class appartementActivity3 extends AppCompatActivity {

    private CheckBox parkingCheckbox;
    private CheckBox balconCheckbox;
    private CheckBox cuisineCheckbox;
    private Button next_button;

    private String appartement_id;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appartement3);

        parkingCheckbox = findViewById(R.id.parkingCheckbox);
        balconCheckbox = findViewById(R.id.balconCheckbox);
        cuisineCheckbox = findViewById(R.id.cuisineCheckbox);
        next_button = findViewById(R.id.next_button);

        // Récupérer l'identifiant de l'appartement
        appartement_id = getIntent().getStringExtra("appartement_id");

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer les valeurs saisies par l'utilisateur


                // Mettre à jour les attributs de l'appartement
                DocumentReference appartementRef = db.collection("appartement").document(appartement_id);

                appartementRef.update("parking", parkingCheckbox.isChecked());
                appartementRef.update("balcon", balconCheckbox.isChecked());
                appartementRef.update("cuisine", cuisineCheckbox.isChecked());
                appartementRef.update("Ascenseur", cuisineCheckbox.isChecked());

                // Aller vers l'activité de détails de l'appartement avec l'identifiant correspondant
                Intent intent = new Intent(appartementActivity3.this, ChambreActivity.class);
                intent.putExtra("appartement_id", appartement_id);
                startActivity(intent);


            }
        });



    }
}