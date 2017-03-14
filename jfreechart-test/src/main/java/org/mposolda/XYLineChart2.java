package org.mposolda;

import java.awt.*;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class XYLineChart2 extends ApplicationFrame {

    public XYLineChart2( String applicationTitle, String chartTitle )
    {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);


        ChartPanel chartPanel = new ChartPanel( xylineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );

        final XYPlot plot = xylineChart.getXYPlot( );
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesPaint( 0 , Color.RED );
        renderer.setSeriesPaint( 1 , Color.GREEN );
        renderer.setSeriesPaint( 2 , Color.YELLOW );
        renderer.setSeriesPaint( 3 , Color.BLUE );

        renderer.setSeriesStroke(0, new BasicStroke(5.0f));
        renderer.setSeriesStroke( 1 , new BasicStroke( 2.0f ) );
        renderer.setSeriesStroke( 2 , new BasicStroke( 2.0f ) );
        renderer.setSeriesStroke( 3 , new BasicStroke( 5.0f ) );

        plot.setRenderer( renderer );



        setContentPane( chartPanel );
    }

    private XYDataset createDataset( )
    {
        FileParser parser = new FileParserImpl();
        Map<String, Map<Integer, Integer>>  data = parser.getData();

        final XYSeriesCollection dataset = new XYSeriesCollection( );

        for (String description : data.keySet()) {
            final XYSeries firefox = new XYSeries( description );
            Map<Integer, Integer> values = data.get(description);
            for (int x : values.keySet()) {
                int y = values.get(x);
                firefox.add(x, y);
            }

            dataset.addSeries(firefox);
        }

        /*
        final XYSeries firefox = new XYSeries( "Firefox" );
        firefox.add( 1.0 , 1.0 );
        firefox.add( 2.0 , 4.0 );
        firefox.add( 3.0 , 3.0 );
        final XYSeries chrome = new XYSeries( "Chrome" );
        chrome.add( 1.0 , 4.0 );
        chrome.add( 2.0 , 5.0 );
        chrome.add( 3.0 , 6.0 );
        final XYSeries iexplorer = new XYSeries( "InternetExplorer" );
        iexplorer.add( 3.0 , 4.0 );
        iexplorer.add( 4.0 , 5.0 );
        iexplorer.add( 5.0 , 4.0 );

        final XYSeries safari = new XYSeries( "Safari" );

        for (int x=0 ; x<100 ; x++) {
            int y = x*x;
            safari.add(x, y);
        }


        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( firefox );
        dataset.addSeries( chrome );
        dataset.addSeries( iexplorer );
        dataset.addSeries( safari );*/
        return dataset;
    }

    @Override
    public void setContentPane(Container contentPane) {
        super.setContentPane(contentPane);
        System.out.println("ahoj");
    }
}
