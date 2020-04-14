package com.titanarchers;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.android.material.math.MathUtils.dist;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class FragmentGroups  extends Fragment {

    private List<ArrowGroupModel> groupList = new ArrayList<>();
    private int NumberOfGroups = 10;
    private ArrowPointViewModel arrowModel;
    private List<ArrowPoint> apList;

    private RecyclerView recyclerView;
    private CustomAdapter mAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        arrowModel = ViewModelProviders.of(getActivity()).get(ArrowPointViewModel.class);

        apList = arrowModel.getArrowPoints().getValue();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            apList = arrowModel.getArrowPoints().getValue();
            if (arrowModel != null && apList != null) {
                getModel(apList);
            }
        }
    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_groups, container);

        if (retView != null) {
            recyclerView = retView.findViewById(R.id.recycler);

            mAdapter = new CustomAdapter(groupList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.setHasFixedSize(true);

            //if (apList != null) getModel(apList);
        }
        return retView;

    }

    private void getModel(List<ArrowPoint> arrowList){
        groupList.clear();

        for(int i = 0; i < NumberOfGroups; i++) {

            int idx = i * 3;
            if (arrowList.size() - 3 >= idx) {
                ArrowPoint a1, a2, a3;
                a1 = arrowList.get(idx);
                a2 = arrowList.get(idx + 1);
                a3 = arrowList.get(idx + 2);

                if (a1 != null && a2 != null && a3 != null) {
                    ArrowGroupModel groupModel = new ArrowGroupModel();
                    groupModel.setArrowPoint1(a1);
                    groupModel.setArrowPoint2(a2);
                    groupModel.setArrowPoint3(a3);
                    genMaxs(a1, a2, a3);
                    groupModel.setGroupRadius(adjRadius);
                    groupModel.setGroupCenterX(adjMX);
                    groupModel.setGroupCenterY(adjMY);
                    groupList.add(groupModel);
                } else {
                    break;
                }
            } else break;

        }
        if (groupList.size() > 0) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public class Test {
        private int idx;
        private ArrowPoint pt;
        private int dist;

        public Test(int i, ArrowPoint p, int d) {
            idx = i;
            pt = p;
            dist = d;
        }

        public int getIdx() { return idx; }
        public ArrowPoint getPt() { return pt; }
        public int getDist() { return dist; }
    }
    private List<Test> testArray = new ArrayList<>();
    private float adjRadius, adjMX, adjMY;

    private void genMaxs(ArrowPoint p1, ArrowPoint p2, ArrowPoint p3) {
        float mX, mY;
        int[] distanceToMean = new int[3];

        //find mean
        mX = (p1.x + p2.x + p3.x)/3;
        mY = (p1.y + p2.y + p3.y)/3;

        //dist from mean (x,y) to each point
        distanceToMean[0] = (int) dist(mX,mY,p1.x,p1.y);
        distanceToMean[1] = (int) dist(mX,mY,p2.x,p2.y);
        distanceToMean[2] = (int) dist(mX,mY,p3.x,p3.y);


        testArray.clear();
        testArray.add(new Test( 0, p1, distanceToMean[0]));
        testArray.add(new Test( 1, p2, distanceToMean[1]));
        testArray.add(new Test( 2, p3, distanceToMean[2]));

        Collections.sort(testArray, new MyComparator());    //(a, b) => (a.dist - b.dist));*/

        //get new point
        //testArray.get(0) largest value, testArray.get(1) 2nd largest value
        //testArray.get(3) smallest value
        double theta = Math.atan2(mY - testArray.get(0).pt.y, mX - testArray.get(0).pt.x);
        int section1 = testArray.get(1).dist;  //add distance
        float newPtX = (float) (mX + section1 * cos(theta));
        float newPtY = (float) (mY + section1 * sin(theta));

        adjRadius = dist(newPtX,newPtY, testArray.get(0).pt.x, testArray.get(0).pt.y) / 2;
        adjMX = (newPtX + testArray.get(0).pt.x)/2;
        adjMY = (newPtY + testArray.get(0).pt.y)/2;
/*
        adjMeans.push( {x : adjMX, y : adjMY } );
        adjMaxs.push(2 * adjRadius);
*/

    }

    class MyComparator implements Comparator<Test> {

        @Override
        public int compare(Test o1, Test o2) {
            if (o1.getDist() > o2.getDist()) {
                return -1;
            } else if (o1.getDist() < o2.getDist()) {
                return 1;
            }
            return 0;
        }
    }
}