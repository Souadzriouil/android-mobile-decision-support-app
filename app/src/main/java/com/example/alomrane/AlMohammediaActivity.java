package com.example.alomrane;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AlMohammediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_al_mohammedia);

        // Get data from Intent extras
       /* String image = getIntent().getStringExtra("image");
        String name = getIntent().getStringExtra("name");
        Long prix = getIntent().getLongExtra("prix", 0);

        // Set the ImageView with the image URL using Picasso library
        ImageView imageView = findViewById(R.id.imageView);
        Picasso.get().load(image).into(imageView);

        // Set the TextViews with the name and price data
        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(name);
        TextView prixTextView = findViewById(R.id.priceTextView);
        prixTextView.setText(String.valueOf(prix));*/
    }
}
