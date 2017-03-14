package org.mposolda;

import org.jfree.ui.RefineryUtilities;

public class XYLineChart_AWT
{


    public static void main( String[ ] args )
    {
        XYLineChart2 chart = new XYLineChart2("Browser Usage Statistics", "Which Browser are you using?");
        chart.pack( );
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible( true );
    }

}
