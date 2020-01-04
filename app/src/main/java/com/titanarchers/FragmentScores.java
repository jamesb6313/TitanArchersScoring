package com.titanarchers;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

public class FragmentScores extends Fragment {

    private TextView tvRow1, tvRow2, tvRow3, tvRow4, tvRow5;
    private TextView[] mTvScores = new TextView[30];

    @Override
    public void onCreate(@Nullable Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        ArrowPointViewModel model;
        model = ViewModelProviders.of(this.getActivity()).get(ArrowPointViewModel.class);

        model.getArrowPoints().observe(this, new Observer<List<ArrowPoint>>() {
            @Override
            public void onChanged(List<ArrowPoint> item) {
                displayScores(item);

                if ((item.size() > 0) && (item.size() % 6 == 0)) {
                    calcSubtotal(item);
                }

            }
        });

    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_scores, container);
        String[] id;

        if(retView != null) {

            int temp;
            id = new String[]{
                    "frS1", "frS2", "frS3", "frS4", "frS5", "frS6",
                    "frS7", "frS8", "frS9", "frS10", "frS11", "frS12",
                    "frS13", "frS14", "frS15", "frS16", "frS17", "frS18",
                    "frS19", "frS20", "frS21", "frS22", "frS23", "frS24",
                    "frS25", "frS26", "frS27", "frS28", "frS29", "frS30"
            };

            for (int i = 0; i < id.length; i++) {

                //TODO; use Objects.requireNonNull() but need API 19 or greater - currently API17
                String PACKAGE_NAME = getActivity().getPackageName();

                if (PACKAGE_NAME != null) {
                    temp = getResources().getIdentifier(id[i], "id", PACKAGE_NAME);
                    mTvScores[i] = retView.findViewById(temp);
                } else {
                    Log.d("MyINFO", "Error getting current score TextView in FragmentScores.java");
                }

            }

            tvRow1 = retView.findViewById(R.id.fragmentScoreSubtotalLine1);
            tvRow2 = retView.findViewById(R.id.fragmentScoreSubtotalLine2);
            tvRow3 = retView.findViewById(R.id.fragmentScoreSubtotalLine3);
            tvRow4 = retView.findViewById(R.id.fragmentScoreSubtotalLine4);
            tvRow5 = retView.findViewById(R.id.fragmentScoreSubtotalLine5);
        }
        return retView;
    }

    private void displayScores(List<ArrowPoint> l) {

        for (int i = 0; i < l.size(); i++) {
            String scoreStr;
            ArrowPoint ap = l.get(i);

            if (ap.score == 0) scoreStr ="M";
            else {
                if (ap.score == 11) scoreStr = "X";
                else scoreStr = String.valueOf(ap.score);
            }

            TextView tvTemp = mTvScores[i];
            tvTemp.setTextColor(ap.color);
            tvTemp.setText(scoreStr);
        }
    }

    private void calcSubtotal(List<ArrowPoint> l) {
        int subtotal = 0;
        int row = l.size() / 6;
        ArrowPoint ap;


        //TODO: need to calc all current row titles
        switch (row) {
            case 1:
                for (int i = 0; i < l.size(); i++) {
                    ap = l.get(i);
                    subtotal += (ap.score == 11) ? 10: ap.score;
                }
                tvRow1.setTextColor(Color.RED);
                tvRow1.setText(String.valueOf(subtotal));
                break;
            case 2:
                for (int i = 6; i < l.size(); i++) {
                    ap = l.get(i);
                    subtotal += (ap.score == 11) ? 10: ap.score;
                }
                tvRow2.setTextColor(Color.RED);
                tvRow2.setText(String.valueOf(subtotal));
                break;
            case 3:
                for (int i = 12; i < l.size(); i++) {
                    ap = l.get(i);
                    subtotal += (ap.score == 11) ? 10: ap.score;
                }
                tvRow3.setTextColor(Color.RED);
                tvRow3.setText(String.valueOf(subtotal));
                break;
            case 4:
                for (int i = 18; i < l.size(); i++) {
                    ap = l.get(i);
                    subtotal += (ap.score == 11) ? 10: ap.score;
                }
                tvRow4.setTextColor(Color.RED);
                tvRow4.setText(String.valueOf(subtotal));
                break;
            case 5:
                for (int i = 24; i < l.size(); i++) {
                    ap = l.get(i);
                    subtotal += (ap.score == 11) ? 10: ap.score;
                }
                tvRow5.setTextColor(Color.RED);
                tvRow5.setText(String.valueOf(subtotal));
                break;
        }

    }

}
