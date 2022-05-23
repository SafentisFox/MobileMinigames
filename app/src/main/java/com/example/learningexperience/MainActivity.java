package com.example.learningexperience;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    protected Animator addBackgroundAnimation(View v, int initialColor, int finalColor, int length) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), initialColor, finalColor);
        GradientDrawable background = (GradientDrawable) v.getBackground();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setColor((Integer) animation.getAnimatedValue());
            }
        });
        animator.setDuration(length);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        return animator;
    }

    int gameState = 1;
    
    protected AnimatorSet fillQueue(Queue<Integer> queue, ArrayList<Button> buttons){
        Random random = new Random();
        List<Animator> animatorList = new ArrayList<>();
        for (int i = 0; i < gameState; i++) {
            int randomInt = random.nextInt(9);
            queue.add(randomInt);
            animatorList.add(addBackgroundAnimation(buttons.get(randomInt), Color.WHITE, Color.CYAN, 300));
        }
        int savedGameState = gameState;
        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                gameState = -1;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                gameState = savedGameState;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.playSequentially(animatorList);
        return set;
    }

    
    
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<TableRow> rows = new ArrayList<>();
        rows.add(findViewById(R.id.row0));
        rows.add(findViewById(R.id.row1));
        rows.add(findViewById(R.id.row2));

        Button testButton = new Button(this);
        testButton.setText("TestButton");

        ArrayList<Button> buttons = new ArrayList<>();

//        LinearLayout.LayoutParams buttonParams =


        for (TableRow row :
                rows) {
            row.setBackgroundColor(Color.BLACK);
            for (int i = 0; i < 3; i++) {
                Button newButton = new Button(this);
//                newButton.setTextScaleX(0);
                newButton.setBackground(getDrawable(R.drawable.custom_button));
                newButton.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                newButton.setSoundEffectsEnabled(false);
                row.addView(newButton);
                buttons.add(newButton);
            }
        }

        addBackgroundAnimation(buttons.get(4), Color.WHITE, Color.CYAN, 500).start();
        Queue<Integer> memoryQueue = new LinkedList<>();
        memoryQueue.add(4);

        for (int i = 0; i < 9; i++) {
            Button button = buttons.get(i);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gameState<0) return;
                    if (memoryQueue.isEmpty()) return;
                    int goal = memoryQueue.peek();
                    Animator animation;
                    if (finalI == goal) {
                        animation = addBackgroundAnimation(buttons.get(finalI), Color.WHITE, Color.GREEN, 300);
                        memoryQueue.remove();
                    } else {
                        animation = addBackgroundAnimation(buttons.get(finalI), Color.WHITE, Color.RED, 300);
                    }

                    if (memoryQueue.isEmpty()){
                            gameState++;
                            animation.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    fillQueue(memoryQueue, buttons).start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                    }
                    animation.start();
                }
            });
        }

        Button restartButton = findViewById(R.id.RestartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameState = 1;
                memoryQueue.clear();
                fillQueue(memoryQueue, buttons).start();
            }
        });
    }
}