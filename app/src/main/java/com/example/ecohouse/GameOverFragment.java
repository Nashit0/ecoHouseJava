package com.example.ecohouse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class GameOverFragment extends Fragment {


    public GameOverFragment() {

    }



    //public static GameOverFragment newInstance() {
    //    GameOverFragment fragment = new GameOverFragment();
    //    Bundle args = new Bundle();
//
    //    fragment.setArguments(args);
    //    return fragment;
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateHighScores();

        //On récupère les balises textes pour pouvoir les modifier

        //SI j'ai le temps -> faire un seul texte qui affiche dynamiquement
        //Seulement les high score si le score est dépasser
        //Sinon afficher les 4 elements
        TextView scoreText = view.findViewById(R.id.text_score);
        scoreText.setText(""+SecondFragment.getScore());

        TextView highScoreText = view.findViewById(R.id.text_highScore);
        highScoreText.setText(""+SecondFragment.getHighScore());

        TextView timeText = view.findViewById(R.id.text_time);
        timeText.setText(SecondFragment.getSeconds()+" secondes");

        TextView bestTimeText = view.findViewById(R.id.text_bestTime);
        bestTimeText.setText(SecondFragment.getBestTime()+" secondes");


        //Boutons clickable
        ImageButton restartButton = view.findViewById(R.id.button_replay);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(GameOverFragment.this)
                        .navigate(R.id.action_GameOverFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gameover, container, false);
    }

    public void updateHighScores() {
        if (SecondFragment.getScore() > SecondFragment.getHighScore()) {
            SecondFragment.setHighScore(SecondFragment.getScore());
            Log.d("TEST_PROJET", "score updated : " +SecondFragment.getHighScore());
        }
        if (SecondFragment.getSeconds() > SecondFragment.getBestTime()) {
            SecondFragment.setBestTime(SecondFragment.getSeconds());
            Log.d("TEST_PROJET", "best time updated : " +SecondFragment.getBestTime());

        }
    }
}