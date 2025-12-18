package com.example.ecohouse.model;

public class FaucetProblem extends EcoProblem {
    private int imgOn, imgOff;

    public FaucetProblem(String id , String name, int imgOn, int imgOff) {
        super(name, id , 1.5f); // Un robinet qui fuit augmente la jauge de 1.5 par seconde
        this.imgOn = imgOn;
        this.imgOff = imgOff;
    }

    @Override
    public void handleInput(Object... args) {

        // On récupère l'angle passé depuis le Fragment
        float angle = (float) args[0];

        // Ta logique : Si l'utilisateur tourne vers le "Fermé" (-90)
        if (angle < -90f && this.isActive) {
            resolve();
        }
        else if(angle>90f && !this.isActive){
            spawn();
        }
    }

    @Override
    public int getActiveDrawable() { return imgOn; }

    @Override
    public int getInactiveDrawable() { return imgOff; }
}
