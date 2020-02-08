package com.titanarchers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    public Fragment fragScoreCard;  //fragmentScoreCard
    //public Fragment fragDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Titan Archers Scoring");

        FragmentManager fm = getSupportFragmentManager();
        fragScoreCard = fm.findFragmentById(R.id.fragmentScoreCard);
        //fragDetails = fm.findFragmentById(R.id.fragmentDetails);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragScoreCard);
        //ft.hide(fragDetails);
        ft.commit();

        loadSavedPreferences();
    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int defaultDistance = sharedPreferences.getInt("storedDistance", 10);
        String name = sharedPreferences.getString("storedName", "YourName");
        String distUnits = sharedPreferences.getString("storedDistUnits", "yards");
    }

    @Override
    public void onBackPressed()
    {
        //boolean fragHidden = true;
        //FragmentManager fm = getSupportFragmentManager();
        //Fragment frag2 = fm.findFragmentById(R.id.fragmentTarget);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!fragScoreCard.isHidden()) {
            ft.hide(fragScoreCard);
        } else {
            super.onBackPressed();
        }


/*        if (!fragScoreCard.isHidden()){
            ft.hide(fragScoreCard);
        } else if (!fragDetails.isHidden()) {
            ft.hide(fragDetails);
        } else if (fragScoreCard.isHidden() && fragDetails.isHidden()) {
            super.onBackPressed();
        }*/
        ft.commit();
    }

}
