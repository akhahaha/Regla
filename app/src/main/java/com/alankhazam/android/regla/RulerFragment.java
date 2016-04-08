package com.alankhazam.android.regla;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment displaying a ruler.
 */
public class RulerFragment extends Fragment {
    private int widthSegmentPixels;
    private int widthNumSegments;
    private int heightSegmentPixels;
    private int heightNumSegments;

    public RulerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ruler, container, false);

        calculateRulerValues();

        return view;
    }

    /**
     * Calculate necessary ruler values using screen length and DPI
     */
    private void calculateRulerValues() {
        // Calculate the pixels per 1/16th inch segment
        widthSegmentPixels = (int) (getWidthDPI() / 16);
        heightSegmentPixels = (int) (getHeightDPI() / 16);

        // Calculate the number of 1/16th inch segments that will fit
        widthNumSegments = getWidthPixels() / widthSegmentPixels;
        heightNumSegments = getHeightPixels() / heightSegmentPixels;
    }

    private float getWidthDPI() {
        return getResources().getDisplayMetrics().xdpi;
    }

    private int getWidthPixels() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private float getHeightDPI() {
        return getResources().getDisplayMetrics().ydpi;
    }

    private int getHeightPixels() {
        return getResources().getDisplayMetrics().heightPixels;
    }
}
