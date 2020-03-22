package com.titanarchers;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    public Fragment fragScoreCard;  //fragmentScoreCard
    //public Fragment fragDetails;

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
        //fragDetails = fm.findFragmentById(R.id.fragmentDetails);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragScoreCard);
        //ft.hide(fragDetails);
        ft.commit();




        if (loadSavedPreferences()) {
            //display SnackBar - see build.gradle Module: app
//            View parentLayout = findViewById(android.R.id.content);
////            Snackbar.make(parentLayout, "Do you want to add your name & distance to score card", Snackbar.LENGTH_LONG)
////                    .setAction("CLOSE", new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////
////                        }
////                    })
////                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
////                    .show();
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
            //openDialog();
        }
    }

/*    public void openDialog() {
        final Dialog dialog = new Dialog(this);
        //final AlertDialog dialog = new AlertDialog(this);

        dialog.setContentView(R.layout.name_dialog);
        dialog.setTitle(R.string.nDialog_title);
    }*/

    private boolean loadSavedPreferences() {
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
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
                return true;
            case R.id.about:
                //paintView.clear();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
