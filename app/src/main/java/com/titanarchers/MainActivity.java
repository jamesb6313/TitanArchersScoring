package com.titanarchers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public Fragment frag1;
    public Fragment frag3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Titan Archers Scoring");

        FragmentManager fm = getSupportFragmentManager();
        frag1 = fm.findFragmentById(R.id.fragmentScoreCard);
        //frag3 = fm.findFragmentById(R.id.fragmentTargetImage);
        Fragment frag2 = fm.findFragmentById(R.id.fragmentTarget);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(frag1);
        //ft.hide(frag3);
        ft.commit();
    }

    @Override
    public void onBackPressed()
    {
        //boolean fragHidden = true;
        //FragmentManager fm = getSupportFragmentManager();
        //Fragment frag2 = fm.findFragmentById(R.id.fragmentTarget);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!frag1.isHidden()){
            ft.hide(frag1);
        } else {
            super.onBackPressed();
        }
        ft.commit();
    }

}
