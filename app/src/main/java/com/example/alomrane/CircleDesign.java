package com.example.alomrane;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CircleDesign extends AppCompatActivity {

    private ImageView image2, image1, image3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_design);

        // Récupération des ImageView à partir des identifiants de ressources
        image2 = findViewById(R.id.image2);
        image1 = findViewById(R.id.image1);
        image3 = findViewById(R.id.image3);

        // Définir la visibilité de toutes les ImageView à "invisible" sauf l'image2
        image1.setVisibility(ImageView.INVISIBLE);
        image3.setVisibility(ImageView.VISIBLE);
        image2.setVisibility(ImageView.INVISIBLE);
// Afficher l'image2 avec un léger délai
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image3.setVisibility(ImageView.VISIBLE);
                // Appliquer l'animation de booklas à image2
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                image3.startAnimation(anim);
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image2.setVisibility(ImageView.VISIBLE);
                // Appliquer l'animation de booklas à image2
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                image2.startAnimation(anim);
            }
        }, 1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image1.setVisibility(ImageView.VISIBLE);
                // Appliquer l'animation de booklas à image2
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                image1.startAnimation(anim);
            }
        }, 2000);

        // Définir le OnClickListener pour booklas
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(getApplicationContext(), MapsActivity1.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                image3.startAnimation(anim);
            }
        });
    }
}