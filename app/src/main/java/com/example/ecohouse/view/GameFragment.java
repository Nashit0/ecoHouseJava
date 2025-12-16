package com.example.ecohouse;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.example.ecohouse.databinding.FragmentGameBinding;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding ;

    // CONCERNANT LES LUMIERES
    private int currentLightLevel = 3;
    private final int[] bedroomDrawables = {
            R.drawable.bedroom_0,
            R.drawable.bedroom_1,
            R.drawable.bedroom_2,
            R.drawable.bedroom_3
    };

    // LE ROBINET QUI FUIT:
    private int isValveOpen = 0;
    private float angleRotation = 0f;
    private float angleInitial = 0f;
    private final int[] valveDrawables = {
            R.drawable.bathroom_0,
            R.drawable.bathroom_1
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentGameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.playButton.setOnClickListener(v->{
                    binding.tutorialScreen.setVisibility(View.GONE) ;
                    binding.gameElements.setVisibility(View.VISIBLE) ;
                });

        binding.lightOverlay.setImageResource(bedroomDrawables[currentLightLevel]);
        binding.lightSwitch3.setOnClickListener(v -> processLightSwitch(3));
        binding.lightSwitch2.setOnClickListener(v -> processLightSwitch(2));
        binding.lightSwitch1.setOnClickListener(v -> processLightSwitch(1));

        // remplacer ici par genre isValveOpen ?
        binding.bathroomOverlay.setImageResource(valveDrawables[isValveOpen]);

        ImageView faucet = binding.robinet ;

        faucet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // 1. Coordonnées du centre du robinet
                float centerX = view.getWidth() / 2f;
                float centerY = view.getHeight() / 2f;

                // 2. Coordonnées du toucher par rapport au centre
                float x = event.getX() - centerX;
                float y = event.getY() - centerY;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Calcul de l'angle de départ pour éviter un "saut" de l'image
                        angleInitial = (float) Math.toDegrees(Math.atan2(y, x));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // Calcul du nouvel angle
                        float nouvelAngle = (float) Math.toDegrees(Math.atan2(y, x));

                        // Calcul de la différence et mise à jour de la rotation de l'image
                        float deltaAngle = nouvelAngle - angleInitial;
                        angleRotation += deltaAngle;

                        view.setRotation(angleRotation); // CSS transform: rotate(...)

                        // Mise à jour pour le prochain mouvement
                        angleInitial = nouvelAngle;
                        break ;

                    case MotionEvent.ACTION_UP:
                        angleRotation = Math.round(angleRotation / 90f) * 90f;
                        view.animate()
                                .rotation(angleRotation)
                                .setDuration(200)
                                .setInterpolator(new OvershootInterpolator(2.0f))
                                .start();

                        if (Math.abs(angleRotation) >= 360) {
                            // stop leak ?
                        }
                        break;
                }
                return true; // Indique qu'on a géré l'événement tactile
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void processLightSwitch(int targetLevel) {

        if (currentLightLevel <= 0) {
            // erreur ou augmentation de la jauge idk
            return;
        }

        if (currentLightLevel != targetLevel) {
            // Log.d("LightGame", "Ordre incorrect. Veuillez cliquer sur le niveau " + currentLightLevel + ".");
            return;
        }

        currentLightLevel--;

        binding.lightOverlay.setImageResource(bedroomDrawables[currentLightLevel]);

        switch (currentLightLevel) {
            case 2:
                binding.lightSwitch3.setSelected(true);
                break;
            case 1:
                binding.lightSwitch2.setSelected(true);
                break;
            case 0:
                binding.lightSwitch1.setSelected(true);
                break;
        }
    }

    /* pour la cheminee à ajuster
    private void toggleLight() {
        isLightOn = !isLightOn;
        if (isLightOn) {
            binding.lightOverlay.setVisibility(View.VISIBLE); // style.visibility = 'hidden'
            binding.lightSwitch.setSelected(true);
            binding.lightOverlay.setImageResource(R.drawable.livingroom_1);
        } else {
            // Allumer
            binding.lightOverlay.setVisibility(View.INVISIBLE); // style.visibility = 'visible'
            binding.lightSwitch.setSelected(false);
            binding.lightOverlay.setImageResource(R.drawable.livingroom_0);

        }
    }
     */

}