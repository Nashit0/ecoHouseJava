package com.example.ecohouse;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ecohouse.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {
    public Integer badPoints = 500;
    public final Integer maxBadPoints = 1200;
    public Integer score = 20;
    private FragmentSecondBinding binding;



    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(v -> {
            if (badPoints < maxBadPoints) {
                badPoints+= 300;
                Log.d("TEST_projet","test : "+badPoints);
                updateProgBar();
            }
            else {
                Log.d("TEST_projet", "limite atteinte");
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
                }
            }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updateProgBar() {
        View rectangleView = binding.rectangleView;
        int parentWidth = ((View) rectangleView.getParent()).getWidth();
        Log.d("TEST_projet", "Taille écran : "+ parentWidth);

        ViewGroup.LayoutParams params = rectangleView.getLayoutParams();
        params.width =(badPoints * parentWidth) / maxBadPoints;
        // largeur en pixels
        params.height = 20;  // hauteur en pixels
        rectangleView.setLayoutParams(params);
    }
}
