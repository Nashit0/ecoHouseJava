package com.example.ecohouse.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.example.ecohouse.R;
import com.example.ecohouse.databinding.FragmentGameBinding;
import com.example.ecohouse.model.EcoProblem;
import com.example.ecohouse.model.FaucetProblem;
import com.example.ecohouse.viewmodel.GameViewModel;

import static com.example.ecohouse.utils.ProblemIds.*;

import java.util.Arrays;
import java.util.List;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding;
    private GameViewModel viewModel;

    // Les vues qui seront amenées à etre mise à jour
    private List<ImageView> problemViews;



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

        initProblemViews();

        // LES OBSERVEURS (la vue qui change lorsque le viewmodel change)

        viewModel.getActiveCount().observe(getViewLifecycleOwner(), count -> {
            if (count > 0) {
                binding.messageGaspillage.setText("ALERTE: " + count + " GASPILLAGES !");
                binding.messageGaspillageDouble.setText("ALERTE: " + count + " GASPILLAGES !");
            } else {
                binding.messageGaspillage.setText("TOUT VA BIEN");
                binding.messageGaspillageDouble.setText("TOUT VA BIEN");
            }
        });

        // afin d'eviter que la jauge grandisse excessivement si les dimesnions du parent
        // n'ont pas encore été chargée
        binding.jaugeGaspillage.post(() -> {
            ViewGroup.LayoutParams params = binding.jaugeGaspillage.getLayoutParams();
            params.width = 1;
            binding.jaugeGaspillage.setLayoutParams(params);
        });

        viewModel.getUrgency().observe(getViewLifecycleOwner(), level -> {
            // Calcul de la largeur de la jauge
            float ratio = level / 100f;
            int maxWidth = toPx(324);
            float safeRatio = Math.max(0f, Math.min(1f, ratio)); // sécurité pour que le ratio reste entre 0 et 1
            int calculatedWidth = (int) (maxWidth * safeRatio);
            int finalWidth = Math.max(1, calculatedWidth);


            ViewGroup.LayoutParams params = binding.jaugeGaspillage.getLayoutParams();
            params.width = finalWidth;
            binding.jaugeGaspillage.setLayoutParams(params);
        });

        viewModel.getIsGameOver().observe(getViewLifecycleOwner(), gameOver -> {
            if (gameOver) {
                binding.messageGaspillage.setText("MERE NATURE EST DEÇUE...");
                binding.messageGaspillageDouble.setText("MERE NATURE EST DEÇUE...");
                binding.messageGaspillage.setTextColor(Color.RED);

                // Optionnel : Afficher un écran de fin ou arrêter les clics
                binding.getRoot().setAlpha(0.8f); // Effet visuel
                view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
            }
        });


        viewModel.getProblemsUpdate().observe(getViewLifecycleOwner(), problems -> {
            for (ImageView image : problemViews) {
                String id = (String) image.getTag();
                if (id == null) continue;

                for (EcoProblem p : problems) {
                    if (p.getId().equals(id)) {
                        image.setImageResource(p.isActive() ? p.getActiveDrawable() : p.getInactiveDrawable());
                        syncViewState(p);
                        break;
                    }
                }
            }
        });

        binding.playButton.setOnClickListener(v -> {
            binding.tutorialScreen.setVisibility(0);
        });

        binding.lightSwitch1.setOnClickListener(v -> viewModel.updateProblemInput(SMALL_LIGHT_1));
        binding.lightSwitch2.setOnClickListener(v -> viewModel.updateProblemInput(SMALL_LIGHT_2));
        binding.lightSwitch3.setOnClickListener(v -> viewModel.updateProblemInput(LARGE_LIGHT));

        setupRobinetTouchListener(binding.robinetBath, FAUCET_BATH);
        setupRobinetTouchListener(binding.robinetCuisine, FAUCET_KITCHEN);
        setupRobinetTouchListener(binding.robinetSalleDeBain, FAUCET_SDB);

        setupHandTouchListener();
        setupTempTouchListener();
    }

    private void initProblemViews() {
        problemViews = Arrays.asList(
                binding.kitchenFaucet,
                binding.faucetSdb,
                binding.bath,
                binding.sLamp1,
                binding.sLamp2,
                binding.lLamp,
                binding.stove,
                binding.fridge,
                binding.cheminee
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRobinetTouchListener(ImageView faucet, String type) {
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
                            viewModel.updateProblemInput(type, internalRotation);
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

                        viewModel.updateProblemInput(type, internalRotation);
                        break;
                }
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupHandTouchListener() {
        binding.handGrab.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY; // le deplacement
            float startX, startY; // les coordonnées de depart pour y retourner

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
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
                        if (isViewOverlapping(binding.handGrab, binding.four) || isViewOverlapping(binding.handGrab, binding.fridge)) {
                            binding.handGrab.setImageResource(R.drawable.hand_active);
                        } else {
                            binding.handGrab.setActivated(false);
                            binding.handGrab.setImageResource(R.drawable.hand_default);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isViewOverlapping(binding.handGrab, binding.four)) {
                            // ACTION REUSSIE
                            viewModel.updateProblemInput(STOVE);
                        } else if (isViewOverlapping(binding.handGrab, binding.fridge)) {
                            viewModel.updateProblemInput(FRIDGE);
                        }
                        binding.handGrab.setImageResource(R.drawable.hand_default);
                        view.animate().x(startX).y(startY).start();
                        break;
                }
                return true;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTempTouchListener() {
        View handle = binding.thermoHandle;
        View jauge = binding.thermoJauge;
        int maxHeight = toPx(150);
        handle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
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
                        viewModel.updateProblemInput(FIREPLACE, newHeight, maxHeight);

                        boolean isAboveHalf = newHeight > (maxHeight / 2);
                        int color = isAboveHalf ? Color.parseColor("#ED5C5F") : Color.parseColor("#7D6812");
                        jauge.setBackgroundColor(color);
                        EcoProblem fireplace = viewModel.getProblemById(FIREPLACE);

                        if (fireplace != null) {
                            boolean isCurrentlyActive = fireplace.isActive();

                            if (isAboveHalf != isCurrentlyActive) {
                                viewModel.updateProblemInput(FIREPLACE, newHeight, maxHeight);

                                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void syncViewState(EcoProblem p) {
        String id = p.getId();
        if (p instanceof FaucetProblem) {
            float targetRotation = p.isActive() ? 0f : -90f; // 0° = Ouvert, -90° = Fermé
            View faucet = null;
            switch (p.getId()) {
                case FAUCET_BATH:
                    faucet = binding.robinetBath ;
                    break;
                case FAUCET_KITCHEN:
                    faucet = binding.robinetCuisine ;
                    break;
                case FAUCET_SDB:
                    faucet = binding.robinetSalleDeBain ;
                    break;
            }
            if(faucet != null){
                if (Math.abs(faucet.getRotation() - targetRotation) > 5f) {
                    faucet.animate().rotation(targetRotation).setDuration(400).start();
                }
            }
        } else if (id.equals(FIREPLACE)) {
            int targetHeight = p.isActive() ? toPx(125) : toPx(10);
            View jauge = binding.thermoJauge;

            if (Math.abs(jauge.getHeight() - targetHeight) > 5) {
                ValueAnimator anim = ValueAnimator.ofInt(jauge.getHeight(), targetHeight);
                anim.addUpdateListener(animation -> {
                    ViewGroup.LayoutParams lp = jauge.getLayoutParams();
                    lp.height = (int) animation.getAnimatedValue();
                    jauge.setLayoutParams(lp);

                    jauge.setBackgroundColor(p.isActive() ? Color.parseColor("#ED5C5F") : Color.parseColor("#7D6812"));
                });
                anim.setDuration(400).start();
            }
        } else if (id.equals(SMALL_LIGHT_1)) {
            binding.lightSwitch1.setImageResource(p.isActive() ? R.drawable.switch_on : R.drawable.switch_off);
        } else if (id.equals(SMALL_LIGHT_2)) {
            binding.lightSwitch2.setImageResource(p.isActive() ? R.drawable.switch_on : R.drawable.switch_off);
        } else if (id.equals(LARGE_LIGHT)) {
            binding.lightSwitch3.setImageResource(p.isActive() ? R.drawable.switch_on : R.drawable.switch_off);
        }
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
        } else if (badPoints > 323) {
            params.width = (int) (324 * getResources().getDisplayMetrics().density);

        }
        params.height = 20;
        rectangleView.setLayoutParams(params);
    }
}