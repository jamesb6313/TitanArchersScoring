package com.titanarchers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArrowPointViewModel extends ViewModel {


    private MutableLiveData<List<ArrowPoint>> arrowPoint;
    private List<ArrowPoint> apList = new ArrayList<>();

    public LiveData<List<ArrowPoint>> getArrowPoints() {
        if (arrowPoint == null) {
            arrowPoint = new MutableLiveData<List<ArrowPoint>>();
            loadArrowPoints();
        }
        return arrowPoint;
    }

    public void updateArrowPoint(int position, int newScore, int newColor, float newX, float newY) {

        if (apList != null && apList.size() > position) {
            ArrowPoint pt = apList.get(position);
            pt.score = newScore;
            pt.color = newColor;
            pt.x = newX;
            pt.y = newY;

            arrowPoint.setValue(apList);
        }
    }

    public void deleteArrowPoint(int position) {
        if (apList.size() > position) {
            apList.remove(position);
            arrowPoint.setValue(apList);
        }
    }

    public int getListSize() {
        if (apList.size() > 0) {
            return apList.size();
        }
        return 0;
    }

    public void addArrowPoint(int position, int newScore, int newColor, float newX, float newY) {
        ArrowPoint pt = ArrowPoint.builder().setScore(newScore).setColor(newColor).setX(newX).setY(newY).build();
        apList.add(pt);
        arrowPoint.setValue(apList);
    }

    private void loadArrowPoints() {
        if (apList.size() > 0) {
            apList = arrowPoint.getValue();
        }
    }
}
