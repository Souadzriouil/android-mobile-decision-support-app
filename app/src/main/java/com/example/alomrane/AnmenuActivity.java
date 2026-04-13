package com.example.alomrane;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AnmenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView image1, image2, image3, image4, image5, image6;
    private List<ImageView> imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decmenu);

        // Références des images dans le layout
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);

        // Définir les écouteurs de clic pour chaque image
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);
        image6.setOnClickListener(this);

        // Ajouter les images à la liste
        imageViews = new ArrayList<>();
        imageViews.add(image1);
        imageViews.add(image2);
        imageViews.add(image3);
        imageViews.add(image4);
        imageViews.add(image5);
        imageViews.add(image6);

        // Attendre que le layout soit complètement affiché pour positionner les images
        ViewTreeObserver viewTreeObserver = findViewById(R.id.container).getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Supprimer le listener après que le layout soit complètement affiché
                findViewById(R.id.container).getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int centerX = findViewById(R.id.container).getWidth() / 2;
                int centerY = findViewById(R.id.container).getHeight() / 2;
                int radius = (int) (Math.min(centerX, centerY) / 2);  // Ajuster le rayon selon les besoins
                int spacing = 80; // Espacement entre les images

                // Positionner les images en cercle avec espacement
                int numImages = imageViews.size();
                double angleInterval = 2 * Math.PI / numImages;

                int i = 0;
                for (ImageView imageView : imageViews) {
                    double angle = (2 * Math.PI * i) / numImages;

                    // Calculer la position de l'ImageView
                    int x = (int) (centerX + (radius + spacing) * Math.cos(angle));
                    int y = (int) (centerY + (radius + spacing) * Math.sin(angle));

                    // Définir la position de l'ImageView
                    imageView.setX(x - imageView.getWidth() / 2);
                    imageView.setY(y - imageView.getHeight() / 2);

                    i++;
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(AnmenuActivity.this, declarationActivity.class);
        String imageExtra = "";

        if (v == image1) {
            imageExtra = "image1";
        } else if (v == image2) {
            imageExtra = "image2";
        } else if (v == image3) {
            imageExtra = "image3";
        } else if (v == image4) {
            imageExtra = "image4";
        } else if (v == image5) {
            imageExtra = "image5";
        } else if (v == image6) {
            imageExtra = "image6";
        }

        intent.putExtra("image", imageExtra);
        startActivity(intent);
    }


}
