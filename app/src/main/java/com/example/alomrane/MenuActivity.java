package com.example.alomrane;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ramotion.circlemenu.CircleMenuView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final CircleMenuView menu = findViewById(R.id.circle_menu);
        menu.setScaleX(1.5f); // Augmenter l'échelle sur l'axe X par 1.5
        menu.setScaleY(1.5f); // Augmenter l'échelle sur l'axe Y par 1.5

        // Écouter les événements de menu
        menu.setEventListener(new CircleMenuView.EventListener(){
            @Override
            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
                Log.d("D","onMenuOpenAnimationStart");

            }

            @Override
            public void onMenuOpenAnimationEnd(@NonNull CircleMenuView view) {
                Log.d("D","onMenuOpenAnimationEnd");


            }

            @Override
            public void onMenuCloseAnimationStart(@NonNull CircleMenuView view) {
                Log.d("D","onMenuCloseAnimationStart");

            }

            @Override
            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView view) {
                Log.d("D","onMenuCloseAnimationEnd");
            }

            @Override
            public void onButtonClickAnimationStart(@NonNull CircleMenuView view, int index) {
                Log.d("D", "onButtonClickAnimationStart | index: " + index);

                switch (index) {
                    case 0: // Bouton Home
                        // Ajouter une pause de 1 seconde avant de démarrer l'activité
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Démarrer l'activité souhaitée avec une animation personnalisée
                                Intent intent = new Intent(MenuActivity.this, MapsActivity1.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }, 900); // 1000 millisecondes = 1 seconde
                        break;
                    case 1: // Bouton 2
                        // Ajouter une pause de 1 seconde avant de démarrer l'activité
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Démarrer l'activité souhaitée avec une animation personnalisée
                                Intent intent2 = new Intent(MenuActivity.this, CasablancaActivity.class);
                                startActivity(intent2);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }, 900); // 1000 millisecondes = 1 seconde
                        break;
                    // Ajouter d'autres cas pour les autres boutons si nécessaire
                }
            }


            @Override
            public void onButtonLongClickAnimationEnd(@NonNull CircleMenuView view, int index) {
                Log.d("D","onButtonLongClickAnimationEnd | index: " + index);
            }

            @Override
            public boolean onButtonLongClick(@NonNull CircleMenuView view, int buttonIndex) {
                Log.d("D","onButtonLongClick | index: " + buttonIndex);
                return true;
            }

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
                Log.d("D","onButtonClickAnimationEnd | index: " + buttonIndex);
            }

            @Override
            public void onButtonLongClickAnimationStart(@NonNull CircleMenuView view, int buttonIndex) {
                Log.d("D","onButtonLongClickAnimationStart | index: " + buttonIndex);
            }
        });

    }
}
