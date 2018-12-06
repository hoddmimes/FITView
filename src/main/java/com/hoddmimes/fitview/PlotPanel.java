package com.hoddmimes.fitview;

import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import org.jfree.chart.*;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.DateAxis;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PlotPanel extends JPanel
{
    private static final int PLOT_NONE = 0;
    private static final int PLOT_POWER = 1;
    private static final int PLOT_HEART = 2;
    private static final int PLOT_ASCENT = 3;
    private static final int PLOT_SPEED = 4;
    private static final int PLOT_VAM = 5;
    private static final int PLOT_ALTITUDE = 6;
    private static final int PLOT_PW_HR = 7;

    private static final long serialVersionUID = 1L;

    //Variables
    private JFreeChart           mChart;
    private RelativeDateFormat   mRelativeFormat;
    private  JPanel              mControlPanel;
    private ChartPanel            mChartPanel;

    private PlotSeriesData       mCurrentPlotType = null;
    TimeSeriesCollection         mSeriesCollection;
    SessionMesg                  mSessionMessage;


    java.util.List<RecordMesg>   mLogEntries;
    FITView.AppConfiguration     mAppCfg;


    PlotSeriesData mDataPower;
    PlotSeriesData mDataHeartRate;
    PlotSeriesData mDataSpeed;
    PlotSeriesData mDataElevation;
    PlotSeriesData mDataVAM;
    PlotSeriesData mDataPwHr;
    PlotSeriesData mDataEmpty;
    List<PlotSeriesData> mDataList;




    PlotPanel(FITView.AppConfiguration pAppCfg) {
        super( new BorderLayout());
        mLogEntries = null;

        mAppCfg = pAppCfg;

        mDataList = new ArrayList<>();
        mDataPower = new PlotSeriesData( true,"Power","Watt",new PowerEvaluator( mAppCfg.getPowerSmoothInterval()));
        mDataList.add( mDataPower );
        mDataHeartRate = new PlotSeriesData( true, "Heart Rate","BPM",new HeartRateEvaluator());
        mDataList.add( mDataHeartRate );
        mDataSpeed = new PlotSeriesData(true, "Speed","Km/h",new SpeedEvaluator());
        mDataList.add( mDataSpeed );
        mDataElevation = new PlotSeriesData(false, "Elevation","Meter",new ElevationEvaluator());
        mDataList.add( mDataElevation );
        mDataVAM = new PlotSeriesData( true, "VAM","Meter/h",new VAMEvaluator( mAppCfg.getVAMCalculateInterval()));
        mDataList.add( mDataVAM );
        mDataPwHr = new PlotSeriesData( true, "Power/HR","Watt/BPM",new PwHrEvaluator( mAppCfg.getPwHrInterval()));
        mDataList.add( mDataPwHr );
        mDataEmpty = new PlotSeriesData( false, null,null, null);

        setupControlPanel();
        setupChartPanel();
    }

    private JFreeChart setupChart( PlotSeriesData pData ) {
        mSeriesCollection = new TimeSeriesCollection();
        mSeriesCollection.addSeries(pData.getAverageSeries());
        mSeriesCollection.addSeries( pData.getSeriesData());


        mRelativeFormat.setBaseMillis(pData.findFirstNoneZeroValueTime());

        // Create chart
        mChart = ChartFactory.createXYBarChart(
                    "",    // Title
                    "",    // Time axis text
                    true,
                    "",    // Value axis text
                     mSeriesCollection,    // XY Dataset
                     PlotOrientation.VERTICAL,
                    false,    // Legend
                    true,    // Tooltips
                    false    // URLs
            );


        if (mChartPanel != null) {
            super.remove(mChartPanel);
        }

        mChartPanel = new ChartPanel(mChart);
        super.add(mChartPanel, BorderLayout.CENTER);
        super.validate();

        mChartPanel.setMouseZoomable(true, false);
        mChart.setBackgroundPaint(Color.lightGray);

        Object o = mChart.getPlot();
        XYPlot tPlot = (XYPlot) mChart.getPlot();
        tPlot.setDomainGridlinesVisible(false);
        tPlot.setRangeGridlinesVisible(true);
        tPlot.setDomainPannable(true);
        tPlot.setOrientation(PlotOrientation.VERTICAL);

        ValueAxis tXAxis = tPlot.getDomainAxis();
        tXAxis.setTickLabelsVisible(true);
        tXAxis.setTickMarksVisible(true);

        //XYLineAnnotation tLineAnnotation = pData.getAverageLine();
        //if (tLineAnnotation != null) {
        //    tPlot.addAnnotation( tLineAnnotation );
        // }

        tPlot.setRangeAxis(pData.getYLabel());

        XYLineAndShapeRenderer tRenderer = new XYLineAndShapeRenderer(true, false);
        tRenderer.setSeriesToolTipGenerator(0, new ToolTipGenerator());
        tRenderer.setSeriesToolTipGenerator(1, new ToolTipGenerator());

        DateAxis tAxis = (DateAxis) tPlot.getDomainAxis();
        tAxis.setDateFormatOverride(mRelativeFormat);

        tPlot.setRenderer(0, tRenderer);

        return mChart;
    }


    private void setupChartPanel()
    {
        mRelativeFormat = new RelativeDateFormat();
        mRelativeFormat.setShowZeroDays(false);
        mRelativeFormat.setShowZeroHours(false);
        mRelativeFormat.setHourSuffix(":");
        mRelativeFormat.setMinuteSuffix(":");
        mRelativeFormat.setSecondSuffix("");
        mRelativeFormat.setHourFormatter(createNumberFormat(2));
        mRelativeFormat.setMinuteFormatter(createNumberFormat(2));
        mRelativeFormat.setSecondFormatter(createNumberFormat(2));

        setupChart( mDataEmpty );


        //NumberAxis tPriceAxis = new NumberAxis("Price");
        //tPlot.setRangeAxis(0, tPriceAxis);

        this.repaint();
    }

    private NumberFormat createNumberFormat(int pDigits) {
        NumberFormat n = NumberFormat.getNumberInstance();
        n.setGroupingUsed(false);
        n.setMaximumFractionDigits(0);
        n.setMinimumIntegerDigits(pDigits);
        n.setMaximumIntegerDigits(pDigits);
        return n;
    }

    private void resetCheckBoxes( PlotSeriesData pPlotData ) {
        for( PlotSeriesData psd : mDataList ) {
            psd.getCheckBox().setSelected(false);
        }
        mCurrentPlotType = pPlotData;

        if (pPlotData != null) {
            pPlotData.getCheckBox().setSelected(true);
        }
    }

    private void clearData() {
        for( PlotSeriesData psd : mDataList ) {
            psd.clear();
        }
        mLogEntries = null;
    }

    void setData(java.util.List<RecordMesg> pLogEntries, SessionMesg pSessionMessage) {
        clearData();
        resetCheckBoxes(null);
        mLogEntries = pLogEntries;
        mSessionMessage = pSessionMessage;
        generateSeries();
    }


    private void setSerie( PlotSeriesData pPlotData) {
            resetCheckBoxes( pPlotData );
            setupChart( pPlotData );
            mChart.fireChartChanged();
            super.repaint();
        }




    private void generateSeries() {
        for( RecordMesg rm : mLogEntries) {
            for(PlotSeriesData psd : mDataList ) {
                psd.add( rm );
            }
        }
    }


    private void setupControlPanel() {
        mControlPanel = new JPanel( new BorderLayout());
        JPanel tCheckBoxPanel = new JPanel( new FlowLayout());
        JPanel tButtonPanelPanel = new JPanel( new FlowLayout());


        for( PlotSeriesData psd : mDataList ) {
            tCheckBoxPanel.add( psd.getCheckBox() );
            psd.getCheckBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  setSerie( psd );
                }
            });
        }



        mControlPanel.add( tCheckBoxPanel, BorderLayout.CENTER );

        // Create and setup button panel
        /**
        mResetButton = new JButton("Reset");
        mResetButton.setPreferredSize(new Dimension( 80,22));

        mResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Reset button");
            }
        });
        tButtonPanelPanel.add( mResetButton );
        mControlPanel.add( tButtonPanelPanel, BorderLayout.SOUTH );
         */

        // Add Control panel
        super.add( mControlPanel, BorderLayout.SOUTH);
    }


    private class ElevationEvaluator implements SeriesDataEvaluatorIf
    {
        double mTotAlt;
        double mLatestAlt;

        ElevationEvaluator() {
            mTotAlt = 0;
            mLatestAlt = -1;
        }

        @Override
        public double evaluateRecordMsgData( RecordMesg rm ) {
            int a = rm.getAltitude().intValue();
            if (mLatestAlt < 0) {
                mLatestAlt = a;
                return mTotAlt;
            }
            double v = a - mLatestAlt;
            mTotAlt += (v > 0) ? v : 0.0d;
            mLatestAlt = a;
            return mTotAlt;
        }

        @Override
        public String formatToolTipValue(Number pValue) {
            return String.valueOf( pValue.intValue() );
        }

        double getTotalAltitude() {
            return mTotAlt;
        }
    }




    class HeartRateEvaluator implements SeriesDataEvaluatorIf
    {
        @Override
        public double evaluateRecordMsgData(RecordMesg pRecMsg) {
            return pRecMsg.getHeartRate().doubleValue();
        }

        @Override
        public String formatToolTipValue(Number pValue) {
            return String.valueOf( pValue.intValue() );
        }
    }

    class SpeedEvaluator implements SeriesDataEvaluatorIf
    {
        @Override
        public double evaluateRecordMsgData(RecordMesg pRecMsg) {
            return (pRecMsg.getSpeed().doubleValue() * 3.6d);
        }

        @Override
        public String formatToolTipValue(Number pValue) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);
            return nf.format( pValue.doubleValue());
        }
    }

    class PowerEvaluator implements SeriesDataEvaluatorIf
    {
        private int mSmoothInterval;
        private LinkedList<Double> mList;

        PowerEvaluator( int pSmoothInterval ) {
            mSmoothInterval = pSmoothInterval;
            mList = new LinkedList<>();
        }

        @Override
        public String formatToolTipValue(Number pValue) {
            return String.valueOf( pValue.intValue() );
        }

        @Override
        public double evaluateRecordMsgData(RecordMesg pRecMsg) {
            double v = pRecMsg.getPower().doubleValue();
            mList.addLast(v);
            if (mList.size() > mSmoothInterval) {
                mList.removeFirst();
            }
            return mList.stream().mapToDouble( Double::doubleValue ).average().orElse(0.0d);
        }
    }

    class PwHrEvaluator implements SeriesDataEvaluatorIf {
        int mInterval;

        double mLatestValue,x,mSum;
        LinkedList<Double> mPower;
        LinkedList<Double> mHeartrate;


        PwHrEvaluator(int pInterval ) {
            mInterval = pInterval;
            mPower = new LinkedList<>();
            mHeartrate = new LinkedList<>();
        }

        @Override
        public String formatToolTipValue(Number pValue) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            return nf.format( pValue.doubleValue());
        }

        @Override
        public double evaluateRecordMsgData( RecordMesg rm ) {
            if (rm.getAltitude() == null) {
                return -1.0d;
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
                return -1.0d;
            }

            double p = mPower.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double h = mHeartrate.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            return (h > 0) ? (p/h) : -1.0d;
        }
    }


    class VAMEvaluator implements SeriesDataEvaluatorIf
    {
        int mInterval;
        int mTicks;
        double mLatestValue,x,mSum;


        VAMEvaluator( int pInterval ) {
            mInterval = pInterval;
            mSum = 0;
            mTicks = 0;
            mLatestValue = -1;
        }

        @Override
        public String formatToolTipValue(Number pValue) {
            return String.valueOf( pValue.intValue() );
        }

        @Override
        public double evaluateRecordMsgData( RecordMesg rm ) {
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


    private class ToolTipGenerator implements XYToolTipGenerator
    {
        @Override
        public String generateToolTip(XYDataset pDataSet, int pSeries, int pItem) {

            Number x = pDataSet.getX(pSeries, pItem ); // Time entry
            Number y = pDataSet.getY(pSeries, pItem ); // Time entry
            String  ts = mRelativeFormat.format( new Date( x.longValue()));

            return  "         " + ts + "  " + mCurrentPlotType.getToolTipPrefix( pSeries ) +
                    mCurrentPlotType.formatToolTipValue( y ) + "  " + mCurrentPlotType.getToolTipUnit();

        }
    }
}
