package com.neatorobotics.android.slide.framework.robot.settings;

public class CleaningSettings {
    private int mSpotAreaLength;
    private int mSpotAreaHeight;
    private int mCleaningCategory;

    public int getSpotAreaLength() {
        return mSpotAreaLength;
    }

    public void setSpotAreaLength(int spotAreaLength) {
        mSpotAreaLength = spotAreaLength;
    }

    public int getSpotAreaHeight() {
        return mSpotAreaHeight;
    }

    public void setSpotAreaHeight(int spotAreaHeight) {
        mSpotAreaHeight = spotAreaHeight;
    }

    public int getCleaningCategory() {
        return mCleaningCategory;
    }

    public void setCleaningCategory(int CleaningCategory) {
        mCleaningCategory = CleaningCategory;
    }
}
