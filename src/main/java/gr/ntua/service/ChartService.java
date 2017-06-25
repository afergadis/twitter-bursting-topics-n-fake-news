package gr.ntua.service;

import gr.ntua.domain.Trend;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

/**
 * Created by katerina on 6/24/17.
 */
public class ChartService {
    private final static String filename =  "./src/main/resources/static/images/trend_graph.jpeg";

    public String drawChart(Iterable<Trend> trends) {
        return initialize(trends);
    }

    private String initialize(Iterable<Trend> trends) {
        String chartTitle = "XY Chart for "+trends.iterator().next().getName();

        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Time",
                "Bursting (%)",
                createDataset(trends),
                PlotOrientation.VERTICAL,
                true, true, false);


        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */

        File XYChart = new File(filename);

        try {
            Files.deleteIfExists(XYChart.toPath());
            ChartUtilities.saveChartAsJPEG(XYChart, xylineChart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imageB64 = encodeFileToBase64Binary(XYChart);


        return imageB64;
    }

    private XYDataset createDataset(Iterable<Trend> trends) {
        XYSeries trendSeries = new XYSeries( "Bursting per hours" );

        trendSeries.add(0, 0);
        Iterator<Trend> trendIterator = trends.iterator();
        while (trendIterator.hasNext()) {
            Trend tempTrend = trendIterator.next();
            trendSeries.add(tempTrend.getTimespanId(), tempTrend.getBursting());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(trendSeries);

        return dataset;
    }

    private String encodeFileToBase64Binary(File file) {
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = new String(Base64.encodeBase64(bytes), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedfile;
    }
}
