package com.example.posture_corrector.presenter.ScreenSlide;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.posture_corrector.presenter.Fragment.OnBoardingFragment1;
import com.example.posture_corrector.presenter.Fragment.OnBoardingFragment2;

public class ScreenSlidePager extends FragmentStatePagerAdapter {

    int behavior;

    public ScreenSlidePager(@NonNull FragmentManager fm, int behavior) {
        super(fm);
        this.behavior = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnBoardingFragment1();
            case 1:
                return new OnBoardingFragment2();
        }
        return null;
    }

    @Override
    public int getCount() {
        return behavior;
    }

}
