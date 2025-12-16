package com.example.ecohouse.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.example.ecohouse.R;
import com.example.ecohouse.databinding.FragmentGameBinding;
import com.example.ecohouse.viewmodel.GameViewModel;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding ;
    private GameViewModel viewModel;

    private final int[] bedroomDrawables = {
            R.drawable.bedroom_0,
            R.drawable.bedroom_1,
            R.drawable.bedroom_2,
            R.drawable.bedroom_3
    };

    private final int[] valveDrawables = {
            R.drawable.bathroom_0,
            R.drawable.bathroom_1
    };

    // LE ROBINET
    private float angleRotation = 0f;
    private float angleInitial = 0f;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentGameBinding.inflate(inflater, container, false);
       viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LES OBSERVEURS (la vue qui change lorsque le viewmodel change)
        viewModel.getGameStarted().observe(getViewLifecycleOwner(), started -> {
            if (started) {
                binding.tutorialScreen.setVisibility(View.GONE);
                binding.gameElements.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getLightLevel().observe(getViewLifecycleOwner(), level -> {
            binding.lightOverlay.setImageResource(bedroomDrawables[level]);
            binding.lightSwitch3.setSelected(level <= 2);
            binding.lightSwitch2.setSelected(level <= 1);
            binding.lightSwitch1.setSelected(level <= 0);
        });

        viewModel.getValveState().observe(getViewLifecycleOwner(), state -> {
            binding.bathroomOverlay.setImageResource(valveDrawables[state]);
        });

        binding.playButton.setOnClickListener(v -> viewModel.startGame());

        binding.lightSwitch3.setOnClickListener(v -> viewModel.processLightSwitch(3));
        binding.lightSwitch2.setOnClickListener(v -> viewModel.processLightSwitch(2));
        binding.lightSwitch1.setOnClickListener(v -> viewModel.processLightSwitch(1));

        setupRobinetTouchListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRobinetTouchListener() {
        binding.robinet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // calcul du centre du robinet
                float centerX = view.getWidth() / 2f;
                float centerY = view.getHeight() / 2f;

                // calcul de l'emplacement du toucher par rapport au centre du robinet
                float x = event.getX() - centerX;
                float y = event.getY() - centerY;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        angleInitial = (float) Math.toDegrees(Math.atan2(y, x));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float nouvelAngle = (float) Math.toDegrees(Math.atan2(y, x));

                        float deltaAngle = nouvelAngle - angleInitial;
                        angleRotation += deltaAngle;

                        view.setRotation(angleRotation);

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
                            viewModel.closeValve(); // On notifie le succès !
                        }
                        break;
                }
                return true; // return true obligatoire pour indiquer qu'on a géré l'événement OnTouch
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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