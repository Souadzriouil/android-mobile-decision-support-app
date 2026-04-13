package com.example.alomrane;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class CalculeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String appartementId;
    String apport;
    String duree;
    String tauxCredit;
    double tva; // newly added variable

    Button telechagerpdf;

    private TextView resultTextView;
    double totalAmount;

    double mensualite;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcule);

        resultTextView = findViewById(R.id.resultTextView);
        String fraisId = getIntent().getStringExtra("fraisId");
        appartementId = getIntent().getStringExtra("appartement_id");
        telechagerpdf = findViewById(R.id.telechagerpdf);
        db = FirebaseFirestore.getInstance();

        telechagerpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showFileNameDialog();
                } else {
                    ActivityCompat.requestPermissions(CalculeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });


        db.collection("appartement").document(appartementId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            totalAmount = Double.parseDouble(documentSnapshot.getString("price"));

                            db.collection("frais").whereEqualTo("appartement_id", appartementId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                                apport = documentSnapshot.getString("apport");
                                                duree = documentSnapshot.getString("duree");
                                                tauxCredit = documentSnapshot.getString("taux_credit");
                                                mensualite = documentSnapshot.getDouble("mensualite");


                                                double apportValue = Double.parseDouble(apport);
                                                int dureeValue = Integer.parseInt(duree);
                                                double tauxCreditValue;

                                                if (tauxCredit.contains("%")) {
                                                    tauxCreditValue = Double.parseDouble(tauxCredit.replace("%", "")) / 100;
                                                } else {
                                                    tauxCreditValue = Double.parseDouble(tauxCredit) / 100;
                                                }

                                                double tauxMensuel = tauxCreditValue / 12;
                                                double montantCredit = totalAmount - apportValue;
                                                double mensualite = (montantCredit * tauxMensuel) / (1 - Math.pow(1 + tauxMensuel, -dureeValue * 12));

                                                // Update "mensualite" in "frais" collection
                                                db.collection("frais").document(documentSnapshot.getId())
                                                        .update("mensualite", mensualite)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(CalculeActivity.this, "Mensualite updated successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(CalculeActivity.this, "Failed to update mensualite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                // Calculate the fees
                                                double enregistrementPercentage = 0.04; // 4% of the purchase price
                                                double conservationFoncierePercentage = 0.015; // 1.5% of the purchase price
                                                double conservationFonciereFixedAmount = 150; // Fixed amount of MAD 150
                                                double certificatsProprieteAmount = 300; // Fixed amount of MAD 300
                                                double fraisAnnexesAmount = 1000; // Fixed amount of MAD 1000
                                                double honorairesAmount = 0; // Update the calculation
                                                double honorairesVATAmount = 0; // Update the calculation
                                                double pret = totalAmount - apportValue;
                                                // Calculate the fees
                                                double enregistrement = totalAmount * 0.04; // 4% of the purchase price
                                                double conservationFonciere = totalAmount * 0.015 + 200; // 1.5% of the purchase price plus a fixed amount of MAD 150
                                                double Fraishypothèque = pret * 0.015;
                                                double certificatsPropriete = 200; // Fixed amount of MAD 020
                                                double fraisdivers = 3000; // Fixed amount of MAD 1000
                                                //double fraisnotaire = totalAmount * 0.01;

                                                double fraisnotaire = totalAmount * 0.01; // 1% of the total amount
                                                tva = fraisnotaire * 0.10; // 10% of honoraires

                                                // Calculate the total notary fees
                                                double total = enregistrement + conservationFonciere + fraisnotaire + certificatsPropriete + Fraishypothèque + tva + pret;


                                                // Create the result string
                                                String result = "Sur une durée de " + duree + " ans avec un taux de " + tauxCredit +
                                                        " et un apport de " + apport + " MAD, voici votre mensualité : " +
                                                        "avec la prime d'assurance de " + String.format("%.2f", mensualite + 100) + " MAD" +
                                                        " et sans prime d'assurance de " + String.format("%.2f", mensualite) + " MAD." +
                                                        "\n\nDétail des frais notariaux:\n" +
                                                        "Enregistrement: " + String.format("%.2f", enregistrement) + " MAD\n" +
                                                        "Conservation foncière: " + String.format("%.2f", conservationFonciere) + " MAD\n" +
                                                        "Frais d'hypothèque :" + String.format("%.2f", Fraishypothèque) + "MAD\n" +
                                                        "Prêt: " + String.format("%.2f", pret) + " MAD\n" +
                                                        "frais divers: " + String.format("%.2f", fraisdivers) + " MAD\n" +
                                                        "Certificats de propriété: " + String.format("%.2f", certificatsPropriete) + " MAD\n" +
                                                        "Frais de notaire: " + String.format("%.2f", fraisnotaire) + " MAD\n" +
                                                        "TVA: " + String.format("%.2f", tva) + " MAD\n" +
                                                        "Total: " + String.format("%.2f", total) + " MAD";

                                                resultTextView.setText(result);
                                                // Create a new Notaire object without notaireId
                                                Map<String, Object> notaire = new HashMap<>();
                                                notaire.put("fraisId", fraisId); // Set this value
                                                notaire.put("total", total);
                                                notaire.put("enregistrement", enregistrement);
                                                notaire.put("conservationFonciere", conservationFonciere);
                                                notaire.put("fraisnotaire", fraisnotaire);
                                                notaire.put("certificatsPropriete", certificatsPropriete);
                                                notaire.put("Fraishypothèque", Fraishypothèque);
                                                notaire.put("tva", tva);
                                                notaire.put("pret", pret);
                                                notaire.put("idappartement", appartementId);

                                                // Add a new document to the "notaire" collection
                                                db.collection("notaire")
                                                        .add(notaire)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                // Document a1dded successfully, now get the ID and update the document
                                                                String notaireId = documentReference.getId();
                                                                notaire.put("notaireId", notaireId);

                                                                documentReference.update(notaire)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(CalculeActivity.this, "NotaireId updated successfully", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(CalculeActivity.this, "Failed to update NotaireId: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(CalculeActivity.this, "Error adding notaire document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG", "Permission: " + permissions[0] + " was " + grantResults[0]);
        }
    }

    private void showFileNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nom du fichier");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString().trim();
                savePdf(fileName);
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // ...

    private void savePdf(String fileName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appartement").document(appartementId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String info =
                                    "Société de construction : " + documentSnapshot.getString("name") + "\n" +
                                    "Prix : " + documentSnapshot.getString("price") + "\n" +
                                    "Superficie : " + documentSnapshot.getDouble("superficie") + "\n" +
                                    "Nombre de chambres : " + documentSnapshot.getDouble("nbChambres") + "\n" +
                                    "Nombre d'étages : " + documentSnapshot.getDouble("nbEtages") + "\n" +
                                    "Parking : " + documentSnapshot.getBoolean("parking") + "\n" +
                                    "Balcon : " + documentSnapshot.getBoolean("balcon") + "\n" +
                                    "Cuisine : " + documentSnapshot.getBoolean("cuisine") + "\n";

                            db.collection("room")
                                    .whereEqualTo("appartement_id", appartementId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            StringBuilder roomInfo = new StringBuilder();
                                            for (QueryDocumentSnapshot roomSnapshot : queryDocumentSnapshots) {
                                                String roomType = roomSnapshot.getString("typeofroom");
                                                String direction = roomSnapshot.getString("direction");

                                                roomInfo.append("").append(roomType).append(",");
                                                roomInfo.append(" Direction : ").append(direction).append("\n");

                                            }

                                            // Fetch data from "frais" collection
                                            db.collection("frais")
                                                    .whereEqualTo("appartement_id", appartementId)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot notaireSnapshots) {
                                                            StringBuilder notaireInfo = new StringBuilder();
                                                            for (QueryDocumentSnapshot notaireSnapshot : notaireSnapshots) {
                                                                // Assume that "data_field" is the field name you want to retrieve
                                                                String duree = notaireSnapshot.getString("duree");
                                                                String tauxCredit = notaireSnapshot.getString("taux_credit");
                                                                String apport = notaireSnapshot.getString("apport");
                                                                double mensualite = notaireSnapshot.getDouble("mensualite");

                                                                String result = "Sur une durée de " + duree + " ans avec un taux de " + tauxCredit +
                                                                        " et un apport de " + apport + " MAD, voici votre mensualité : " +
                                                                        "avec la prime d'assurance de " + String.format("%.2f", mensualite + 100) + " MAD" +
                                                                        " et sans prime d'assurance de " + String.format("%.2f", mensualite) + " MAD.";

                                                                notaireInfo.append(result).append("\n");
                                                            }
                                                            // Fetch data from "notaire" collection
                                                            db.collection("notaire")
                                                                    .whereEqualTo("idappartement", appartementId)
                                                                    .get()
                                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot notaireSnapshots) {
                                                                            StringBuilder notaireDataInfo = new StringBuilder();
                                                                            for (QueryDocumentSnapshot notaireSnapshot : notaireSnapshots) {
                                                                                // Assume that "data_field" is the field name you want to retrieve
                                                                                double pret = notaireSnapshot.getDouble("pret");
                                                                                double Fraishypothèque = notaireSnapshot.getDouble("Fraishypothèque");
                                                                                double certificatsPropriete = notaireSnapshot.getDouble("certificatsPropriete");
                                                                                double enregistrement = notaireSnapshot.getDouble("enregistrement");
                                                                                double fraisnotaire = notaireSnapshot.getDouble("fraisnotaire");
                                                                                double conservationFonciere = notaireSnapshot.getDouble("conservationFonciere");
                                                                                double tva = notaireSnapshot.getDouble("tva");
                                                                                double total = notaireSnapshot.getDouble("total");

                                                                                // Append the data to the StringBuilder
                                                                                notaireDataInfo.append("Prêt : ").append(pret).append("\n");
                                                                                notaireDataInfo.append("Frais d'hypothèque : ").append(Fraishypothèque).append("\n");
                                                                                notaireDataInfo.append("Certificats de propriété : ").append(certificatsPropriete).append("\n");
                                                                                notaireDataInfo.append("Enregistrement : ").append(enregistrement).append("\n");
                                                                                notaireDataInfo.append("Frais de notaire : ").append(fraisnotaire).append("\n");
                                                                                notaireDataInfo.append("Conservation Fonciere : ").append(conservationFonciere).append("\n");
                                                                                notaireDataInfo.append("TVA : ").append(tva).append("\n");
                                                                                notaireDataInfo.append("Total : ").append(total).append("\n");
                                                                            }
                                                                            // Fetch data from "notaire" collection
                                                                            db.collection("AI")
                                                                                    .whereEqualTo("appartement_id", appartementId)
                                                                                    .get()
                                                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(QuerySnapshot AISnapshots) {
                                                                                            StringBuilder AIDataInfo = new StringBuilder();
                                                                                            for (QueryDocumentSnapshot AISnapshot : AISnapshots) {
                                                                                                // Assume that "data_field" is the field name you want to retrieve
                                                                                                String description = AISnapshot.getString("description");

                                                                                                // Append the data to the StringBuilder
                                                                                               AIDataInfo.append("description : ").append(description).append("\n");

                                                                                            }

                                                                            File path = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName + ".pdf");

                                                                            try {
                                                                                PdfWriter writer = new PdfWriter(path);
                                                                                PdfDocument pdf = new PdfDocument(writer);
                                                                                Document document = new Document(pdf);

                                                                                document.add(new Paragraph("Détails de l'appartement :")
                                                                                        .setBold()
                                                                                        .setFontSize(16)
                                                                                        .setTextAlignment(TextAlignment.CENTER));
                                                                                document.add(new Paragraph(info));

                                                                                document.add(new Paragraph("Détails des chambres :")
                                                                                        .setBold()
                                                                                        .setFontSize(16)
                                                                                        .setTextAlignment(TextAlignment.CENTER));
                                                                                document.add(new Paragraph(roomInfo.toString()));

                                                                                // Add notaire data to the PDF
                                                                                document.add(new Paragraph("Les données financières :")
                                                                                        .setBold()
                                                                                        .setFontSize(16)
                                                                                        .setTextAlignment(TextAlignment.CENTER));
                                                                                document.add(new Paragraph(notaireInfo.toString()));

                                                                                // Add notaire data to the PDF
                                                                                document.add(new Paragraph("")
                                                                                        .setBold()
                                                                                        .setFontSize(16)
                                                                                        .setTextAlignment(TextAlignment.CENTER));
                                                                                document.add(new Paragraph(notaireDataInfo.toString()));

                                                                                // Add AI data to the PDF
                                                                                document.add(new Paragraph("description :")
                                                                                        .setBold()
                                                                                        .setFontSize(16)
                                                                                        .setTextAlignment(TextAlignment.CENTER));
                                                                                document.add(new Paragraph(AIDataInfo.toString()));

                                                                                document.close();


                                                                                Toast.makeText(CalculeActivity.this, "Fichier PDF enregistré avec succès", Toast.LENGTH_SHORT).show();
                                                                            } catch (Exception e) {
                                                                                Toast.makeText(CalculeActivity.this, "Erreur lors de l'enregistrement du fichier PDF : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }

                                                                            Uri pdfUri = FileProvider.getUriForFile(
                                                                                    CalculeActivity.this,
                                                                                    CalculeActivity.this.getApplicationContext().getPackageName() + ".provider",
                                                                                    path);

                                                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                            intent.setDataAndType(pdfUri, "application/pdf");
                                                                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                                            try {
                                                                                startActivity(intent);
                                                                            } catch (Exception e) {
                                                                                Toast.makeText(CalculeActivity.this, "Aucune application pour visualiser les fichiers PDF n'est installée.", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(CalculeActivity.this, "Erreur lors de la récupération des données du notaire : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(CalculeActivity.this, "Erreur lors de la récupération des données des chambres : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(CalculeActivity.this, "Erreur lors de la récupération des données du notaire : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CalculeActivity.this, "Erreur lors de la récupération du document : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }


                    }
                });
    }
}





