package com.example.alomrane;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity2 extends AppCompatActivity {

    private TextView responseTextView;
    private FirebaseFirestore db;
    private String prix;
    private String appartement_id;
    private double superficie;
    private Number nbChambres;
    private Number Etages;
    private boolean cuisineéquipée;
    private boolean hasParking;
    private boolean hasBalcon;
    private Number latitude;
    private Number longitude;
    String typeChambre ;
    String direction;
    private String Id_AI;

    private String roomsInfo ;

    ImageButton Next;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        responseTextView = findViewById(R.id.responseTextView);
        Next=findViewById(R.id.Next);


        // Get the project ID from the intent
        Intent intent = getIntent();
        appartement_id = intent.getStringExtra("appartement_id");


        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, AichambresActivity.class);
                intent.putExtra("appartement_id", appartement_id);
                startActivity(intent);
            }
        });
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


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
                                superficie = document.getDouble("superficie");
                                nbChambres = document.getDouble("nbChambres");
                                Etages = document.getDouble("nbEtages");
                                cuisineéquipée = document.getBoolean("cuisine");
                                hasParking = document.getBoolean("parking");
                                hasBalcon = document.getBoolean("balcon");
                                prix = document.getString("price");
                                latitude=document.getDouble("latitude");
                                longitude=document.getDouble("longitude");


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
                                            roomsInfo = ""; // Clear the previous rooms info

                                            for (QueryDocumentSnapshot document : value) {
                                                // Récupérer les données de Spinbou
                                                typeChambre = document.getString("typeofroom");
                                                direction = document.getString("direction");

                                                // Ajouter les informations de la chambre à la variable roomsInfo
                                                roomsInfo += typeChambre + "orientation"+ direction + " ";


                                            }
                                            String question =
                                                    "lister tous les avantages et les inconvénients de cet appar situé au Maroc:  Location: "+ latitude+longitude+ " Area: " +superficie + "m Nbr of rooms " +nbChambres+ "Etage:"+ Etages +" cuisine équipée: " +
                                                            " Parking: " + (hasParking ? "Oui" : "Non") +" Balcon: " + (hasBalcon ? "cuisine equiper" : "Non") +
                                                            " Price"+ prix+"dhs" ;

                                            // Send the question to the API
                                            sendQuestion(question);

                                        }


                                    }
                                });

                    }
                });


    }

    void addResponse(String response) {
        responseTextView.setText(""); // Efface le contenu précédent du TextView

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        final String text = response.substring(0, i + 1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                responseTextView.setText(text);
                            }
                        });
                        Thread.sleep(20); // Temps de pause entre chaque lettre (ajustez selon vos préférences)
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void sendQuestion(String question) {
        callAPI(question);
    }

    void callAPI(String question) {
        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray messagesArray = new JSONArray();

            // Add system message
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a real estate expert.");
            messagesArray.put(systemMessage);

            // Add user message
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messagesArray.put(userMessage);

            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messagesArray);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API.API_URL) // Use the correct URL for chat completions
                .header("Authorization", "Bearer " + API.API) // Make sure to use a valid API key
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        JSONObject messageObject = jsonArray.getJSONObject(0).getJSONObject("message");
                        String result = messageObject.getString("content");
                        addResponse(result.trim());

                        String userId = currentUser.getUid();

                        // Generate unique AI id
                        Id_AI = db.collection("AI").document().getId();

                        // Create a new document in the "AI" collection
                        Map<String, Object> aiDocument = new HashMap<>();
                        aiDocument.put("Id_AI", Id_AI);
                        aiDocument.put("appartement_id", appartement_id);
                        aiDocument.put("user_id", userId);
                        aiDocument.put("descriptionapp", result.trim());

                        db.collection("AI").document(Id_AI)
                                .set(aiDocument)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "AI document added successfully");
                                        } else {
                                            Log.e(TAG, "Failed to add AI document", task.getException());
                                        }
                                    }
                                });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }

        });

    }


}