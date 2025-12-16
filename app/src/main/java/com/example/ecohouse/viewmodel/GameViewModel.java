package com.example.ecohouse.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<Integer> lightLevel = new MutableLiveData<>(3);
    private final MutableLiveData<Integer> valveState = new MutableLiveData<>(1); // 1 = fuit, 0 = fermé
    private final MutableLiveData<Boolean> gameStarted = new MutableLiveData<>(false);

    // Getters pour le Fragment
    public LiveData<Integer> getLightLevel() { return lightLevel; }
    public LiveData<Integer> getValveState() { return valveState; }
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

    public void closeValve() {
        valveState.setValue(0);
    }

}
