package com.smallgroup.drawapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class StartActivity extends AppCompatActivity {

    private Button start;
    private RelativeLayout fon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        start = findViewById(R.id.start);
        fon = findViewById(R.id.load_fon);
        fon.setVisibility(View.GONE);
        start.setOnClickListener(v -> {
            startFonAnim();
        });

        //startAnim();
    }

    void startFonAnim() {
        fon.setVisibility(View.VISIBLE);
        SpringAnimation anim = new SpringAnimation(fon, DynamicAnimation.TRANSLATION_Y, 0);
        anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        anim.setStartValue(2000);
        anim.setStartVelocity(2);
        anim.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        anim.addEndListener((animation, canceled, value, velocity) -> startMainActivity());
        anim.start();
    }

    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.top_out, R.anim.bottom_in);
    }

    void startAnim() {
        SpringAnimation anim = new SpringAnimation(start, DynamicAnimation.TRANSLATION_Y, 0);
        float pixelPerSecond = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        anim.setStartValue(-1000);
        anim.setMaxValue(260);
        anim.setStartVelocity(pixelPerSecond);
        anim.getSpring().setStiffness(SpringForce.STIFFNESS_LOW);
        anim.start();
    }
}