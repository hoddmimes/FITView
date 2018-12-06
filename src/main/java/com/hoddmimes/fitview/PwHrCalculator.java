package com.hoddmimes.fitview;

import com.garmin.fit.RecordMesg;

import java.util.LinkedList;

class PwHrCalculator
{
    int mInterval;
    int mTicks;
    double mLatestValue,x,mSum;
    LinkedList<Double> mPower;
    LinkedList<Double> mHeartrate;


    PwHrCalculator(int pInterval ) {
        mInterval = pInterval;
        mPower = new LinkedList<>();
        mHeartrate = new LinkedList<>();
        mTicks = 0;
        mLatestValue = -1;
    }

    double add( RecordMesg rm ) {
        if (rm.getAltitude() == null) {
            return -1;
        }

        mPower.addLast( rm.getPower().doubleValue());
        mHeartrate.addLast( rm.getHeartRate().doubleValue());
        if (mPower.size() > mInterval) {
            mPower.removeFirst();
        }
        if (mHeartrate.size() > mInterval) {
            mHeartrate.removeFirst();
        }

        if (mPower.size() < mInterval) {
            return 0.0d;
        }

        double p = mPower.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double h = mHeartrate.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        return (h > 0) ? (p/h) : 0;
    }
}
