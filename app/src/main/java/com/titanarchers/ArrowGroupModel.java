package com.titanarchers;

public class ArrowGroupModel {
    private ArrowPoint arrowPoint1;
    private ArrowPoint arrowPoint2;
    private ArrowPoint arrowPoint3;
    private float groupRadius;
    private float groupCenterX;
    private float groupCenterY;
    private int groupRating;
    private int groupPercent;
    private int groupColor;
    private boolean showGroup;
    private int groupTextColor;


    public ArrowPoint getArrowPoint1() {
        return arrowPoint1;
    }
    public void setArrowPoint1(ArrowPoint arrowPoint1) {
        this.arrowPoint1 = arrowPoint1;
    }

    public ArrowPoint getArrowPoint2() {
        return arrowPoint2;
    }
    public void setArrowPoint2(ArrowPoint arrowPoint2) {
        this.arrowPoint2 = arrowPoint2;
    }

    public ArrowPoint getArrowPoint3() { return arrowPoint3; }
    public void setArrowPoint3(ArrowPoint arrowPoint3) { this.arrowPoint3 = arrowPoint3; }

/////
    public float getGroupRadius() { return groupRadius; }
    public void setGroupRadius(float groupRadius) { this.groupRadius = groupRadius; }

    public float getGroupCenterX() { return groupCenterX; }
    public void setGroupCenterX(float groupCenterX) { this.groupCenterX = groupCenterX; }

    public float getGroupCenterY() { return groupCenterY; }
    public void setGroupCenterY(float groupCenterY) { this.groupCenterY = groupCenterY; }

    public int getGroupRating() { return groupRating; }
    public void setGroupRating(int groupRating) { this.groupRating = groupRating; }

    public int getGroupColor() { return groupColor; }
    public void setGroupColor(int groupColor) { this.groupColor = groupColor; }

    public boolean getShowGroup() { return showGroup; }
    public void setShowGroup(boolean showGroup) { this.showGroup =showGroup; }

    public int getGroupTextColor() { return groupTextColor; }
    public void setGroupTextColor(int groupTextColor) { this.groupTextColor = groupTextColor; }

    public int getGroupPercent() { return groupPercent; }
    public void setGroupPercent(int groupPercent) { this.groupPercent = groupPercent; }
}
