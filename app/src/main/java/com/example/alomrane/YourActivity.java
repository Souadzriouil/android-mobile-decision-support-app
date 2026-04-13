package com.example.alomrane;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class YourActivity extends BottomSheetDialogFragment {
    private ImageView imageView;
    private TextView declarationView;
    private TextView personNameView; // Added TextView for person's name
    private String declarationId;

    public static YourActivity newInstance(String declarationId) {
        YourActivity fragment = new YourActivity();
        Bundle args = new Bundle();
        args.putString("idDeclaration", declarationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getTheme() {
        return R.style.CustomDialogStyle; // Use custom style for dialog
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_your, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            declarationId = getArguments().getString("idDeclaration");
        }

        Toast.makeText(getContext(), "declarationId" + declarationId, Toast.LENGTH_SHORT).show();

        imageView = view.findViewById(R.id.image);
        declarationView = view.findViewById(R.id.declaration);
        personNameView = view.findViewById(R.id.person_name); // Initialize personNameView
        getDeclarationFromFirebase(declarationId);

        setUserNameFromFirebaseAuth();
    }

    private void setUserNameFromFirebaseAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if (name != null) {
                personNameView.setText(name);
            } else {
                Toast.makeText(getContext(), "User name is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "User is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeclarationFromFirebase(String declarationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("declarations").document(declarationId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String declaration = document.getString("declaration");
                    String url = document.getString("url");

                    declarationView.setText(declaration);
                    Glide.with(getContext()).load(url).into(imageView);
                }
            }
        });
    }
}
