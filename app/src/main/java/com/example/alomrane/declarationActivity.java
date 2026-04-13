package com.example.alomrane;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class declarationActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

    private ImageView imageView;
    private EditText declarationInput;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String imageUrl = null;
    private boolean imageUploading = false;
    private String idDeclaration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration);

        imageView = findViewById(R.id.imageView);
        declarationInput = findViewById(R.id.declarationInput);
        Button button = findViewById(R.id.button);
        ImageButton takePhotoButton = findViewById(R.id.takePhotoButton);
        ImageButton uploadPhotoButton = findViewById(R.id.uploadPhotoButton);

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        takePhotoButton.setOnClickListener(v -> takePhoto());
        uploadPhotoButton.setOnClickListener(v -> uploadPhoto());
        button.setOnClickListener(v -> addDeclaration());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("image")) {
            String imageClicked = intent.getStringExtra("image");
            Toast.makeText(this, "Image cliquée : " + imageClicked, Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void uploadPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE);
    }

    private void addDeclaration() {
        String declaration = declarationInput.getText().toString();

        if (declaration.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir le champ de déclaration", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageView.getDrawable() == null) {
            Toast.makeText(this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUploading) {
            Toast.makeText(this, "L'image est en cours de téléchargement, veuillez patienter", Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            String imageClicked = getIntent().getStringExtra("image");
            String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming user is already authenticated


            idDeclaration = db.collection("declarations").document().getId(); // generate a new id for the declaration

            Map<String, Object> newDeclaration = new HashMap<>();
            newDeclaration.put("idDeclaration", idDeclaration);
            newDeclaration.put("idUser", idUser);
            newDeclaration.put("declaration", declaration);
            newDeclaration.put("url", imageUrl);
            newDeclaration.put("latitude", location.getLatitude());
            newDeclaration.put("longitude", location.getLongitude());
            newDeclaration.put("typedeclaration", imageClicked);


            db.collection("declarations")
                    .document(idDeclaration)
                    .set(newDeclaration)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(declarationActivity.this, "Declaration added!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(declarationActivity.this, "Error adding declaration!", Toast.LENGTH_SHORT).show();
                        }
                    });

            Intent intent = new Intent(declarationActivity.this, MapsActivity1.class);
            intent.putExtra("imageClicked", imageClicked);
            startActivity(intent);
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == PICK_IMAGE) && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            Uri imageUri;

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
            } else if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebase(bitmap);
            } else {
                Toast.makeText(this, "Erreur lors de la récupération de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        String fileName = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + fileName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageUploading = true;

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageUploading = false;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        imageUploading = false;
                    }
                });
            }
        });
    }
}
