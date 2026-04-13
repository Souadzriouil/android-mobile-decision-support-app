package com.example.alomrane;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class AichambresActivity extends AppCompatActivity {
    private TextView responseTextView;
    private FirebaseFirestore db;
    private String appartement_id;

    String typeChambre ;
    String direction;
    private String Id_AI;

    private String roomsInfo ;

    Button Next;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichambres);

        responseTextView = findViewById(R.id.responseTextView);
        Next=findViewById(R.id.Next);


        // Get the project ID from the intent
        Intent intent = getIntent();
        appartement_id = intent.getStringExtra("appartement_id");


        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AichambresActivity.this, donnerActivity.class);
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
                                                    "lister tous les avantages et les inconvénients de chaque chambre: " +roomsInfo;

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
                        aiDocument.put("description", result.trim());

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