package com.example.alomrane;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.alomrane.databinding.ActivityMainBinding;
import com.example.alomrane.databinding.ActivityMapsactivity1Binding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MapsActivity1 extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap gMap;
    private LocationManager locationManager;
    private boolean isFabOpen = true;  // Changez cela à false
    private CardView cardView;
    private ActivityMainBinding binding;
    String appartement_id;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_LOCATION = 123;
    // private static final int REQUEST_CODE_LOCATION = 2;
    private double latitude;
    private double longitude;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_mapsactivity1); // Supprimez cette ligne
        ActivityMapsactivity1Binding binding = ActivityMapsactivity1Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Button projectbutton = findViewById(R.id.projectbutton);
        Button addButton = findViewById(R.id.add_project_button);
        binding.editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                manageMenu(binding);
            }

            private void manageMenu(ActivityMapsactivity1Binding binding) {

                if (isFabOpen) {
                    isFabOpen = false;
                    binding.editFab.setImageResource(R.drawable.ic_edit);
                    //binding.editFab.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity1.this, R.color.bg_color)));
                    binding.editFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity1.this, R.color.white)));
                    binding.layoutMenu.setBackgroundResource(0);
                    binding.aspectRatioFab.hide();
                    binding.cropFab.hide();
                    binding.rotateFab.hide();
                } else {
                    isFabOpen = true;
                    binding.editFab.setImageResource(R.drawable.ic_close);
                   // binding.editFab.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity1.this, R.color.bg_color)));
                    binding.editFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity1.this, R.color.white)));
                    binding.layoutMenu.setBackgroundResource(R.drawable.bg_top_corner);
                    binding.aspectRatioFab.show();
                    binding.cropFab.show();
                    binding.rotateFab.show();
                }


                }

        });
        // Récupérer l'id de l'appartement depuis l'intent
        appartement_id = getIntent().getStringExtra("appartement_id");

        projectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity1.this, AnmenuActivity.class);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity1.this, appartementActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LOCATION);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, start requesting location updates
        }


        // Ajoutez cette ligne pour appeler la méthode addMarkersToMap()
        addMarkersToMap();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start requesting location updates

            }
        }
    }

    private void addMarkersToMap() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference appartementRef = db.collection("appartement");
        CollectionReference declarationsRef = db.collection("declarations");
        int markerWidth = 140;
        int markerHeight = 140;

        // Listener pour les modifications dans la collection "appartement"
        appartementRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Gérer les erreurs de récupération des données
                    return;
                }

                // Effacer tous les marqueurs existants sur la carte
                gMap.clear();

                // Ajouter les marqueurs pour les appartements à la carte
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String idappartemnt = documentSnapshot.getString("appartement_id");
                    String name = documentSnapshot.getString("name");
                    Double latitude = documentSnapshot.getDouble("latitude");
                    Double longitude = documentSnapshot.getDouble("longitude");

                    if (latitude != null && longitude != null) {
                        LatLng location = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(idappartemnt)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        Marker marker = gMap.addMarker(markerOptions);
                        marker.setTag("appartement");
                    }
                }
            }
        });

        declarationsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle the error
                    return;
                }

                // Ajouter les marqueurs pour les déclarations à la carte
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String iddeclaration = documentSnapshot.getId();
                    String imageClicked = documentSnapshot.getString("typedeclaration");
                    Double latitude = documentSnapshot.getDouble("latitude");
                    Double longitude = documentSnapshot.getDouble("longitude");

                    if (imageClicked != null && latitude != null && longitude != null) {
                        LatLng location = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(iddeclaration);
                        if (imageClicked.equals("image1")) {
                            Bitmap image1Resized = resizeMarkerIcon(120, 120, R.drawable.animals);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image1Resized));
                        } else if (imageClicked.equals("image2")) {
                            Bitmap image2Resized = resizeMarkerIcon(120, 120, R.drawable.theif);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image2Resized));
                        } else if (imageClicked.equals("image3")) {
                            Bitmap image3Resized = resizeMarkerIcon(120, 120, R.drawable.smell);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image3Resized));
                        } else if (imageClicked.equals("image4")) {
                            Bitmap image4Resized = resizeMarkerIcon(120, 120, R.drawable.light);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image4Resized));
                        } else if (imageClicked.equals("image5")) {
                            Bitmap image5Resized = resizeMarkerIcon(120, 120, R.drawable.more);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image5Resized));
                        } else if (imageClicked.equals("image6")) {
                            Bitmap image6Resized = resizeMarkerIcon(120, 120, R.drawable.hole);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image6Resized));
                        } else {
                            // Default marker icon
                            Bitmap defaultResized = resizeMarkerIcon(120, 120, R.drawable.default_image);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(defaultResized));
                        }

                        Marker marker = gMap.addMarker(markerOptions);
                        marker.setTag("declaration");
                    }
                }
            }
        });
    }


    private Bitmap resizeMarkerIcon(int width, int height, @DrawableRes int iconResourceId) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(iconResourceId);
        Bitmap originalBitmap = bitmapDrawable.getBitmap();
        return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        } else {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        LatLng currentLocation = new LatLng(31.791702, -7.09262);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        addMarkersToMap();
        gMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (!marker.getTitle().equals("Marker at Current Location")) {
            // Get the ID from the marker's title
            final String id = marker.getTitle();

            if(marker.getTag().equals("appartement")) {
                Intent intent = new Intent(MapsActivity1.this, afficheproject.class);
                intent.putExtra("appartement_id", id);
                startActivity(intent);
            } else { // C'est une déclaration
                YourActivity bottomSheetDialogFragment = YourActivity.newInstance(id);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
            }

            return true; // Indique que vous avez géré l'événement du clic sur le marqueur
        }

        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        addMarkersToMap();
    }

    }

