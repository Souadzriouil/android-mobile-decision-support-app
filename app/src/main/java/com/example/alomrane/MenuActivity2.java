package com.example.alomrane;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity2 extends AppCompatActivity {

    private ImageView image2, image1, image3, image4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);

        // Récupération des ImageView à partir des identifiants de ressources
        image2 = findViewById(R.id.image2);
        image1 = findViewById(R.id.image1);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        // Définir la visibilité de toutes les ImageView à "invisible"
        image1.setVisibility(ImageView.INVISIBLE);
        image2.setVisibility(ImageView.INVISIBLE);
        image3.setVisibility(ImageView.INVISIBLE);
        image4.setVisibility(ImageView.INVISIBLE);

        // Afficher les images avec un délai progressivement plus long
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image1.setVisibility(ImageView.VISIBLE);
                // Appliquer l'animation de booklas à image1
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                image1.startAnimation(anim);
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image2.setVisibility(ImageView.VISIBLE);
                // Appliquer l'animation de booklas à image1
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                image2.startAnimation(anim);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image3.setVisibility(ImageView.VISIBLE);
                // Appliquer l'animation de booklas à image1
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                image3.startAnimation(anim);
            }
        }, 1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image4.setVisibility(ImageView.VISIBLE);
                // Appliquer l'animation de booklas à image1
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                image4.startAnimation(anim);
            }
        }, 2000);

        // Définir le OnClickListener pour image2
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(getApplicationContext(), appartementActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                image2.startAnimation(anim);
            }
        });
    }
}
