package com.titanarchers;

public class ArrowPoint {
    public int score;
    public int color;
    public float x;
    public float y;


    private ArrowPoint(int score, int color, float x, float y) {
        this.score = score;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public static ArrowPointBuilder builder(){
        return new ArrowPointBuilder();
    }

    public static class ArrowPointBuilder {
        private int score;
        private int color;
        private float x;
        private float y;

        public ArrowPointBuilder setScore(int score) {
            this.score = score;
            return this;
        }

        public ArrowPointBuilder setColor(int color) {
            this.color = color;
            return this;
        }

        public ArrowPointBuilder setX(float x) {
            this.x = x;
            return this;
        }

        public ArrowPointBuilder setY(float y) {
            this.y = y;
            return this;
        }

        public ArrowPoint build() {
            return new ArrowPoint(score, color, x, y);
        }
    }

    @Override
    public String toString() {
        return "ArrowPoint{" +
                "score" + score +
                ", color='" + color + '\'' +
                ", x='" + x + '\'' +
                ", y=" + y +
                '}';
    }

}
