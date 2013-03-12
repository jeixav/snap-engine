package org.esa.beam.statistics.percentile.interpolated;

import org.esa.beam.apache.math3.Interpolator;
import org.esa.beam.apache.math3.InterpolatorFactory;
import org.esa.beam.apache.math3.PolynomialSplineFunction;

import java.util.ArrayList;

public class GapFiller {

    public static void fillGaps(float[] interpolationFloats, final String interpolationMethod, final float startValueFallback, final float endValueFallback) {
        fillStartAndEndWithFallback(interpolationFloats, startValueFallback, endValueFallback);

        ArrayList<Double> xList = new ArrayList<Double>();
        ArrayList<Double> yList = new ArrayList<Double>();

        for (int i = 0; i < interpolationFloats.length; i++) {
            float value = interpolationFloats[i];
            if (!Float.isNaN(value)) {
                xList.add((double) i);
                yList.add((double) value);
            }
        }

        double[] nx = new double[xList.size()];
        double[] ny = new double[yList.size()];

        for (int i = 0; i < xList.size(); i++) {
            nx[i] = xList.get(i);
            ny[i] = yList.get(i);
        }

        PolynomialSplineFunction interpolate = null;
        Interpolator interpolator = InterpolatorFactory.createInterpolator(interpolationMethod);
        if (interpolator != null) {
            interpolate = interpolator.interpolate(nx, ny);
        }
        for (int i = 0; i < interpolationFloats.length; i++) {
            interpolationFloats[i] = (float) interpolate.value(i);
        }
    }

    public static void fillStartAndEndWithFallback(float[] interpolationFloats, final float startValueFallback, final float endValueFallback) {
        if (Float.isNaN(interpolationFloats[0])) {
            interpolationFloats[0] = startValueFallback;
        }
        final int lastIdx = interpolationFloats.length - 1;
        if (Float.isNaN(interpolationFloats[lastIdx])) {
            interpolationFloats[lastIdx] = endValueFallback;
        }
    }

}
