package org.mposolda;


import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.TextAnchor;

public class LineChart2 { // TODO
// public class LineChart2 extends ApplicationFrame {


    public static String genLineChart()throws Exception{
        // visit statistics
        TimeSeries timeSeries=new TimeSeries("A Site Traffic Statistics", Month.class);
        // adding data
        timeSeries.add(new Month(1,2013), 100);
        timeSeries.add(new Month(2,2013), 200);
        timeSeries.add(new Month(3,2013), 300);
        timeSeries.add(new Month(4,2013), 400);
        timeSeries.add(new Month(5,2013), 560);
        timeSeries.add(new Month(6,2013), 600);
        timeSeries.add(new Month(7,2013), 750);
        timeSeries.add(new Month(8,2013), 890);
        timeSeries.add(new Month(9,2013), 120);
        timeSeries.add(new Month(10,2013), 400);
        timeSeries.add(new Month(11,2013), 1200);
        timeSeries.add(new Month(12,2013), 1600);
        // visit statistics
        TimeSeries timeSeries2=new TimeSeries("B Site Traffic Statistics", Month.class);
        // adding data
        timeSeries2.add(new Month(1,2013), 50);
        timeSeries2.add(new Month(2,2013), 100);
        timeSeries2.add(new Month(3,2013), 150);
        timeSeries2.add(new Month(4,2013), 200);
        timeSeries2.add(new Month(5,2013), 220);
        timeSeries2.add(new Month(6,2013), 300);
        timeSeries2.add(new Month(7,2013), 340);
        timeSeries2.add(new Month(8,2013), 400);
        timeSeries2.add(new Month(9,2013), 450);
        timeSeries2.add(new Month(10,2013), 500);
        timeSeries2.add(new Month(11,2013), 70);
        timeSeries2.add(new Month(12,2013), 800);
    // Define a collection of time series
        TimeSeriesCollection lineDataset=new TimeSeriesCollection();
        lineDataset.addSeries(timeSeries);
        lineDataset.addSeries(timeSeries2);
        JFreeChart chart= ChartFactory.createTimeSeriesChart("Visit Statistics Timeline", "Month", "Visit", lineDataset, true, true, true);
    // Set the main title
        chart.setTitle(new TextTitle("A,B website traffic comparison chart", new Font("Lishu", Font.ITALIC, 15)));
    // Set the subtitle
        TextTitle subtitle = new TextTitle("2013", new Font("Bold", Font.BOLD, 12));
        chart.addSubtitle(subtitle);
        chart.setAntiAlias(true);
        //Set the range of the timeline.
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis dateaxis = (DateAxis)plot.getDomainAxis();
        dateaxis.setDateFormatOverride(new java.text.SimpleDateFormat("M "));
        dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH,1));
    // Set whether the curve shows data points
        XYLineAndShapeRenderer xylinerenderer = (XYLineAndShapeRenderer)plot.getRenderer();
        xylinerenderer.setBaseShapesVisible(true);
    // Set the curve to display the value of each data point
        XYItemRenderer xyitem = plot.getRenderer();
        xyitem.setBaseItemLabelsVisible(true);
        xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 12));
        plot.setRenderer(xyitem);

        //String fileName= ServletUtilities.saveChartAsPNG(chart, 700, 500, session);
        //return fileName;
        // TODO
        return null;
    }

    // TODO
//    @Override
//    public void setContentPane(Container contentPane) {
//        super.setContentPane(contentPane);
//        System.out.println("ahoj");
//    }
}

