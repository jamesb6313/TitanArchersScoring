package com.titanarchers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class FragmentOptions extends Fragment {
    private ArrowPointViewModel model;

    //private View v_target;
    //private String fn;
    //private static final int CREATE_REQUEST_CODE = 40;

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        model = ViewModelProviders.of(this.getActivity()).get(ArrowPointViewModel.class);
    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_options, container);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();

        // Do not use fragmentBelongActivity.getFragmentManager() method which is not compatible with older android os version. .
        final FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();
        final Fragment scoreFragment = fragmentManager.findFragmentById(R.id.fragmentScores); // Get scores Fragment object.


        if(retView!=null)
        {

            final Button redoButton = retView.findViewById(R.id.fOptionAndroid);
            final Button detailsButton = retView.findViewById(R.id.fOptionIos);
            final Button showButton = retView.findViewById(R.id.fOptionWindows);

            // Click this button to REDO Last ArrowPoint placed
            redoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model != null) {
                        if (model.getListSize() <= 0){
                            Toast.makeText(fragmentBelongActivity, "Nothing to redo", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //delete last ArrowPoint added
                        int pos = model.getListSize() - 1;
                        if (pos >= 0) {
                            model.deleteArrowPoint(pos);
                        }

                        // Get the TextView object in right Fragment.
                        //TODO; use Objects.requireNonNull() but need API 19 or greater - currently API17
                        String frScoreCell = "frS" + (pos + 1);
                        String PACKAGE_NAME = getActivity().getPackageName();
                        int temp;
                        if (PACKAGE_NAME != null) {
                            temp = getResources().getIdentifier(frScoreCell, "id", PACKAGE_NAME);
                            final TextView rightFragmentTextView = scoreFragment.getView().findViewById(temp);
                            // Set text in right Fragment TextView.
                            rightFragmentTextView.setText(getResources().getString(R.string.blankScore));
                        } else {
                            Log.d("MyINFO", "Error getting current score TextView in FragmentScores.java");
                        }

                        Toast.makeText(fragmentBelongActivity, "Redo last arrow", Toast.LENGTH_LONG).show();
                    }
                }
            });

            // Click this button will show a Toast popup - TODO Indevelopment - Enter Distance????.
            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(fragmentBelongActivity, "In development", Toast.LENGTH_SHORT).show();
                }
            });

            // Click this button will show ScoreCard (WebView with PNG img & HTML)
            showButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model != null && model.getListSize() > 0) {
                        Fragment scorecard = fragmentManager.findFragmentById(R.id.fragmentScoreCard);  // Get scorecard Fragment object.

                        FragmentTransaction ft = fragmentBelongActivity.getSupportFragmentManager().beginTransaction();

                        if (scorecard.isHidden()) {
                            ft.show(scorecard);
                            //windowsButton.setText("Hide Card");
                        } else {
                            ft.hide(scorecard);
                            //windowsButton.setText("Show Card");
                        }
                        ft.commit();
                    } else {
                        Toast.makeText(fragmentBelongActivity, "Nothing to show.", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

        return retView;
    }
}
