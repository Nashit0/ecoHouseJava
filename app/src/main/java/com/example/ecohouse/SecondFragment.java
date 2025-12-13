package com.example.ecohouse;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ecohouse.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {
    private static Integer badPoints;
    private final Integer maxBadPoints = 1200;
    private static Integer score;
    private static Integer highScore = 0;
    private static FragmentSecondBinding binding;

    private static int secondsElapsed = 0;
    private static int bestTime = 0;
    private Handler handler = new Handler();
    private Runnable timerRunnable;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        badPoints = 900;
        score = 0;
        secondsElapsed = 0;
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TIMER
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                secondsElapsed++;

                badPoints += 140;
                Log.d("TIMER", "Secondes écoulées : " + secondsElapsed);
                updateProgBarDisplay();
                if (badPoints > maxBadPoints) {
                    Log.d("TEST_projet", "Echec");
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_GameOverFragment);
                    //NOTE POUR MOI -> changer ^ pour que ça se connect bien au gameover
                }

                // reprogrammer dans 1 seconde
                handler.postDelayed(this, 1000);
            }
        };

        // Lancer le timer
        handler.postDelayed(timerRunnable, 1000);

        binding.buttonSecond.setOnClickListener(v -> {

                badPoints -= 30;
                score += 10;
                Log.d("TEST_projet","test : "+badPoints);
                updateProgBarDisplay();

        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(timerRunnable); // évite fuites mémoire
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updateProgBarDisplay() {
        View rectangleView = binding.rectangleView;
        int parentWidth = ((View) rectangleView.getParent()).getWidth();
        Log.d("TEST_projet", "Taille écran : "+ parentWidth);

        ViewGroup.LayoutParams params = rectangleView.getLayoutParams();
        params.width = (badPoints * parentWidth) / maxBadPoints;
        params.height = 20;
        rectangleView.setLayoutParams(params);
    }



    //GETTER & SETTER

    public static Integer getScore() {
        return score;
    }
    public static void setScore(Integer newScore) {
        score = newScore;
    }

    public static Integer getHighScore() {
        return highScore;
    }
    public static void setHighScore(Integer newScore) {
        highScore = newScore;
    }

    public static Integer getSeconds() {
        return secondsElapsed;
    }

    public static void setSeconds(Integer newS) {
        secondsElapsed = newS;
    }
    public static Integer getBestTime() {
        return bestTime;
    }
    public static void setBestTime(Integer newS) {
        bestTime = newS;
    }
}
