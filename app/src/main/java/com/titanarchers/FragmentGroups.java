package com.titanarchers;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.android.material.math.MathUtils.dist;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class FragmentGroups  extends Fragment {

    public List<ArrowGroupModel> groupList = new ArrayList<>();
    private ArrowPointViewModel arrowModel;
    private CustomAdapter mAdapter;
    private static final int FALSE = 0, TRUE = 1, NOT_SET = 2;
    private int groupMode = FALSE;
    private TargetView targetCanvasView;
    private static final int DEFAULT_COLOR = Color.GREEN;
    private int ratingColor = DEFAULT_COLOR;

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        arrowModel = ViewModelProviders.of(getActivity()).get(ArrowPointViewModel.class);

        //apList = arrowModel.getArrowPoints().getValue();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        List<ArrowPoint> apList;

        if (!hidden) {
            apList = arrowModel.getArrowPoints().getValue();
            if (arrowModel != null && apList != null) {
                // Get Fragment belonged Activity
                FragmentActivity fragmentBelongActivity = getActivity();

                // Do not use fragmentBelongActivity.getFragmentManager() method which is not compatible with older android os version. .
                FragmentManager fm = fragmentBelongActivity.getSupportFragmentManager();

                Fragment targetCanvasFrag = fm.findFragmentById(R.id.fragmentTarget);
                targetCanvasView = targetCanvasFrag.getView().findViewById(R.id.targetView);
                getModel(apList);

                FragmentGroupCard fragmentGroupCard = (FragmentGroupCard) fm.findFragmentById(R.id.fragmentGroupCard);
                fragmentGroupCard.setGroups(groupList);

                groupMode = TRUE;
                targetCanvasView.setGroupDrawingStatus(groupMode, groupList);
            }
        }
    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_groups, container);
        RecyclerView recyclerView;
        Button btn_share;

        if (retView != null) {
            recyclerView = retView.findViewById(R.id.recycler);

            mAdapter = new CustomAdapter(groupList, new CustomAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ArrowGroupModel arrowGroup) {

                    //Fragment targetCanvasFrag = fm.findFragmentById(R.id.fragmentTarget);
                    //targetCanvasView = targetCanvasFrag.getView().findViewById(R.id.targetView);
                    //if (drawGroup == FALSE) drawGroup = TRUE;
                    //else drawGroup = FALSE;
                    groupMode = TRUE;

                    targetCanvasView.setGroupDrawingStatus(groupMode, groupList);
                    //Toast.makeText(fragmentBelongActivity, "Group Center = (" +arrowGroup.getGroupCenterX() + ", " + arrowGroup.getGroupCenterY() + ")", Toast.LENGTH_LONG).show();
                }
            });
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            recyclerView.setHasFixedSize(true);

            // Get ref to TargetView - Get Fragment belonged Activity
            final FragmentActivity fragmentBelongActivity = getActivity();
            final FragmentManager fm = fragmentBelongActivity.getSupportFragmentManager();

            btn_share = retView.findViewById(R.id.btn_share2);
            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupList != null && groupList.size() > 0) {


                        // Get groupCard Fragment object from MainActivity()
                        Fragment groupCard = fm.findFragmentById(R.id.fragmentGroupCard);
                        FragmentTransaction ft = fragmentBelongActivity.getSupportFragmentManager().beginTransaction();

                        if (groupCard == null) throw new AssertionError();
                        if (groupCard.isHidden()) {
                            ft.show(groupCard);
                        } else {
                            ft.hide(groupCard);
                        }
                        ft.commit();
                    } else {
                        Toast.makeText(fragmentBelongActivity, "No groups to show.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            //if (apList != null) getModel(apList);
        }
        return retView;

    }

    private void getModel(List<ArrowPoint> arrowList){
        int NumberOfGroups = 10;
        int grpRating;

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

                    groupModel.setGroupTextColor(targetCanvasView.getColorValue(i));
                    groupModel.setArrowPoint1(a1);
                    groupModel.setArrowPoint2(a2);
                    groupModel.setArrowPoint3(a3);

                    calcGroupCircle(a1, a2, a3);
                    groupModel.setGroupRadius(adjRadius);
                    groupModel.setGroupCenterX(adjMX);
                    groupModel.setGroupCenterY(adjMY);

                    grpRating = calcGroupRating(adjRadius);
                    groupModel.setGroupRating(grpRating);

                    int grpPercent = calcGroupPercent(grpRating);
                    groupModel.setGroupPercent(grpPercent);

                    calcRatingColor(grpRating);
                    groupModel.setGroupColor(ratingColor);
                    groupModel.setShowGroup(false);

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

        private Test(int i, ArrowPoint p, int d) {
            idx = i;
            pt = p;
            dist = d;
        }

        public int getIdx() { return idx; }
        //public ArrowPoint getPt() { return pt; }
        private int getDist() { return dist; }
    }
    private List<Test> testArray = new ArrayList<>();
    private float adjRadius, adjMX, adjMY;

    // Group percent = group size (rating - 0 to 10) * distance (as score) from target center to group center (0 to 10)
    private int calcGroupPercent(int grpSize) {
        //float targetCenterX = targetCanvasView.centerX;
        //float targetCenterY = targetCanvasView.centerY;

        //distanceToGroup = (int) dist(targetCenterX, targetCenterY, adjMX, adjMY);

        int groupCenterScore = targetCanvasView.getScore(adjMX, adjMY);
        if (groupCenterScore == 11) groupCenterScore = 10;

        int percentValue = (grpSize * groupCenterScore) >= 100 ? 99 : (grpSize * groupCenterScore);
        return percentValue;

    }

    private int calcGroupRating(float radius) {
        int rating = 11;
        boolean found = false;

        for (int i = 0; i < targetCanvasView.mRadii.length; i++) {
            float targetRadius = targetCanvasView.mRadii[i];
            float grpRadius = radius;   // * targetCanvasView.mScaleFactor;

            if (grpRadius < targetRadius) {
                found = true;
            }
            if (found) break;
            rating--;
        }

        if (!found) rating = 0;
        return rating;

    }

    //@RequiresApi(api = Build.VERSION_CODES.M)
    private void calcRatingColor(int grpRating) {
        switch (grpRating) {
            case 1: case 2:
                ratingColor = Color.WHITE;
                break;
            case 3: case 4:
                ratingColor = Color.BLACK;
                break;
            case 5: case 6:
                ratingColor = Color.BLUE;
                break;
            case 7: case 8:
                ratingColor = Color.RED;
                break;
            case 9: case 10: case 11:
                //String tColor =
                ratingColor = Build.VERSION.SDK_INT<23 ? getResources().getColor(R.color.colorYellowRating) : getResources().getColor(R.color.colorYellowRating,null);
                break;
            default:
                ratingColor = DEFAULT_COLOR;
                break;
        }
    }

    private void calcGroupCircle(ArrowPoint p1, ArrowPoint p2, ArrowPoint p3) {
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