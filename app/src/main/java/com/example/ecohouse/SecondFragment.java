package com.example.ecohouse;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ecohouse.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {
    //Variables jeu
    private static Integer badPoints;
    private final Integer maxBadPoints = 1200;
    private static Integer score;
    private static Integer highScore = 0;
    private static int secondsElapsed = 0;
    private static int bestTime = 0;
    private static int secondsBeforeEvent = 3;
    private static int actualSecondsBeforeEvent = secondsBeforeEvent;
    //Problèmes :
    // 3 niveaux de danger (1 à régler ,2 urgent, 3 extremement urgent)
    // 0 étant l'absence de problème
    //chaques problemes sont respectivements
    // réferencer par un id dans le code
    private static int problemLight = 0; //id 1
        private static int lightUnsolvedSeconds=0;
    private static int problemWater = 0; //id 2
        private static int waterUnsolvedSeconds=0;
    private static int problemTrash = 0;//id 3
        private static int trashUnsolvedSeconds=0;
    private static int problemHeating = 0;//id 4
        private static int heatUnsolvedSeconds=0;

    private final List<Integer> forbiddenRandomNumber = new ArrayList<>();



    //Autres variables
    private static FragmentSecondBinding binding;


    private Handler handler = new Handler();
    private Runnable timerRunnable;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        problemLight = 0;
        problemWater = 0;
        problemTrash = 0;
        problemHeating = 0;
        badPoints = 0;
        score = 0;
        secondsElapsed = 0;
        secondsBeforeEvent = 3;
        actualSecondsBeforeEvent = secondsBeforeEvent;
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
                gameEventManager();
                badPoints += (problemLight+problemHeating+problemTrash+problemWater)*10;
                Log.d("TIMER", "Secondes écoulées : " + secondsElapsed +
                        "PROBLEMS : "+
                                "LIGHT lvl : "+problemLight
                        +"WATER lvl : "+problemWater
                        +"TRASH lvl : "+problemTrash
                        +"HEATING lvl : "+problemHeating
                );

                updateProgBarDisplay();
                updateProblemsIntensity();
                updateTestDisplay(view);
                if (badPoints > maxBadPoints) {
                    Log.d("TEST_projet", "Echec");
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_GameOverFragment);
                }

                // reprogrammer dans 1 seconde
                handler.postDelayed(this, 1000);
            }
        };

        // Lancer le timer
        handler.postDelayed(timerRunnable, 20);

        binding.buttonSecond.setOnClickListener(v -> {

                resolveLightProblem();
                resolveWaterProblem();
                resolveTrashProblem();
                resolveHeatProblem();

                //Log.d("TEST_projet","test : "+badPoints);
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

    public void updateTestDisplay(View view) {
        TextView secondText = view.findViewById(R.id.text_secondsAndScore);
        secondText.setText(""+secondsElapsed);
        /// /// NE PAS COPIER COLLER DANS GAME FRAGMENT !!!
        TextView prblm4Text = view.findViewById(R.id.text_prblmHeat);
        prblm4Text.setText("HEAT "+problemHeating);
        TextView prblm3Text = view.findViewById(R.id.text_prblmTrash);
        prblm3Text.setText("TRASH "+problemTrash);
        TextView prblm2Text = view.findViewById(R.id.text_prblmWater);
        prblm2Text.setText("WATER "+problemWater);
        TextView prblm1Text = view.findViewById(R.id.text_prblmLight);
        prblm1Text.setText("LIGHT "+problemLight);

        /// /// ^^NE PAS COPIER COLLER DANS GAME FRAGMENT^^ !!!
    }

    // Fonctions code jeu
    public void updateProgBarDisplay() {
        View rectangleView = binding.rectangleView;
        int parentWidth = ((View) rectangleView.getParent()).getWidth();
        //Log.d("TEST_projet", "Taille écran : "+ parentWidth);

        ViewGroup.LayoutParams params = rectangleView.getLayoutParams();
        params.width = (badPoints * parentWidth) / maxBadPoints;
        params.height = 20;
        rectangleView.setLayoutParams(params);
    }
    public void gameEventManager() {
        //s'active à chaque seconde
        //Generer un problème
        actualSecondsBeforeEvent--;
        Log.d("TEST_projet", "Secondes avant event : "+ actualSecondsBeforeEvent + " Secondes max : " + secondsBeforeEvent);
        if (actualSecondsBeforeEvent == 0) {
            actualSecondsBeforeEvent = secondsBeforeEvent;
            createGameProblem();
        }

        //Changer difficulté au fur et a mesure du temps
        if (secondsElapsed > 6 && secondsElapsed < 16) secondsBeforeEvent = 2;
        if (secondsElapsed >= 16) secondsBeforeEvent = 1;
    }

    public void createGameProblem() {
        int randomProblem = randomNumber1To4();
        if(problemLight==0 && randomProblem==1){problemLight = 1;}
        if(problemWater==0 && randomProblem==2) {problemWater = 1;}
        if(problemTrash==0 && randomProblem==3){problemTrash = 1;}
        if(problemHeating==0 && randomProblem==4){problemHeating = 1;}
        Log.d("TEST_projet", "Problem "+randomProblem+" created successfully ! ");

    }

    public Integer randomNumber1To4() {
        if (forbiddenRandomNumber.size() == 4) {
            return 0;
        }
        int randomPrblm;
        do {
            randomPrblm = (int) (Math.random() * 4) + 1;
        } while (forbiddenRandomNumber.contains(randomPrblm));
        forbiddenRandomNumber.add(randomPrblm);
        return randomPrblm;
    }

    public void updateProblemsIntensity() {
        //LIGHT
        if(problemLight>0){lightUnsolvedSeconds++;}//si probleme alors compter le temps
        if (lightUnsolvedSeconds == 4) {problemLight=2;}//Augmenter intensité 2 prbl au bout de 4s
        if (lightUnsolvedSeconds == 10) {problemLight=3;}//Augmenter intensité 3 prbl au bout de 10s

        //WATER
        if(problemWater>0){waterUnsolvedSeconds++;}
        if (waterUnsolvedSeconds == 4) {problemWater=2;}
        if (waterUnsolvedSeconds == 10) {problemWater=3;}

        //TRASH
        if(problemTrash>0){trashUnsolvedSeconds++;}
        if (trashUnsolvedSeconds == 4) {problemTrash=2;}
        if (trashUnsolvedSeconds == 10) {problemTrash=3;}

        //HEAT
        if(problemHeating>0){heatUnsolvedSeconds++;}
        if (heatUnsolvedSeconds == 4) {problemHeating=2;}
        if (heatUnsolvedSeconds == 10) {problemHeating=3;}
    }

    public void resolveLightProblem() {
        if (problemLight >0) {
            lightUnsolvedSeconds = 0;
            problemLight = 0;
            //enlever l'interdiction de selectionner le problème dans le code aléatoire
            for (int i = 0; i < forbiddenRandomNumber.size(); i++) {
                if(forbiddenRandomNumber.get(i) == 1) {
                    forbiddenRandomNumber.remove(i);
                }
            }
            lightUnsolvedSeconds = 0;
            Log.d("TEST_projet","LIGHT PROBLEME REMOVED" + forbiddenRandomNumber);
            badPoints -= 20;
            //Score elever si problème regler rapidement
            if(problemLight == 1)score += 30;
            if(problemLight == 2)score += 20;
            if(problemLight == 3)score += 10;
        }
        else {
            badPoints+= 10;
            Log.d("TEST_projet","PENALITE ! ");
        }
    }
    public void resolveWaterProblem() {
        if (problemWater >0) {
            waterUnsolvedSeconds = 0;
            problemWater = 0;
            //enlever l'interdiction de selectionner le problème dans le code aléatoire
            for (int i = 0; i < forbiddenRandomNumber.size(); i++) {
                if(forbiddenRandomNumber.get(i) == 2) {
                    forbiddenRandomNumber.remove(i);
                }
            }
            waterUnsolvedSeconds = 0;
            Log.d("TEST_projet","WATER PROBLEME REMOVED" + forbiddenRandomNumber);
            badPoints -= 20;
            //Score elever si problème regler rapidement
            if(problemWater == 1)score += 30;
            if(problemWater == 2)score += 20;
            if(problemWater == 3)score += 10;
        }
        else {
            badPoints+= 10;
            Log.d("TEST_projet","PENALITE ! ");
        }
    }
    public void resolveTrashProblem() {
        if (problemTrash >0) {
            trashUnsolvedSeconds = 0;
            problemTrash = 0;
            //enlever l'interdiction de selectionner le problème dans le code aléatoire
            for (int i = 0; i < forbiddenRandomNumber.size(); i++) {
                if(forbiddenRandomNumber.get(i) == 3) {
                    forbiddenRandomNumber.remove(i);
                }
            }
            trashUnsolvedSeconds = 0;
            Log.d("TEST_projet","TRASH PROBLEME REMOVED" + forbiddenRandomNumber);
            badPoints -= 20;
            //Score elever si problème regler rapidement
            if(problemTrash == 1)score += 30;
            if(problemTrash == 2)score += 20;
            if(problemTrash == 3)score += 10;
        }
        else {
            badPoints+= 10;
            Log.d("TEST_projet","PENALITE ! ");
        }
    }

    public void resolveHeatProblem() {
        if (problemHeating >0) {
            heatUnsolvedSeconds = 0;
            problemHeating = 0;
            //enlever l'interdiction de selectionner le problème dans le code aléatoire
            for (int i = 0; i < forbiddenRandomNumber.size(); i++) {
                if(forbiddenRandomNumber.get(i) == 4) {
                    forbiddenRandomNumber.remove(i);
                }
                badPoints -= 20;
                //Score elever si problème regler rapidement
                if(problemHeating == 1)score += 30;
                if(problemHeating == 2)score += 20;
                if(problemHeating == 3)score += 10;
            }
            heatUnsolvedSeconds = 0;
            Log.d("TEST_projet","HEAT PROBLEME REMOVED" + forbiddenRandomNumber);
        }
        else {
            badPoints+= 10;
            Log.d("TEST_projet","PENALITE ! ");
        }
    }
    /// /// GETTER & SETTER

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
