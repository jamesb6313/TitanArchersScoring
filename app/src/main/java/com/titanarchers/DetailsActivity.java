package com.titanarchers;

import android.content.SharedPreferences;
import android.os.Bundle;
//import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class DetailsActivity extends AppCompatActivity {
    public EditText et_Name, et_Distance;
    public Spinner sp_units;
    public ArrayAdapter<CharSequence> unit_Adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Button btn_save, btn_cancel;


        et_Name = (EditText) findViewById(R.id.etName);
        et_Distance = (EditText) findViewById(R.id.etDistance);
        sp_units = findViewById(R.id.sp_Units);

        unit_Adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.units, android.R.layout.simple_spinner_item);
        unit_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_units.setAdapter(unit_Adapter);

        loadSavedPreferences();


        btn_save = findViewById(R.id.btn_saveDetails);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dist = Integer.parseInt(et_Distance.getText().toString());
                String unit = sp_units.getSelectedItem().toString();

                savePreferences("storedDistance", dist);
                savePreferences("storedName", et_Name.getText().toString());
                savePreferences("storedUnits", unit);
                finish();
            }
        });

        btn_cancel = findViewById(R.id.btn_cancelDetails);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        int defaultDistance = sharedPreferences.getInt("storedDistance", 10);
        String name = sharedPreferences.getString("storedName", "YourName");
        String distUnits = sharedPreferences.getString("storedUnits", "Yards");


        et_Distance.setText(String.valueOf(defaultDistance));
        et_Name.setText(name);
        int selectionPosition= unit_Adapter.getPosition(distUnits);
        sp_units.setSelection(selectionPosition);
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
