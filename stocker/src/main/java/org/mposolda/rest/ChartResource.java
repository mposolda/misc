package org.mposolda.rest;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.function.Function;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.mposolda.reps.rest.CompaniesRep;
import org.mposolda.reps.rest.CompanyFullRep;
import org.mposolda.reps.rest.CurrenciesRep;
import org.mposolda.services.Services;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Path("/charts")
public class ChartResource {

    protected static final Logger logger = Logger.getLogger(ChartResource.class);

    /**
     * @return chart with displayed companies in the portfolio based on current value
     */
    @GET
    @NoCache
    @Path("/portfolio-current")
    @Produces("image/png")
    public Response portfolioCurrent(@QueryParam("title") String chartTitle) {
        return generateChart(chartTitle, company -> Math.round(company.getCurrentPriceOfAllStocksInHoldCZK() / 1000));
    }

    private Response generateChart(String chartTitle, Function<CompanyFullRep, Long> valueFromCompanyFunction) {
        try {
            CompaniesRep companies = Services.instance().getCompanyInfoManager().getCompanies();
            CurrenciesRep currencies = Services.instance().getCompanyInfoManager().getCurrencies();

            // Create dataset from companies
            DefaultPieDataset dataset = new DefaultPieDataset();
            for (CompanyFullRep company : companies.getCompanies()) {
                Long value = valueFromCompanyFunction.apply(company);
                if (value > 0) {
                    dataset.setValue(company.getName(), value);
                }
            }

            // Add cash
            dataset.setValue("CASH", Math.round(currencies.getPriceInHoldCZK() / 1000));

            JFreeChart chart = createChart(dataset, chartTitle);

            return Response.ok(new FeedReturnStreamingOutput(chart)).type("image/png").cacheControl(CacheControlUtil.noCache()).build();

        } catch (Exception e) {
            logger.error("Problem when serving chart request", e);
            return Response.serverError().build();
        }
    }

    private JFreeChart createChart(PieDataset dataset, String chartTitle) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createPieChart(
                chartTitle,  // chart title
                dataset,             // dataset
                false,               // include legend
                true,
                false
        );

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(222, 222, 255));

        final PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setCircular(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} {1} ({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        ));
        plot.setNoDataMessage("No data available");

        return chart;
    }


    public static class FeedReturnStreamingOutput implements StreamingOutput {

        private final JFreeChart chart;

        public FeedReturnStreamingOutput(JFreeChart chart) {
            this.chart = chart;
        }


        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
            ChartUtils.writeChartAsPNG(output,
                    chart,
                    1000,
                    540);

            output.flush();
        }
    }


}
