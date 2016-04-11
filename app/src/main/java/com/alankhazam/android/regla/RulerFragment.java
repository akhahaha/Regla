package com.alankhazam.android.regla;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment displaying a ruler.
 */
public class RulerFragment extends Fragment {
    private View rootView;
    private ListView rulerListView;
    private ListView rulerValueView;

    public RulerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ruler, container, false);

        rulerListView = (ListView) rootView.findViewById(R.id.rulerListView);
        rulerValueView = (ListView) rootView.findViewById(R.id.rulerValueList);

        final int heightDPI = (int) getResources().getDisplayMetrics().ydpi;
        ViewGroup.LayoutParams segmentParams = rulerListView.getLayoutParams();
        segmentParams.width = (int) (heightDPI * 0.4);
        rulerListView.setLayoutParams(segmentParams);


        // Add listener to draw ruler after rootView has been drawn in order to obtain proper height
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (rulerListView.getHeight() == 0) {
                            // Draw initial ruler with rootView height
                            rulerListView.setAdapter(
                                    new RulerAdapter(getActivity(), R.layout.listitem_ruler_segment,
                                            rootView.getHeight(), heightDPI));
                        } else {
                            // Remove listener RulerListView has been drawn once
                            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            // Redraw ruler to maximum length
                            rulerListView.setAdapter(
                                    new RulerAdapter(getActivity(), R.layout.listitem_ruler_segment,
                                            rulerListView.getHeight(), heightDPI));
                            rulerValueView.setAdapter(
                                    new RulerValueAdapter(getActivity(), R.layout.listitem_ruler_value,
                                            rulerListView.getHeight(), heightDPI));
                        }
                    }
                });

        return rootView;
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

        private int numSegments; // Number of 1/16-inch segments
        private int segmentPixels;

        private int baseMarkerThickness = 1; // Base segment marker width in dp
        private int baseMarkerLength = 10; // Base segment marker length in dp
        private float baseMarkerTextSize = 10;

        /**
         * Constructor
         *
         * @param context  The current context.
         * @param resource The resource ID for a layout file containing a TextView to use when.
         * @param pixels   The length of the ruler in pixels.
         * @param dpi      The relevant DPI.
         */
        public RulerAdapter(Context context, int resource, int pixels, int dpi) {
            super(context, resource);

            // Calculate the pixels per 1/16-inch segment
            this.segmentPixels = dpi / 16;

            // Calculate the necessary number of segments
            this.numSegments = pixels / segmentPixels;

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

            // Set segment length to 1/16-inch
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
                markerText = ""; // Draw text with independent ruler marker
            } else if (position % 8 == 0) {
                // Half inch
                multiplier = HALF_INCH_MULTIPLIER;
                markerText = ""; // Draw text with indepedent ruler marker
            } else if (position % 4 == 0) {
                // Quarter inch
                multiplier = QUARTER_INCH_MULTIPLIER;
                markerText = position / 4 % 4 + "/4";
                markerTextView.setTypeface(null, Typeface.BOLD);
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

    /**
     * Adapter to convert a vertical ListView into a RulerView.
     */
    private class RulerValueAdapter extends ArrayAdapter<Integer> {
        private int numSegments; // Number of 1/2-inch segments
        private int segmentPixels;

        /**
         * Constructor
         *
         * @param context  The current context.
         * @param resource The resource ID for a layout file containing a TextView to use when.
         * @param pixels   The length of the ruler in pixels.
         * @param dpi      The relevant DPI.
         */
        public RulerValueAdapter(Context context, int resource, int pixels, int dpi) {
            super(context, resource);

            // Calculate the pixels per 1/2-inch segment
            this.segmentPixels = dpi / 2;

            // Calculate the necessary number of segments
            this.numSegments = pixels / segmentPixels;
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
                convertView = inflater.inflate(R.layout.listitem_ruler_value, parent, false);
            }

            // Set segment length to 1/2-inch
            ViewGroup.LayoutParams segmentParams = convertView.getLayoutParams();
            segmentParams.height = segmentPixels;
            convertView.setLayoutParams(segmentParams);

            // Customize segment display
            TextView markerTextView = ((TextView) convertView.findViewById(R.id.segmentValue));
            String markerText;
            markerTextView.setTypeface(null, Typeface.BOLD);
            int markerTextSize;
            if (position % 2 == 0) {
                // Whole inch
                markerText = Integer.toString(position / 2);
                markerTextView.setTypeface(null, Typeface.BOLD);
                markerTextSize = 10;
            } else {
                // Half inch
                markerText = "1/2";
                markerTextView.setTypeface(null, Typeface.NORMAL);
                markerTextSize = 8;
            }

            // Apply segment properties
            markerTextView.setText(markerText);
            markerTextView.setTextSize(markerTextSize);

            return convertView;
        }
    }
}
