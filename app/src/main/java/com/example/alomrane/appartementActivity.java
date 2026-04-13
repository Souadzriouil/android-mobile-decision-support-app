package com.example.alomrane;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class appartementActivity extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageButton cameraButton;
    private ImageButton galleryButton;
    private TextView imageUrlTextView,mTextViewLocation;
    EditText txtdata,tttt3;
    Button next_button;
    private String imageUrl;
    private View background;

    private double mLatitude;
    private double mLongitude;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        setContentView(R.layout.activity_appartement);
        background = findViewById(R.id.background);

        cameraButton = findViewById(R.id.cameraImageButton);
        galleryButton = findViewById(R.id.galleryImageButton);
        imageUrlTextView = findViewById(R.id.imageUrlTextView);
        mTextViewLocation = findViewById(R.id.txtLocation);
        next_button = findViewById(R.id.next_button);
        txtdata = findViewById(R.id.txtdata);

        tttt3 = findViewById(R.id.tttt3);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (savedInstanceState == null) {
            background.setVisibility(View.INVISIBLE);

            final ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();

            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                });
            }

        }




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
                mTextViewLocation.setText("Lat: " + mLatitude + ", Long: " + mLongitude);
            } else {
                // Aucune localisation trouvée
            //    Toast.makeText(this, "Impossible de trouver la localisation actuelle", Toast.LENGTH_SHORT).show();
            }
        }


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSelectPictureIntent();
            }
        });


        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();

           // Récupérer la valeur du champ de prix
                String prixString = tttt3.getText().toString();

                // Vérifier que les champs sont remplis
                if (txtdata.getText().toString().isEmpty()) {
                    Toast.makeText(appartementActivity.this, "Please enter name of project", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tttt3.getText().toString().isEmpty()) {
                    Toast.makeText(appartementActivity.this, "Please enter price of project", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imageUrlTextView.getText().toString().isEmpty()) {
                    Toast.makeText(appartementActivity.this, "Please enter image of project", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Récupérer l'ID de l'utilisateur à partir de Firebase Auth
                String userId = user.getUid();
                String appartementId = mFirestore.collection("appartement").document().getId();
                // Stocker les valeurs dans un objet Map
                Map<String, Object> appartement = new HashMap<>();
                appartement.put("appartement_id", appartementId);
                appartement.put("user_id", userId);
                appartement.put("name", txtdata.getText().toString());
                appartement.put("image_url", imageUrl);
                appartement.put("price", tttt3.getText().toString());
                appartement.put("latitude", mLatitude);
                appartement.put("longitude", mLongitude);
                appartement.put("superficie", 0000000);
                appartement.put("nbEtages", 000000);
                appartement.put("nbChambres", 0000000);
                appartement.put("parking", false);
                appartement.put("balcon", false);
                appartement.put("cuisine", false);
                appartement.put("Ascenseur", false);

                // Enregistrer l'appartement dans Firestore
                mFirestore.collection("appartement").document(appartementId).set(appartement)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("MainActivity", "Appartement saved to Firestore");


                                // afficher un message de succès
                                Toast.makeText(appartementActivity.this, "Appartement ajouté avec succès", Toast.LENGTH_SHORT).show();




                                // Naviguer vers la prochaine activité en passant l'ID de l'appartement

                                Intent intent = new Intent(appartementActivity.this, appartementActivity2.class);
                                intent.putExtra("appartement_id", appartementId);
                                intent.putExtra("user_id", userId);
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("MainActivity", "Failed to save appartement to Firestore: " + e.getMessage());
                                Toast.makeText(appartementActivity.this, "Erreur lors de l'ajout de l'appartement", Toast.LENGTH_SHORT).show();
                            }
                        });


            }



        });


    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private void dispatchSelectPictureIntent() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(selectPictureIntent, REQUEST_IMAGE_PICK);
    }
    private Uri bitmapToUriConverter(Bitmap bitmap) {
        Uri uri = null;
        try {
            File file = new File(getBaseContext().getExternalCacheDir(), "image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Uri imageUri = bitmapToUriConverter(imageBitmap);
                saveImageToExternalStorage(imageUri);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                saveImageToExternalStorage(selectedImageUri);
            }
        }
    }

    private void saveImageToExternalStorage(Uri imageUri) {
        // upload the image to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final String uuid = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child("images/" + uuid + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("MainActivity", "Failed to upload image: " + e.getMessage());
                imageUrlTextView.setText("Erreur lors de l'ajout de l'image");
                imageUrlTextView.setTextColor(Color.RED);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get the URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // save the URL to imageUrl variable
                        imageUrl = uri.toString();
                        Log.d("MainActivity", "Image URL saved to imageUrl variable");
                        imageUrlTextView.setText("L'image a été ajoutée avec succès");
                        imageUrlTextView.setTextColor(Color.WHITE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MainActivity", "Failed to get image URL: " + e.getMessage());
                    }
                });
            }
        });
    }
    private void circularRevealActivity() {
        int cx = background.getRight() - getDips(44);
        int cy = background.getBottom() - getDips(44);

        float finalRadius = Math.max(background.getWidth(), background.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                background,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(1000);
        background.setVisibility(View.VISIBLE);
        circularReveal.start();

    }
    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = background.getWidth() - getDips(44);
            int cy = background.getBottom() - getDips(44);

            float finalRadius = Math.max(background.getWidth(), background.getHeight());
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(background, cx, cy, finalRadius, 0);

            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    background.setVisibility(View.INVISIBLE);
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            circularReveal.setDuration(1000);
            circularReveal.start();
        }
        else {
            super.onBackPressed();
        }
    }
}