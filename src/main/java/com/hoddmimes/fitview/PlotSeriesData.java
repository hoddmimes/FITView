package com.hoddmimes.fitview;

import com.garmin.fit.RecordMesg;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import javax.swing.*;
import java.util.Date;
import java.util.List;

public class PlotSeriesData
{
    private TimeSeries            mSeriesData;
    private JCheckBox             mCheckBox;
    private SimpleRegression      mRegression;
    private SeriesDataEvaluatorIf mEvaluator;
    private String                mYLabel;
    private boolean               mPlotAvgSeries;

    PlotSeriesData( boolean pPlotAvgSeries, String pChkBoxLbl, String pYLabel, SeriesDataEvaluatorIf pEvalutor ) {
        mEvaluator = pEvalutor;
        mCheckBox = new JCheckBox( pChkBoxLbl );
        mCheckBox.setSelected( false );
        mRegression = new SimpleRegression();
        mSeriesData = new TimeSeries("");
        mYLabel = pYLabel;
        mPlotAvgSeries = pPlotAvgSeries;
    }

    public String formatToolTipValue( Number pValue ) {
        return mEvaluator.formatToolTipValue( pValue );
    }

    public String getToolTipPrefix( int pSeries  ) {
        return (pSeries == 0) ? "Avg " + mCheckBox.getText() + " : " : mCheckBox.getText() + " : ";
    }

    public String getToolTipUnit() {
        return mYLabel;
    }


    public JCheckBox getCheckBox() {
        return mCheckBox;
    }

    public void add( RecordMesg pRecMsg ) {
        Millisecond tMs = new Millisecond( new Date(pRecMsg.getTimestamp().getTimestamp() * 1000L));
        double tValue = mEvaluator.evaluateRecordMsgData( pRecMsg );
        if (tValue >= 0.0d ) {
            mSeriesData.add( tMs, tValue );
            mRegression.addData( tMs.getFirstMillisecond(), tValue );
        }
    }

    public void clear() {
        mCheckBox.setSelected( false );
        mSeriesData.clear();
        mRegression.clear();
    }

    public TimeSeries getSeriesData() {
        return mSeriesData;
    }

    public NumberAxis getYLabel() {
        return  (mYLabel == null) ? null : new NumberAxis( mYLabel );
    }

    public long findFirstNoneZeroValueTime() {
        for(TimeSeriesDataItem tItm : (List<TimeSeriesDataItem>) mSeriesData.getItems()) {
            if (tItm.getValue().doubleValue() > 0) {
                return tItm.getPeriod().getFirstMillisecond();
            }
        }
        return 0;
    }

    private long findLastNoneZeroValueTime() {
        int tLastIndex = mSeriesData.getItemCount() - 1;
        for( int i = tLastIndex; i >= 0; i-- ) {
            TimeSeriesDataItem tItm = mSeriesData.getDataItem( i );
            if (tItm.getValue().doubleValue() > 0) {
                return tItm.getPeriod().getFirstMillisecond();
            }
        }
        return 0;
    }


    private double calculateAverage() {
        double tSum = 0, v = 0;
        int tCount = 0;
        for(TimeSeriesDataItem tItm : (List<TimeSeriesDataItem>) mSeriesData.getItems()) {
           v = tItm.getValue().doubleValue();
           if (v > 0) {
               tSum += tItm.getValue().doubleValue();
               tCount++;
           }
        }
        return  tSum / (double) tCount;
    }

    public TimeSeries getAverageSeries() {
        double tSum = 0;
        int tCount = 0;

        TimeSeries tAvgSeries = new TimeSeries("");
        if ((mSeriesData.getItemCount() == 0) || (!mPlotAvgSeries)) {
            return tAvgSeries;
        }


        for( TimeSeriesDataItem tItm : (List<TimeSeriesDataItem>) mSeriesData.getItems()) {
            if (tItm.getValue().doubleValue() > 0) {
                tSum += tItm.getValue().doubleValue();
                tCount++;
                tAvgSeries.add( tItm.getPeriod(), (double) (tSum / (double) tCount) );
            }
        }

        return tAvgSeries;

        /**
        double tSlope = mRegression.getSlope();
        double tAvg = calculateAverage();

        long tFirstTS = findFirstNoneZeroValueTime();
        long tLastTS = findLastNoneZeroValueTime();


        double y1 = (tSlope > 0) ? tAvg + (tSlope * ((tLastTS - tFirstTS) / 2)) : tAvg - (tSlope * ((tLastTS - tFirstTS) / 2));
        double y2 = (tSlope > 0) ? tAvg - (tSlope * ((tLastTS - tFirstTS) / 2)) : tAvg + (tSlope * ((tLastTS - tFirstTS) / 2));

        XYLineAnnotation tAnnotation = new XYLineAnnotation((double) tFirstTS, y1, (double) tLastTS, y2);
        return tAnnotation;
         **/
    }
}
