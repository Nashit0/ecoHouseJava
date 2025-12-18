package com.example.ecohouse.model;

public abstract class EcoProblem {
    protected String name;
    protected String id ;
    protected boolean isActive = false;
    protected float urgencyImpact; // Combien il fait monter la jauge par seconde

    public EcoProblem(String name, String id ,  float urgencyImpact) {
        this.name = name;
        this.id = id ;
        this.urgencyImpact = urgencyImpact;
    }

    public boolean isActive() { return isActive; }

    // Méthode pour déclencher le problème
    public void spawn() { this.isActive = true; }

    // Méthode pour résoudre le problème
    public void resolve() { this.isActive = false; }

    // Chaque problème dira au ViewModel quel ID de ressource afficher
    public abstract int getActiveDrawable();
    public abstract int getInactiveDrawable();

    public String getName(){
        return name ;
    }

    public String getId(){
        return id ;
    }

    // Cette méthode sera surchargée par les enfants (Robinet, Thermo, etc.)
    public abstract void handleInput(Object... args);


    public float getUrgencyImpact(){
        return urgencyImpact ;
    }
}
