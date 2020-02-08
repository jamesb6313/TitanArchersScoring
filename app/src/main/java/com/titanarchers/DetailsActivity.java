package com.titanarchers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DetailsActivity extends AppCompatActivity {
    public EditText et_Name, et_Distance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Button btn_save, btn_cancel;

        et_Name = (EditText) findViewById(R.id.etName);
        et_Distance = (EditText) findViewById(R.id.etDistance);
        loadSavedPreferences();

        btn_save = findViewById(R.id.btn_saveDetails);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity
                int dist = Integer.parseInt(et_Distance.getText().toString());

                savePreferences("storedDistance", dist);
                savePreferences("storedName", et_Name.getText().toString());
                onBackPressed();
            }
        });

        btn_cancel = findViewById(R.id.btn_cancelDetails);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int defaultDistance = sharedPreferences.getInt("storedDistance", 10);
        String name = sharedPreferences.getString("storedName", "YourName");
        String distUnits = sharedPreferences.getString("storedDistUnits", "yards");


        et_Distance.setText(String.valueOf(defaultDistance));
        et_Name.setText(name);
    }

    private void savePreferences(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
