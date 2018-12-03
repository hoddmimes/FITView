package com.hoddmimes.fitview;

import org.jfree.data.time.*;

import java.util.Date;

public class IrregularTime extends Millisecond
{
    private long mInterval;

    private static Date alignPeriod( Date pDate, long pInterval ) {
        long x = pDate.getTime() / pInterval;
        long r = pDate.getTime() - (pDate.getTime() * pInterval);
        if (r > (pInterval/2)) {
            return new Date( x + pInterval );
        } else {
            return new Date( x );
        }
    }

    public IrregularTime( long pInterval ) {
        super(alignPeriod( new Date(), pInterval));
        mInterval = pInterval;
    }

    public IrregularTime( Date pDate, long pInterval ) {
        super(alignPeriod( pDate, pInterval));
        mInterval = pInterval;
    }

    public IrregularTime( long pTimestamp, long pInterval ) {
        super(alignPeriod( new Date( pTimestamp ), pInterval));
        mInterval = pInterval;
    }

    @Override
    public RegularTimePeriod previous() {
        long t = super.getMillisecond();
        return new IrregularTime(new Date(t - mInterval), mInterval);
    }

    @Override
    public RegularTimePeriod next() {
        long t = super.getMillisecond();
        return new IrregularTime(new Date(t + mInterval), mInterval);
    }

    @Override
    public long getSerialIndex() {
        long t = super.getMillisecond();
        return (t/mInterval);
    }

    @Override
    public boolean equals(Object pObj) {
        if (pObj == this) {
            return false;
        }
        if (!(pObj instanceof  IrregularTime)) {
            return false;
        }
        IrregularTime it = (IrregularTime) pObj;
        if (it.getMillisecond() != super.getMillisecond()) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object pObj) {
       if (pObj instanceof RegularTimePeriod) {
           RegularTimePeriod rt = (RegularTimePeriod) pObj;
           if (super.getMillisecond() < rt.getFirstMillisecond()) {
               return -1;
           } else if (super.getMillisecond() > rt.getFirstMillisecond()) {
               return 1;
           }
           return 0;
       }
       return 1;
    }
}
