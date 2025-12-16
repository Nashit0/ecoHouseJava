package com.example.ecohouse.viewmodel;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<Integer> lightLevel = new MutableLiveData<>(3);
    private final MutableLiveData<Integer> valveState = new MutableLiveData<>(1); // 1 = fuit, 0 = fermé
    private final MutableLiveData<Integer> kitchenState = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> gameStarted = new MutableLiveData<>(false);

    private final Handler gameHandler = new Handler(); // temps qui s'ecoule
    private Runnable waterWasteRunnable;

    // Getters pour le Fragment
    public LiveData<Integer> getLightLevel() { return lightLevel; }
    public LiveData<Integer> getValveState() { return valveState; }
    public LiveData<Integer> getKitchenState() { return kitchenState; }
    public LiveData<Boolean> getGameStarted() { return gameStarted; }

    public void startGame() {
        gameStarted.setValue(true);
    }

    public void processLightSwitch(int clickedLevel) {
        Integer current = lightLevel.getValue();
        if (current != null && current > 0 && current == clickedLevel) {
            lightLevel.setValue(current - 1);
        }
    }


    public void updateValvePosition(float angle) {
        if (angle < -90f) {
            closeValve();
        } else if (angle > 90f) {
            openValve();
        }
    }

    private void openValve() {
        if (valveState.getValue() != null && valveState.getValue() == 0) {
            valveState.setValue(1);
            startGaspillage();
        }
    }

    private void closeValve() {
        valveState.setValue(0);
        gameHandler.removeCallbacks(waterWasteRunnable);
    }

    private void startGaspillage() {
        waterWasteRunnable = new Runnable() {
            @Override
            public void run() {
                if (valveState.getValue() == 1) {
                    /* int currentScore = score.getValue() != null ? score.getValue() : 0;
                    score.setValue(Math.max(0, currentScore - 2)); // Perd 2 pts par seconde */
                    gameHandler.postDelayed(this, 1000); // Récurrence 1s
                }
            }
        };
        gameHandler.postDelayed(waterWasteRunnable, 1000);
    }

    public void handleFour() {
        if(kitchenState.getValue() != null && kitchenState.getValue() == 0){
            kitchenState.setValue(1);
        }else{
            kitchenState.setValue(0);
        }
    }
}
