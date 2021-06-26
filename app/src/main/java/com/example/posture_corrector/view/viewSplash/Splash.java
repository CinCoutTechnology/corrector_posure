package com.example.posture_corrector.view.viewSplash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.posture_corrector.R;
import com.example.posture_corrector.presenter.ScreenSlide.ScreenSlidePager;

public class Splash extends AppCompatActivity {

    ImageView logo, splashImage;
    LottieAnimationView lottie;
    Animation animation;

    private static final int NUM_PAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        splashImage = findViewById(R.id.img);
        lottie = findViewById(R.id.lottie);

        ViewPager viewPager = findViewById(R.id.pager);
        ScreenSlidePager pagerAdapter = new ScreenSlidePager(getSupportFragmentManager(), NUM_PAGES);
        viewPager.setAdapter(pagerAdapter);

        animation = AnimationUtils.loadAnimation(this, R.anim.o_b_anim);
        viewPager.startAnimation(animation);

        splashImage.animate().translationX(-1600).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(1400).setDuration(1000).setStartDelay(4000);
        lottie.animate().translationY(1400).setDuration(1000).setStartDelay(4000);


    }
}