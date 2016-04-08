package com.alankhazam.android.regla;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        // Display ruler
        ((ListView) view.findViewById(R.id.rulerListView)).setAdapter(
                new RulerAdapter(getActivity(), R.layout.listitem_ruler_segment,
                        heightNumSegments, heightSegmentPixels));

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

    /**
     * Adapter to convert a vertical ListView into a RulerView.
     */
    private class RulerAdapter extends ArrayAdapter<Integer> {
        private final double WHOLE_INCH_MULTIPLIER = 6;
        private final double HALF_INCH_MULTIPLIER = 5;
        private final double QUARTER_INCH_MULTIPLIER = 4;
        private final double EIGHTH_INCH_MULTIPLIER = 3;
        private final double SIXTEENTH_INCH_MULTIPLIER = 2;

        private int numSegments; // Number of 1/16th inch segments
        private int segmentPixels;

        private int baseMarkerThickness = 1; // Base segment marker width in dp
        private int baseMarkerLength = 10; // Base segment marker length in dp
        private float baseMarkerTextSize = 10;

        /**
         * Constructor
         *
         * @param context  The current context.
         * @param resource The resource ID for a layout file containing a TextView to use when
         */
        public RulerAdapter(Context context, int resource, int numSegments, int segmentPixels) {
            super(context, resource);
            this.numSegments = numSegments;
            this.segmentPixels = segmentPixels;

            baseMarkerThickness = segmentPixels / 4;
            baseMarkerLength = segmentPixels;
            baseMarkerTextSize = segmentPixels / 5;
        }

        @Override
        public int getCount() {
            return numSegments;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Create new convertView if necessary
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listitem_ruler_segment, parent, false);
            }

            // Set segment length to 1/16th inch
            ViewGroup.LayoutParams segmentParams = convertView.getLayoutParams();
            segmentParams.height = segmentPixels;
            convertView.setLayoutParams(segmentParams);

            // Customize segment display
            double multiplier;
            ViewGroup.LayoutParams markerParams =
                    convertView.findViewById(R.id.segmentMarker).getLayoutParams();
            TextView markerTextView = ((TextView) convertView.findViewById(R.id.segmentValue));
            String markerText;
            float markerTextSize = (float) (baseMarkerTextSize * 0.75);
            if (position % 16 == 0) {
                // Whole inch
                multiplier = WHOLE_INCH_MULTIPLIER;
                markerText = Integer.toString(position / 16);
                markerTextView.setTypeface(null, Typeface.BOLD);
                markerTextSize = baseMarkerTextSize;
            } else if (position % 8 == 0) {
                // Half inch
                multiplier = HALF_INCH_MULTIPLIER;
                markerText = "1/2";
            } else if (position % 4 == 0) {
                // Quarter inch
                multiplier = QUARTER_INCH_MULTIPLIER;
                markerText = position / 4 % 4 + "/4";
            } else if (position % 2 == 0) {
                // Eighth inch
                multiplier = EIGHTH_INCH_MULTIPLIER;
                markerText = position / 2 % 8 + "/8";
            } else {
                // Sixteenth inch
                multiplier = SIXTEENTH_INCH_MULTIPLIER;
                markerText = "";
            }

            // Apply segment properties
            markerParams.height = baseMarkerThickness;
            markerParams.width = (int) (baseMarkerLength * multiplier);
            convertView.findViewById(R.id.segmentMarker).setLayoutParams(markerParams);
            markerTextView.setText(markerText);
            markerTextView.setTextSize(markerTextSize);

            return convertView;
        }
    }
}
