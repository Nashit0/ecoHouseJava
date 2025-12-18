package com.example.ecohouse.viewmodel;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.ecohouse.R;
import com.example.ecohouse.model.BasicProblem;
import com.example.ecohouse.model.EcoProblem ;
import com.example.ecohouse.model.FaucetProblem;

import static com.example.ecohouse.utils.ProblemIds.*;

public class GameViewModel extends ViewModel {

    // Gestion de l'affichage pour l'état du jeu
    private MutableLiveData<Float> urgencyGauge = new MutableLiveData<>(0f);
    private MutableLiveData<Integer> activeProblemsCount = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> isGameOver = new MutableLiveData<>(false);
    private MutableLiveData<List<EcoProblem>> problemsUpdate = new MutableLiveData<>();

    // Liste de tous les problèmes possibles dans la maison
    private List<EcoProblem> allProblems = new ArrayList<>();

    // Paramètres pour progressivement calibrer la difficulté
    private long startTime;
    private Handler gameHandler = new Handler();
    private float difficultyMultiplier = 1.0f;

    public GameViewModel() {
        startTime = System.currentTimeMillis(); // debut d'une partie
        setupProblems(); // mise en place des problemes
        startGameLoop(); // demarrage de la boucle du jeu
    }

    private void setupProblems() {
        allProblems.add(new FaucetProblem( FAUCET_KITCHEN, "Robinet cuisine", R.drawable.faucet_kitchen_1, R.drawable.faucet_kitchen_0));
        allProblems.add(new FaucetProblem(FAUCET_SDB, "Robinet salle de bain", R.drawable.faucet_sdb_1, R.drawable.faucet_sdb_0));
        allProblems.add(new FaucetProblem( FAUCET_BATH, "Robinet baignoire" , R.drawable.bath_1 , R.drawable.bath_0 )) ;
        allProblems.add(new BasicProblem(SMALL_LIGHT_1 , "Petite lampe 1" , R.drawable.little_lamp_1 , R.drawable.little_lamp_0)) ;
        allProblems.add(new BasicProblem(SMALL_LIGHT_2 , "Petite lampe 2" , R.drawable.little_lamp_1 , R.drawable.little_lamp_0)) ;
        allProblems.add(new BasicProblem(LARGE_LIGHT , "Grande lampe" , R.drawable.l_lamp_1 , R.drawable.l_lamp_0)) ;
        allProblems.add(new BasicProblem(FIREPLACE , "Cheminée" , R.drawable.cheminee_1 , R.drawable.cheminee_0)) ;
        allProblems.add(new BasicProblem(STOVE , "Four" , R.drawable.stove_on , R.drawable.stove_off)) ;
        allProblems.add(new BasicProblem(FRIDGE , "Réfrégirateur" , R.drawable.fridge_open , R.drawable.fridge_closed)) ;

        // Ajoute les autres ici (Lampes, Cheminée, etc.)
    }

    private void startGameLoop() {
        // Boucle de spawn
        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Boolean.TRUE.equals(isGameOver.getValue())) return;

                updateDifficulty();
                trySpawnProblem();

                // Plus le temps passe, plus le délai diminue
                long nextSpawn = (long) (3000 / difficultyMultiplier);
                gameHandler.postDelayed(this, Math.max(nextSpawn, 800));
            }
        }, 2000);

        // Boucle de jauge (Toutes les 100ms pour de la fluidité)
        gameHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUrgency();
                gameHandler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void updateDifficulty() {
        long secondsElapsed = (System.currentTimeMillis() - startTime) / 1000;
        difficultyMultiplier = 1.0f + (secondsElapsed / 30.0f); // Augmente toutes les 30 sec
    }

    private void trySpawnProblem() {
        // On cherche les problèmes inactifs
        List<EcoProblem> inactive = new ArrayList<>();
        for(EcoProblem p : allProblems) if(!p.isActive()) inactive.add(p);

        if(!inactive.isEmpty()) {
            inactive.get(new Random().nextInt(inactive.size())).spawn();
            updateCount();
        }
    }

    private void updateUrgency() {
        float currentImpact = 0;
        for(EcoProblem p : allProblems) {
            if(p.isActive()) currentImpact += p.getUrgencyImpact();
        }

        float currentVal = (urgencyGauge.getValue() != null) ? urgencyGauge.getValue() : 0f;
        float newValue = currentVal + (currentImpact * 0.1f) * difficultyMultiplier;
        if (newValue >= 100f) {
            newValue = 100f;
            isGameOver.setValue(true);
        }
        urgencyGauge.setValue(newValue);
    }



    public void updateProblemInput(String problemId, Object... input) {
        for (EcoProblem p : allProblems) {
            if (p.getId().equals(problemId)) {
                p.handleInput(input);
                updateCount(); // On recalcule combien de problèmes restent
                break;
            }
        }
    }

    private void updateCount() {
        int count = 0;
        for(EcoProblem p : allProblems) if(p.isActive()) count++;
        activeProblemsCount.setValue(count);
        problemsUpdate.setValue(allProblems);
    }

    public EcoProblem getProblemById(String id) {
        for (EcoProblem p : allProblems) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public LiveData<Float> getUrgency() { return urgencyGauge; }
    public LiveData<Integer> getActiveCount() { return activeProblemsCount; }
    public LiveData<List<EcoProblem>> getProblemsUpdate() { return problemsUpdate; }
    public LiveData<Boolean> getIsGameOver() {return isGameOver ; }



    /*
    public LiveData<Boolean> getIsFireplaceOn() { return isFireplaceOn; }


    public LiveData<Integer> getStoveState() { return StoveState; }
    public LiveData<Integer> getFridgeState() { return FridgeState; }


    public void updateTemperature(int currentHeightPx, int maxHeightPx) {
        boolean shouldBeOn = currentHeightPx > (maxHeightPx / 2);

        // On ne met à jour que si l'état change pour éviter des calculs inutiles
        if (isFireplaceOn.getValue() != null && isFireplaceOn.getValue() != shouldBeOn) {
            isFireplaceOn.setValue(shouldBeOn);
        }
    }



    public void handleFour() {
        if(StoveState.getValue() != null && StoveState.getValue() == 0){
            StoveState.setValue(1);


        }else{
            StoveState.setValue(0);
            Log.d("TEST_projet", "handleFour");
            Integer current = badPoints.getValue();
            int valueB = current != null ? current : 0;
            badPoints.setValue(valueB - 30);// MAX -> 324
            //enlever l'interdiction de selectionner le problème dans le code aléatoire
            //for (int i = 0; i < forbiddenRandomNumber.size(); i++) {
            //    if(forbiddenRandomNumber.get(i) == 3) {
            //        Log.d("TEST_projet", "supprime 3");
//
            //        forbiddenRandomNumber.remove(i);
            //    }
            //}
        }
    }

    public void handleFridge() {
        if(FridgeState.getValue() != null && FridgeState.getValue() == 0){
            FridgeState.setValue(1);
        }else{
            FridgeState.setValue(0);
            Log.d("TEST_projet", "handleFridge");
            Integer current = badPoints.getValue();
            int valueB = current != null ? current : 0;
            badPoints.setValue(valueB - 30);// MAX -> 324
            //enlever l'interdiction de selectionner le problème dans le code aléatoire
            //for (int i = 0; i < forbiddenRandomNumber.size(); i++) {
            //    if(forbiddenRandomNumber.get(i) == 4) {
            //        Log.d("TEST_projet", "supprime 4");
//
            //        forbiddenRandomNumber.remove(i);
            //    }
            //}
        }
    }

     */


}

