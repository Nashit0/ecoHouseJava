package com.example.ecohouse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecohouse.databinding.FragmentFirstBinding;
import com.example.ecohouse.databinding.FragmentGameBinding;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding ;
    private boolean isLightOn = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentGameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.playButton.setOnClickListener(v->{
                    binding.tutorialScreen.setVisibility(View.GONE) ;
                    binding.gameElements.setVisibility(View.VISIBLE) ;
                });

        binding.lightSwitch.setSelected(isLightOn);
        binding.lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLight();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void toggleLight() {
        isLightOn = !isLightOn;
        if (isLightOn) {
            binding.lightOverlay.setVisibility(View.VISIBLE); // style.visibility = 'hidden'
            binding.lightSwitch.setSelected(true);
        } else {
            // Allumer
            binding.lightOverlay.setVisibility(View.INVISIBLE); // style.visibility = 'visible'
            binding.lightSwitch.setSelected(false);
        }
    }
}