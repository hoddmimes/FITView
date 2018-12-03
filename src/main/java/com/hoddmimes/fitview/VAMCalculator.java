package com.hoddmimes.fitview;

import com.garmin.fit.RecordMesg;

class VAMCalculator
{
    int mInterval;
    int mTicks;
    double mLatestValue,x,mSum;


    VAMCalculator( int pInterval ) {
        mInterval = pInterval;
        mSum = 0;
        mTicks = 0;
        mLatestValue = -1;
    }

    double add( RecordMesg rm ) {
        if (rm.getAltitude() == null) {
            return -1;
        }

        if (mLatestValue < 0) {
            mLatestValue = rm.getAltitude().doubleValue();
            return -1;
        }

        x = rm.getAltitude().doubleValue() - mLatestValue;
        mLatestValue = rm.getAltitude().doubleValue();
        mSum += (x > 0) ? x : 0;

        mTicks++;
        if ((mTicks % mInterval) == 0)
        {
            x = mSum * (3600.0d / (double) mInterval);
            mSum = 0;
            return x;
        }
        return -1;
    }
}
