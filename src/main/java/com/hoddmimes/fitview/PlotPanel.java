package com.hoddmimes.fitview;

import com.garmin.fit.RecordMesg;
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Date;
import java.util.LinkedList;

public class PlotPanel extends JPanel
{
    private static final int PLOT_NONE = 0;
    private static final int PLOT_POWER = 1;
    private static final int PLOT_HEART = 2;
    private static final int PLOT_ASCENT = 3;
    private static final int PLOT_SPEED = 4;
    private static final int PLOT_VAM = 5;
    private static final int PLOT_ALTITUDE = 6;

    private static final long serialVersionUID = 1L;

    //Variables
    private JFreeChart  mChart;
    private RelativeDateFormat mRelativeFormat;
    private  JPanel     mControlPanel;
    private  ChartPanel mChartPanel;
    private  JCheckBox  mPowerCheckBox;
    private  JCheckBox  mHeartCheckBox;
    private  JCheckBox  mAscentCheckBox;
    private  JCheckBox  mSpeedCheckBox;
    private  JCheckBox  mVAMCheckBox;
    private  JCheckBox  mAltitudeCheckBox;
    private  int        mCurrentPlotType = PLOT_NONE;
    //private  JButton    mResetButton;

    TimeSeries    mPowerSeries;
    TimeSeries    mHeartSeries;
    TimeSeries    mAscentSeries;
    TimeSeries    mSpeedSeries;
    TimeSeries    mVAMSeries;
    TimeSeries    mAltitudeSeries;
    TimeSeriesCollection mSeriesCollection;

    java.util.List<RecordMesg> mLogEntries;
    long mFirstTimeEntry = 0;
    FITView.AppConfiguration mAppCfg;

    PlotPanel(FITView.AppConfiguration pAppCfg) {
        super( new BorderLayout());
        mLogEntries = null;

        mAppCfg = pAppCfg;

        setupControlPanel();
        setupChartPanel();
    }

    private JFreeChart setupChart( int pPlotType ) {


        mSeriesCollection = new TimeSeriesCollection();


        switch (pPlotType) {
            case PLOT_ASCENT:
                mSeriesCollection.addSeries(mAscentSeries);
                break;
            case PLOT_POWER:
                mSeriesCollection.addSeries(mPowerSeries);
                break;
            case PLOT_HEART:
                mSeriesCollection.addSeries(mHeartSeries);
                break;
            case PLOT_SPEED:
                mSeriesCollection.addSeries(mSpeedSeries);
                break;
            case PLOT_VAM:
                mSeriesCollection.addSeries(mVAMSeries);
                break;
            case PLOT_ALTITUDE:
                mSeriesCollection.addSeries(mAltitudeSeries);
                break;
        }

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

        switch (pPlotType) {
            case PLOT_NONE:
                tPlot.setRangeAxis(null);
                break;
            case PLOT_ASCENT:
                tPlot.setRangeAxis(new NumberAxis("Meter"));
                break;
            case PLOT_POWER:
                tPlot.setRangeAxis(new NumberAxis("Watt"));
                break;
            case PLOT_HEART:
                tPlot.setRangeAxis(new NumberAxis("Bpm"));
                break;
            case PLOT_SPEED:
                tPlot.setRangeAxis(new NumberAxis("Km/h"));
                break;
            case PLOT_VAM:
                tPlot.setRangeAxis(new NumberAxis("Vertical m/h"));
                break;
            case PLOT_ALTITUDE:
                tPlot.setRangeAxis(new NumberAxis("Meter"));
                break;
        }


        if (pPlotType == PLOT_NONE) {
            tPlot.setRangeAxis(null);
        }

        XYLineAndShapeRenderer tRenderer = new XYLineAndShapeRenderer(true, false);
        tRenderer.setSeriesToolTipGenerator(0, new ToolTipGenerator());

        DateAxis tAxis = (DateAxis) tPlot.getDomainAxis();
        tAxis.setDateFormatOverride(mRelativeFormat);

        tPlot.setRenderer(0, tRenderer);

        return mChart;
    }


    private void setupChartPanel()
    {
        mPowerSeries = new TimeSeries("Power");
        mHeartSeries = new TimeSeries("Heart Rate");
        mSpeedSeries = new TimeSeries("Speed");
        mAscentSeries = new TimeSeries("Ascent");
        mVAMSeries = new TimeSeries("VAM");
        mAltitudeSeries = new TimeSeries("Altitude");





        mRelativeFormat = new RelativeDateFormat();
        mRelativeFormat.setShowZeroDays(false);
        mRelativeFormat.setShowZeroHours(false);
        mRelativeFormat.setHourSuffix(":");
        mRelativeFormat.setMinuteSuffix(":");
        mRelativeFormat.setSecondSuffix("");
        mRelativeFormat.setHourFormatter(createNumberFormat(2));
        mRelativeFormat.setMinuteFormatter(createNumberFormat(2));
        mRelativeFormat.setSecondFormatter(createNumberFormat(2));

        setupChart( PLOT_NONE );


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

    private void resetCheckBoxes( int pPlotType ) {
        mPowerCheckBox.setSelected( false );
        mHeartCheckBox.setSelected( false );
        mAscentCheckBox.setSelected( false );
        mSpeedCheckBox.setSelected( false );
        mVAMCheckBox.setSelected( false );
        mAltitudeCheckBox.setSelected( false );
        switch( pPlotType ) {
            case PLOT_ALTITUDE:
                mAltitudeCheckBox.setSelected( true );
                break;
            case PLOT_ASCENT:
                mAscentCheckBox.setSelected( true );
                break;
            case PLOT_HEART:
                mHeartCheckBox.setSelected( true );
                break;
            case PLOT_POWER:
                mPowerCheckBox.setSelected( true );
                break;
            case PLOT_SPEED:
                mSpeedCheckBox.setSelected( true );
                break;
            case PLOT_VAM:
                mVAMCheckBox.setSelected( true );
                break;
        }
    }

    private void clearData() {
        mPowerSeries.clear();
        mHeartSeries.clear();
        mAscentSeries.clear();
        mSpeedSeries.clear();
        mVAMSeries.clear();
        mAltitudeSeries.clear();
        mLogEntries = null;
    }

    void setData(java.util.List<RecordMesg> pLogEntries) {
        clearData();
        resetCheckBoxes(PLOT_NONE);
        mLogEntries = pLogEntries;
        generateSeries();
    }


    private void setSerie( int pPlotType) {
            resetCheckBoxes( pPlotType );
            setupChart( pPlotType );
            mCurrentPlotType = pPlotType;
            mChart.fireChartChanged();
            super.repaint();
        }




    private void generateSeries() {
        mFirstTimeEntry = (mLogEntries.get(0).getTimestamp().getTimestamp() * 1000L);
        mRelativeFormat.setBaseMillis( mFirstTimeEntry );
        PowerSmoother tPwr = new PowerSmoother( mAppCfg.getPowerSmoothInterval()); // Smooth power over 4 sec
        AltitudeAccumulator tAlt = new AltitudeAccumulator();
        VAMCalculator   tVAM = new VAMCalculator( mAppCfg.getVAMCalculateInterval() );


        for( RecordMesg rm : mLogEntries) {
            Millisecond tTime = new Millisecond( new Date(rm.getTimestamp().getTimestamp() * 1000L));
            mPowerSeries.add(tTime, tPwr.add(rm));
            mAscentSeries.add( tTime, tAlt.add(rm) );
            mAltitudeSeries.add( tTime, rm.getAltitude().intValue());
            mSpeedSeries.add( tTime, (rm.getSpeed() * 3.6d));
            mHeartSeries.add( tTime, rm.getHeartRate().intValue());
            double tVAMValue = tVAM.add(rm);
            if (tVAMValue >= 0) {
                //mVAMSeries.advanceTime();
                mVAMSeries.add( tTime, tVAMValue);
            }
        }

    }


    private void setupControlPanel() {
        mControlPanel = new JPanel( new BorderLayout());
        JPanel tCheckBoxPanel = new JPanel( new FlowLayout());
        JPanel tButtonPanelPanel = new JPanel( new FlowLayout());

        // Create and add check boxes
        mPowerCheckBox = new JCheckBox("Power", false);
        mPowerCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Power Check Box ");
                setSerie( PLOT_POWER );
            }
        });
        tCheckBoxPanel.add( mPowerCheckBox );

        mHeartCheckBox = new JCheckBox("Heart Rate", false);
        mHeartCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Heart Rate Check Box ");
                setSerie( PLOT_HEART );
            }
        });
        tCheckBoxPanel.add( mHeartCheckBox );

        mSpeedCheckBox = new JCheckBox("Speed", false);
        mSpeedCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Speed Check Box ");
                setSerie( PLOT_SPEED);
            }
        });
        tCheckBoxPanel.add( mSpeedCheckBox );

        mVAMCheckBox = new JCheckBox("VAM", false);
        mVAMCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("VAM Check Box ");
                setSerie( PLOT_VAM);
            }
        });
        tCheckBoxPanel.add( mVAMCheckBox );

        mAltitudeCheckBox = new JCheckBox("Altitude", false);
        mAltitudeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Altitude Check Box ");
                setSerie( PLOT_ALTITUDE);
            }
        });
        tCheckBoxPanel.add( mAltitudeCheckBox );



        mAscentCheckBox = new JCheckBox("Ascent", false);
        mAscentCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Ascent Check Box ");
                setSerie( PLOT_ASCENT);
            }
        });
        tCheckBoxPanel.add( mAscentCheckBox );

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


    private class AltitudeAccumulator
    {
        int mTotAlt;
        int mLatestAlt;

        AltitudeAccumulator() {
            mTotAlt = 0;
            mLatestAlt = -1;
        }

        int add( RecordMesg rm ) {
            int a = rm.getAltitude().intValue();
            if (mLatestAlt < 0) {
                mLatestAlt = a;
                return mTotAlt;
            }
            int v = a - mLatestAlt;
            mTotAlt += (v > 0) ? v : 0;
            mLatestAlt = a;
            return mTotAlt;
        }

        int getTotalAltitude() {
            return mTotAlt;
        }
    }


    private class PowerSmoother
    {
        int mSmoothInterval;
        LinkedList<Integer> mList;

        PowerSmoother( int pSmoothInterval ) {
            mSmoothInterval = pSmoothInterval;
            mList = new LinkedList<>();
        }

        private int avg() {
            double tSum = 0;
            for( Integer i : mList ) {
                tSum += (double) i;
            }
            double d = tSum / (double) mList.size();
            return (int) Math.round(d);
        }

        int add( RecordMesg pRecMsg ) {
            int v = pRecMsg.getPower().intValue();
            mList.addLast(v);
            if (mList.size() > mSmoothInterval) {
                mList.removeFirst();
            }
            return avg();
        }

    }

    private class ToolTipGenerator implements XYToolTipGenerator
    {
        @Override
        public String generateToolTip(XYDataset pDataSet, int pSeries, int pItem) {

            Number x = pDataSet.getX(pSeries, pItem ); // Time entry
            Number y = pDataSet.getY(pSeries, pItem ); // Time entry
            String  ts = mRelativeFormat.format( new Date( x.longValue()));
           switch ( mCurrentPlotType ) {
               case PLOT_ALTITUDE:
                   return ts + " altitude: " + String.valueOf( y.intValue() ) + " m";
               case PLOT_ASCENT:
                   return ts + " total ascent: " + String.valueOf( y.intValue() ) + " m";
               case PLOT_SPEED:
                   return ts + " speed: " + String.valueOf( y.intValue() ) + " km/h";
               case PLOT_HEART:
                   return ts + " heart rate: " + String.valueOf( y.intValue() ) + " bpm";
               case PLOT_POWER:
                   return ts + " power: " + String.valueOf( y.intValue() ) + " W";
               case PLOT_VAM:
                   return ts + " VAM: " + String.valueOf( y.intValue() ) + " m/h";
           }
            /*
            if (pDataSet instanceof XYSeriesCollection) {
                XYSeriesCollection tXYCollection = (XYSeriesCollection) pDataSet;
                XYSeries tSeries = (XYSeries) tXYCollection.getSeries().get(pSeries);
                PriceDataItem tItem = (PriceDataItem) tSeries.getDataItem(pItem);
                return cSDFDate.format(tItem.mDay.mOpenTime) + " price: " + tItem.getYValue();
            }
            */
            return "Series: " + pSeries + " Item: " + pItem;
        }
    }
}
