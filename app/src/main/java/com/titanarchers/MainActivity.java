package com.titanarchers;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public Fragment fragScoreCard;  //fragmentScoreCard
    public Fragment fragGroups;     //fragmentGroups
    public Fragment fragGroupCard;  //fragmentGroupCard

    public int defaultDistance = 10;
    public String defaultName = "YourName";
    public String defaultUnits = "yards";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Titan Archers Scoring");

        FragmentManager fm = getSupportFragmentManager();
        fragScoreCard = fm.findFragmentById(R.id.fragmentScoreCard);
        fragGroups = fm.findFragmentById(R.id.fragmentGroups);
        fragGroupCard = fm.findFragmentById(R.id.fragmentGroupCard);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragScoreCard);
        ft.hide(fragGroups);
        ft.hide(fragGroupCard);
        ft.commit();

        if (loadSavedPreferences()) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Enter name & distance")
                .setMessage("Do you want to add your name & distance to score card?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //what should happen
                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(getApplicationContext(),"No name will be added",Toast.LENGTH_LONG).show();
                        dialogInterface.cancel();
                    }
                });

            alertDialog.show();
        }
    }

    public boolean loadSavedPreferences() {
        boolean result = false;

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        defaultDistance = sharedPreferences.getInt("storedDistance", defaultDistance);
        defaultName = sharedPreferences.getString("storedName", defaultName);
        defaultUnits = sharedPreferences.getString("storedUnits", defaultUnits);
        if (defaultName != null) {
            result = defaultName.equalsIgnoreCase("YourName");
        }
        return result;
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!fragScoreCard.isHidden() || !fragGroups.isHidden() || !fragGroupCard.isHidden()) {

            if (!fragScoreCard.isHidden()) ft.hide(fragScoreCard);
            if (!fragGroupCard.isHidden()) ft.hide(fragGroupCard);
            else if (!fragGroups.isHidden()) {
                ft.hide(fragGroups);
                Fragment targetCanvasFrag = fm.findFragmentById(R.id.fragmentTarget);
                TargetView targetCanvasView = targetCanvasFrag.getView().findViewById(R.id.targetView);

                targetCanvasView.resetGroupDrawingStatus();
            }

        } else {
            super.onBackPressed();
        }
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.details:
                Intent intentD = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intentD);
                return true;
            case R.id.grouping:
                ArrowPointViewModel arrowModel;
                arrowModel = ViewModelProviders.of(this).get(ArrowPointViewModel.class);

                if (arrowModel.getListSize() >= 3) {
                    // Get Fragment belonged Activity
                    final FragmentActivity fragmentBelongActivity = this;
                    // Do not use fragmentBelongActivity.getFragmentManager() method which is not compatible with older android os version. .
                    final FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();

                    fragGroups = fragmentManager.findFragmentById(R.id.fragmentGroups);  // Get scorecard Fragment object.

                    FragmentTransaction ft = fragmentBelongActivity.getSupportFragmentManager().beginTransaction();

                    if (fragGroups.isHidden()) {
                        ft.show(fragGroups);
                        //windowsButton.setText("Hide Card");
                    } else {
                        ft.hide(fragGroups);
                        //windowsButton.setText("Show Card");
                    }
                    ft.commit();

                    //Intent intentG = new Intent(MainActivity.this, GroupingActivity.class);
                    //startActivity(intentG);
                } else {
                    Toast.makeText(this, "Nothing to show", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.about:
                //paintView.clear();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
