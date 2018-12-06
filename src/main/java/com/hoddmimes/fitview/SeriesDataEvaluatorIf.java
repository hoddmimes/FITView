package com.hoddmimes.fitview;

import com.garmin.fit.RecordMesg;

public interface SeriesDataEvaluatorIf
{
    public double evaluateRecordMsgData(RecordMesg pRecMsg );

    public String formatToolTipValue( Number pValue);
}
