package com.example.ecohouse.viewmodel;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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


    private final Handler gameHandler = new Handler(); // temps qui s'ecoule
    private Runnable waterWasteRunnable;

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

    public void startGame() {
        gameStarted.setValue(true);
    }

    public void toggleLight(int switchNumber) {
        if (switchNumber == 1) {
            light1.setValue(!light1.getValue());
        } else if (switchNumber == 2) {
            light2.setValue(!light2.getValue());
        } else if (switchNumber == 3) {
            light3.setValue(!light3.getValue());
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
            case BATH: bathState.setValue(state); break;
            case CUISINE: kitchenSiphonState.setValue(state); break;
            case SDB: sdbSiphonState.setValue(state); break;
        }

    }

    private void openValve() {
        bathState.setValue(1);
    }

    private void closeValve() {
        bathState.setValue(0);
    }

    public void handleFour() {
        if(StoveState.getValue() != null && StoveState.getValue() == 0){
            StoveState.setValue(1);
        }else{
            StoveState.setValue(0);
        }
    }

    public void handleFridge() {
        if(FridgeState.getValue() != null && FridgeState.getValue() == 0){
            FridgeState.setValue(1);
        }else{
            FridgeState.setValue(0);
        }
    }
}
