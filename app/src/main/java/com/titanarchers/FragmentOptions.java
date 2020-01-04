package com.titanarchers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

public class FragmentOptions extends Fragment {
    private ArrowPointViewModel model;


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

        if(retView!=null)
        {
            // Click this button will show the text in right fragment.
            Button androidButton = (Button)retView.findViewById(R.id.fOptionAndroid);
            androidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //delete last ArrowPoint added
                    int pos = model.getListSize() - 1;
                    if (pos >= 0) {
                        model.deleteArrowPoint(pos);
                    }

                    // Do not use fragmentBelongActivity.getFragmentManager() method which is not compatible with older android os version. .
                    FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();

                    // Get scores Fragment object.
                    Fragment scoreFragment = fragmentManager.findFragmentById(R.id.fragmentScores);

                    // Get the TextView object in right Fragment.
                    final TextView rightFragmentTextView = (TextView)scoreFragment.getView().findViewById(R.id.frS1);

                    // Set text in right Fragment TextView.
                    rightFragmentTextView.setText("00");
                    Toast.makeText(fragmentBelongActivity, "Redo last arrow", Toast.LENGTH_SHORT).show();
                }
            });


            // Click this button will show a Toast popup.
            Button iosButton = (Button)retView.findViewById(R.id.fOptionIos);
            iosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //paintView.clear();
                    Toast.makeText(fragmentBelongActivity, "You click IOS button.", Toast.LENGTH_SHORT).show();
                }
            });


            // Click this button will show an alert dialog.
            Button windowsButton = (Button)retView.findViewById(R.id.fOptionWindows);
            windowsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(fragmentBelongActivity).create();
                    alertDialog.setMessage("You click Windows button.");
                    alertDialog.show();
                }
            });
        }

        return retView;
    }

}
