package com.example.ecohouse.model;

public class BasicProblem extends EcoProblem{
    private int imgOn, imgOff;

    public BasicProblem(String id , String name, int imgOn, int imgOff) {
        super(name, id , 0.5f); // Un robinet qui fuit augmente la jauge de 1.5 par seconde
        this.imgOn = imgOn;
        this.imgOff = imgOff;
    }

    @Override
    public void handleInput(Object... args) {
        if (this.isActive) {
            resolve();
        } else {
            spawn();
        }
    }

    @Override
    public int getActiveDrawable() { return imgOn; }

    @Override
    public int getInactiveDrawable() { return imgOff; }
}
