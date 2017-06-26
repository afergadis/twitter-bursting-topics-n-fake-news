package gr.ntua.service;

import gr.ntua.domain.Trend;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        String chartTitle = "Line Chart for "+trends.iterator().next().getName();


        JFreeChart xylineChart = ChartFactory.createLineChart(
                chartTitle,
                "Date",
                "Bursting (%)",
                getDataset(trends),
                PlotOrientation.VERTICAL,
                true, true, false);


        int width = 1240;   /* Width of the image */
        int height = 780;  /* Height of the image */

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

    private DefaultCategoryDataset getDataset(Iterable<Trend> trends) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Iterator<Trend> trendIterator = trends.iterator();
        while (trendIterator.hasNext()) {
            Trend tempTrend = trendIterator.next();

            String date = dateToString(tempTrend.getDateTime());
            String[] datetime = date.split(" ");

            dataset.addValue(tempTrend.getBursting(), datetime[0],datetime[1] );
        }

        return dataset;
    }

    private String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        return df.format(date);
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
