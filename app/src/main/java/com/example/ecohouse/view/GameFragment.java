package com.example.ecohouse.view;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.example.ecohouse.R;
import com.example.ecohouse.databinding.FragmentGameBinding;
import com.example.ecohouse.viewmodel.GameViewModel;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding ;
    private GameViewModel viewModel;

    private final int[] bedroomDrawables = {
            R.drawable.bedroom_0,
            R.drawable.bedroom_1,
            R.drawable.bedroom_2,
            R.drawable.bedroom_3
    };

    private final int[] bathDrawables = {
            R.drawable.bath_0,
            R.drawable.bath_1
    };

    private final int[] stoveDrawables = {
            R.drawable.stove_off,
            R.drawable.stove_on
    };

    private final int[] fridgeDrawables = {
            R.drawable.fridge_closed,
            R.drawable.fridge_open
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       binding = FragmentGameBinding.inflate(inflater, container, false);
       viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // LES OBSERVEURS (la vue qui change lorsque le viewmodel change)
        viewModel.getGameStarted().observe(getViewLifecycleOwner(), started -> {
            if (started) {
                Log.d("TEST_projet", "game indeed started");
                //viewModel.startGame();
                binding.tutorialScreen.setVisibility(View.GONE);
            }
        });
        viewModel.getBadPoints().observe(getViewLifecycleOwner(), points -> {
            updateAlertBarDisplay(points);
        });

        viewModel.getLightLevel().observe(getViewLifecycleOwner(), level -> {
            binding.lightOverlay.setImageResource(bedroomDrawables[level]);
            binding.lightSwitch3.setSelected(level <= 2);
            binding.lightSwitch2.setSelected(level <= 1);
            binding.lightSwitch1.setSelected(level <= 0);
        });

        viewModel.getBathState().observe(getViewLifecycleOwner(), state -> {
            binding.bath.setImageResource(bathDrawables[state]);
        });

        viewModel.getStoveState().observe(getViewLifecycleOwner() , state-> {
            binding.stove.setImageResource(stoveDrawables[state]);
        });

        viewModel.getFridgeState().observe(getViewLifecycleOwner() , state-> {
            binding.fridge.setImageResource(fridgeDrawables[state]);
        });

        binding.playButton.setOnClickListener(v -> viewModel.startGame());

        binding.lightSwitch3.setOnClickListener(v -> viewModel.processLightSwitch(3));
        binding.lightSwitch2.setOnClickListener(v -> viewModel.processLightSwitch(2));
        binding.lightSwitch1.setOnClickListener(v -> viewModel.processLightSwitch(1));

        setupRobinetTouchListener(binding.robinetBath , GameViewModel.FaucetType.BATH);
        setupRobinetTouchListener(binding.robinetCuisine, GameViewModel.FaucetType.CUISINE);
        setupRobinetTouchListener(binding.robinetSalleDeBain, GameViewModel.FaucetType.SDB);
        setupHandTouchListener() ;
        setupTempTouchListener() ;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRobinetTouchListener(ImageView faucet, GameViewModel.FaucetType type) {
        faucet.setOnTouchListener(new View.OnTouchListener() {
            // Variables locales : chaque robinet a les siennes
            private float internalRotation = faucet.getRotation();
            private float lastAngle = 0f;
            private final float DEGREES_THRESHOLD = 3.0f; // Ignore les tremblements sous 3 degrés

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float centerX = view.getWidth() / 2f;
                float centerY = view.getHeight() / 2f;
                float x = event.getX() - centerX;
                float y = event.getY() - centerY;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastAngle = (float) Math.toDegrees(Math.atan2(y, x));
                        // On récupère la rotation actuelle réelle au cas où une animation l'ait bougé
                        internalRotation = view.getRotation();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float currentAngle = (float) Math.toDegrees(Math.atan2(y, x));
                        float deltaAngle = currentAngle - lastAngle;

                        // Correction du passage 180° / -180°
                        if (deltaAngle > 180) deltaAngle -= 360;
                        if (deltaAngle < -180) deltaAngle += 360;

                        // STABILISATION : On ne bouge que si le mouvement est significatif
                        if (Math.abs(deltaAngle) > DEGREES_THRESHOLD) {
                            internalRotation += deltaAngle;

                            // On limite pour éviter des chiffres astronomiques (ex: 450000°)
                            internalRotation %= 360;

                            view.setRotation(internalRotation);
                            lastAngle = currentAngle;

                            // Mise à jour du ViewModel
                            viewModel.updateValvePosition(type, internalRotation);
                            binding.angletest.setText(String.format("%.1f°", internalRotation));
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        // Snap propre à 90 degrés
                        internalRotation = Math.round(view.getRotation() / 90f) * 90f;

                        view.animate()
                                .rotation(internalRotation)
                                .setDuration(250)
                                .setInterpolator(new OvershootInterpolator(1.5f))
                                .start();

                        viewModel.updateValvePosition(type, internalRotation);
                        break;
                }
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupHandTouchListener(){
        binding.handGrab.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY; // le deplacement
            float startX, startY; // les coordonnées de depart pour y retourner

            @Override
            public boolean onTouch(View view , MotionEvent event){
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // initialisation des positions initiales
                        startX = view.getX();
                        startY = view.getY();

                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // deplacement de l'element
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;
                        view.setX(newX);
                        view.setY(newY);
                        if (isViewOverlapping(binding.handGrab, binding.four) || isViewOverlapping(binding.handGrab, binding.fridge) ) {
                            binding.handGrab.setImageResource(R.drawable.hand_active) ;
                        } else {
                            binding.handGrab.setActivated(false);
                            binding.handGrab.setImageResource(R.drawable.hand_default) ;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isViewOverlapping(binding.handGrab, binding.four)) {
                            // ACTION REUSSIE
                            viewModel.handleFour();
                        }else if(isViewOverlapping(binding.handGrab, binding.fridge)){
                            viewModel.handleFridge() ;
                    }
                        binding.handGrab.setImageResource(R.drawable.hand_default) ;
                        view.animate().x(startX).y(startY).start();
                        break;
                }
                return true ;
            }
        }) ;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTempTouchListener() {
        View handle = binding.thermoHandle;
        View jauge = binding.thermoJauge ;
        handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    // Calcul de la position du doigt sur l'écran
                    float rawY = event.getRawY();

                    // Trouver la position du bas de la jauge sur l'écran
                    int[] location = new int[2];
                    jauge.getLocationOnScreen(location);
                    int jaugeBottomY = location[1] + jauge.getHeight();

                    // Calculer la nouvelle hauteur
                    int newHeight = jaugeBottomY - (int) rawY;

                    // Application des limites
                    if (newHeight < toPx(10)) newHeight = toPx(10);
                    if (newHeight > toPx(125)) newHeight = toPx(125);

                    // Mise à jour de la hauteur dans le layout
                    ViewGroup.LayoutParams params = jauge.getLayoutParams();
                    params.height = newHeight;
                    jauge.setLayoutParams(params);
                }
                return true; // Important pour continuer à recevoir les événements de mouvement
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean isViewOverlapping(View hand, View element) {
        int[] handPosition = new int[2];
        int[] elementPosition = new int[2];

        hand.getLocationOnScreen(handPosition);
        element.getLocationOnScreen(elementPosition);

        // Crée deux rectangles pour comparer les positions
        Rect rectFirst = new Rect(handPosition[0], handPosition[1],
                handPosition[0] + hand.getWidth(), handPosition[1] + hand.getHeight());
        Rect rectSecond = new Rect(elementPosition[0], elementPosition[1],
                elementPosition[0] + element.getWidth(), elementPosition[1] + element.getHeight());

        // Vérifie l'intersection entre les deux
        return rectFirst.intersect(rectSecond);
    }

    private int toPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    public void updateAlertBarDisplay(Integer badPoints) {
        View rectangleView = binding.jaugeGaspillage;
        int parentWidth = ((View) rectangleView.getParent()).getWidth();
        //Log.d("TEST_projet", "Taille écran : "+ parentWidth);

        ViewGroup.LayoutParams params = rectangleView.getLayoutParams();
        //params.width = (30 * parentWidth) / 500;
        params.width = (int) (badPoints * getResources().getDisplayMetrics().density);
        if (badPoints < 2) {
            params.width = (int) (1 * getResources().getDisplayMetrics().density);
        }
        else if (badPoints> 323) {
            params.width = (int) (324 * getResources().getDisplayMetrics().density);

        }
        params.height = 20;
        rectangleView.setLayoutParams(params);
    }
    /* pour la cheminee à ajuster
    private void toggleLight() {
        isLightOn = !isLightOn;
        if (isLightOn) {
            binding.lightOverlay.setVisibility(View.VISIBLE); // style.visibility = 'hidden'
            binding.lightSwitch.setSelected(true);
            binding.lightOverlay.setImageResource(R.drawable.livingroom_1);
        } else {
            // Allumer
            binding.lightOverlay.setVisibility(View.INVISIBLE); // style.visibility = 'visible'
            binding.lightSwitch.setSelected(false);
            binding.lightOverlay.setImageResource(R.drawable.livingroom_0);

        }
    }
     */
}