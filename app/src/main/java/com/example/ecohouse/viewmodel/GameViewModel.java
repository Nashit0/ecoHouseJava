package com.example.ecohouse.viewmodel;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecohouse.model.Problem;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends ViewModel {
    private MutableLiveData<Boolean> light1 = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> light2 = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> light3 = new MutableLiveData<>(false);
    public LiveData<Boolean> getLight1Status() { return light1; }
    public LiveData<Boolean> getLight2Status() { return light2; }
    public LiveData<Boolean> getLight3Status() { return light3; }
    private final MutableLiveData<Integer> bathState = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> kitchenSiphonState = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> sdbSiphonState = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> StoveState = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> FridgeState = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> gameStarted = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> badPoints = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> bathStateProblem = new MutableLiveData<>(1);


    private final Handler gameHandler = new Handler(); // temps qui s'ecoule
    private Runnable loopRunnable;
    //Variables
    private final MutableLiveData<Integer> secondsElapsed = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> secondsElapsedBeforeProblem = new MutableLiveData<>(4);

    private final List<Integer> forbiddenRandomNumber = new ArrayList<>();


    public enum FaucetType {
        BATH, CUISINE, SDB
    }

    // Getters pour le Fragment
    public LiveData<Integer> getKitchenSiphonState() { return kitchenSiphonState; }
    public LiveData<Integer> getsdbSiphonState() { return sdbSiphonState; }
    public LiveData<Integer> getBathState() { return bathState; }
    public LiveData<Integer> getStoveState() { return StoveState; }
    public LiveData<Integer> getFridgeState() { return FridgeState; }
    public LiveData<Boolean> getGameStarted() { return gameStarted; }
    public MutableLiveData<Integer> getSecondsElapsedBeforeProblem() { return secondsElapsedBeforeProblem; }
    public LiveData<Integer> getBadPoints() { return badPoints; }

    public void startGame() {
        Log.d("TEST_projet", "game start");

        gameStarted.setValue(true);
        gameLoop();
    }
    private void gameLoop() {
        loopRunnable = new Runnable() {
            @Override
            public void run() {
                //GameFragment.updateAlertBarDisplay();
                Integer current = badPoints.getValue();
                int valueB = current != null ? current : 0;
                badPoints.setValue(valueB + 10);// MAX -> 324
                if (badPoints.getValue() > 323) {
                 //   int d = Log.d("TEST_projet", "PERDU !!!");
                    //CHARGER GAME OVER
                }
                Integer currentSecondsElapsed = secondsElapsed.getValue();
                int value = currentSecondsElapsed != null ? currentSecondsElapsed : 0;
                secondsElapsed.setValue(value + 1);

                //Permet de créer un problème toutes les "secondsElapsedBeforeProblem" secondes
                if ((secondsElapsed.getValue() % getSecondsElapsedBeforeProblem().getValue()) == 0) {
                    createGameProblem();
                }
                //int d = Log.d("TEST_projet", "Une seconde s'est écoulé "+secondsElapsed.getValue());
                gameHandler.postDelayed(this, 1000); // Récurrence 1s
            }
        };
        gameHandler.postDelayed(loopRunnable, 1000);
    }

    public void toggleLight(int switchNumber) {
        if (switchNumber == 1) {
            light1.setValue(!light1.getValue());
        } else if (switchNumber == 2) {
            light2.setValue(!light2.getValue());
        } else if (switchNumber == 3) {
            light3.setValue(!light3.getValue());
        }
        if (lightLevel.getValue()==0) {
            Integer currentB = badPoints.getValue();
            int valueB = currentB != null ? currentB : 0;
            badPoints.setValue(valueB - 30);// MAX -> 324
            //enlever l'interdiction de selectionner le problème dans le code aléatoire
            //for (int i = 0; i < forbiddenRandomNumber.size(); i++) {
            //    if(forbiddenRandomNumber.get(i) == 1) {
            //        Log.d("TEST_projet", "supprime 1");

            //        forbiddenRandomNumber.remove(i);
            //    }
            //}
        }
    }


    public void updateValvePosition(FaucetType type , float angle) {
        int state = 0 ;
        if (angle < -90f) {
            state = 0;
        } else if (angle > 90f) {
            state = 1 ;
        }else{
            return ;
        }

        switch (type) {
            case BATH:
                bathState.setValue(state);
                Log.d("TEST_projet", "case BATH");
                if(bathStateProblem.getValue()==1) {
                    Integer currentB = badPoints.getValue();
                    int valueB = currentB != null ? currentB : 0;
                    badPoints.setValue(valueB - 30);// MAX -> 324
                    bathStateProblem.setValue(0);
                }


            break;
            case CUISINE:
                kitchenSiphonState.setValue(state);
                Log.d("TEST_projet", "case CUISINE");

                break;
            case SDB:
                sdbSiphonState.setValue(state);
                Log.d("TEST_projet", "case SDB");

                break;
        }

    }

    private void openValve() {
        bathState.setValue(1);
    }

    private void closeValve() {
        bathState.setValue(0);
        Integer current = badPoints.getValue();
        int valueB = current != null ? current : 0;
        badPoints.setValue(valueB - 30);// MAX -> 324
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

    public void createGameProblem() {
        int randomProblem = randomNumber1To4();
        int d = Log.d("TEST_projet", "toutes les 4s "+secondsElapsed.getValue() + "rdm \n"
                +randomProblem + " et puis "+ forbiddenRandomNumber);

        if(randomProblem==1){
            d = Log.d("TEST_projet", "Problem1 avant "+lightLevel.getValue());
            lightLevel.setValue(3);
            d = Log.d("TEST_projet", "Problem1 après "+lightLevel.getValue());
            //NOTE pour moi : enlever lnb interdit !!!
        }
        if( randomProblem==2) {
            d = Log.d("TEST_projet", "Problem2 avant "+bathStateProblem.getValue());
            bathState.setValue(1);
            bathStateProblem.setValue(1);

            d = Log.d("TEST_projet", "Problem2 avant "+bathStateProblem.getValue());
        }
        if(randomProblem==3){
            d = Log.d("TEST_projet", "Problem3 avant "+StoveState.getValue());
            StoveState.setValue(1);
            d = Log.d("TEST_projet", "Problem3 avant "+StoveState.getValue());
        }
        if(randomProblem==4){
            d = Log.d("TEST_projet", "problem 4 avant " + FridgeState.getValue());
            FridgeState.setValue(1);
            d = Log.d("TEST_projet", "problem 4 après " + FridgeState.getValue());
        }

        //d = Log.d("TEST_projet", "problem 4 avant " + kitchenSiphonState.getValue());
        //kitchenSiphonState.setValue(1);
        //d = Log.d("TEST_projet", "problem 4 après " + kitchenSiphonState.getValue());

    }


    public Integer randomNumber1To4() {
        if (forbiddenRandomNumber.size() == 4) {
            return 0;
        }
        int randomPrblm;
        //do {
            randomPrblm = (int) (Math.random() * 4) + 1;
        //} while (forbiddenRandomNumber.contains(randomPrblm));
        //forbiddenRandomNumber.add(randomPrblm);
        return randomPrblm;
    }

}

