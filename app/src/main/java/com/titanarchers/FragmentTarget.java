package com.titanarchers;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class FragmentTarget extends Fragment {
    private TargetView targetView;
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
        View retView = inflater.inflate(R.layout.fragment_target, container);

        if(retView!=null) {
            targetView = (TargetView) retView.findViewById(R.id.targetView);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            targetView.init(metrics);

            targetView.setCoordinatesListener((new TargetView.OnCoordinateUpdate() {
                @Override
                public void onUpdate(int score, int color, float x, float y) {
                    model.addArrowPoint(0, score, color, x, y);
                }
            }));
        }
        return retView;
    }
}
