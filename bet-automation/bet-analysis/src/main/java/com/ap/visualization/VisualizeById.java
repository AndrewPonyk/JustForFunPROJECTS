package com.ap.visualization;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.jdesktop.swingx.JXDatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A demo showing the addition and removal of multiple datasets / renderers.
 */
public class VisualizeById extends ApplicationFrame implements ActionListener {
    private static final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    /** The plot. */
    private XYPlot plot;

    private JTextField idText;

    private JTextField queryText;

    /** The index of the last dataset added. */
    private int datasetIndex = 0;

    /** Panel for drawing chart*/
    private final ChartPanel chartPanel;
    private final JFreeChart chart;
    private final JXDatePicker picker;

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public VisualizeById(final String title) {
        super(title);

        // random plot - can be added in JFreeChart constructor
        //final XYSeriesCollection dataset1 = createRandomDataset("Series 1");

        chart = ChartFactory.createXYLineChart(
                "Multiple example", "X", "Y", null, PlotOrientation.VERTICAL, true, true, false
        );
        chart.setBackgroundPaint(Color.white);

        this.plot = chart.getXYPlot();
        this.plot.setBackgroundPaint(Color.white);
        this.plot.setDomainGridlinePaint(Color.white);
        this.plot.setRangeGridlinePaint(Color.white);
//        this.plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 4, 4, 4, 4));
        final ValueAxis axis = this.plot.getDomainAxis();

        final XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        axis.setAutoRange(true);

        final NumberAxis rangeAxis2 = new NumberAxis("Range Axis 2");
        rangeAxis2.setAutoRangeIncludesZero(false);

        final JPanel content = new JPanel(new BorderLayout());

        chartPanel = new ChartPanel(chart);
        content.add(chartPanel);

        final JButton button1 = new JButton("Add Dataset");
        button1.setActionCommand("ADD_DATASET");
        button1.addActionListener(this);

        final JButton button2 = new JButton("Remove Dataset");
        button2.setActionCommand("REMOVE_DATASET");
        button2.addActionListener(this);

        final JButton saveAllBets = new JButton("Save bets as images from date");
        saveAllBets.setActionCommand("SAVE_BETS_AS_IMAGES");
        saveAllBets.addActionListener(this);

        picker = new JXDatePicker();
        picker.setDate(Calendar.getInstance().getTime());
        picker.setFormats(new SimpleDateFormat("yyyy-MM-dd"));

        final JButton saveAllBetsByQuery = new JButton("Save bets as images from query");
        saveAllBetsByQuery.setActionCommand("SAVE_BETS_AS_IMAGES_FROM_QUERY");
        saveAllBetsByQuery.addActionListener(this);

        queryText = new JTextField(30);
        idText = new JTextField(10);

        final JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(idText);
        buttonPanel.add(saveAllBets);
        buttonPanel.add(picker);
        buttonPanel.add(saveAllBetsByQuery);
        buttonPanel.add(queryText);

        content.add(buttonPanel, BorderLayout.SOUTH);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 900));
        setContentPane(content);
    }

    private List<List<XYSeriesCollection>> getAllDatasetsFromDate(String date){
        List<List<XYSeriesCollection>> result = new ArrayList<>();
        try {
            List<List<ImmutableTriple<String, Double, Double>>> allBetsFromDate = BetDataPlotRetriever.getAllBetsFromDate(date);
            for (List<ImmutableTriple<String, Double, Double>> item : allBetsFromDate) {
                result.add(tripleToXySeriesCollections(Optional.of(item)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<List<XYSeriesCollection>> getAllDatasetsFromQuery(String query){
        List<List<XYSeriesCollection>> result = new ArrayList<>();
        try {
            List<List<ImmutableTriple<String, Double, Double>>> allBetsFromDate =
                    BetDataPlotRetriever.getAllBetsFromQuery(query);
            for (List<ImmutableTriple<String, Double, Double>> item : allBetsFromDate) {
                result.add(tripleToXySeriesCollections(Optional.of(item)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieve dataset from BET_HISTORY table
     *
     * @return The data set
     */
    private List<XYSeriesCollection> getDatasetFromBetHistory(String like) {
        try {
            Optional<java.util.List<ImmutableTriple<String, Double, Double>>> betData = BetDataPlotRetriever.getBetDataByLike(like);
            return tripleToXySeriesCollections(betData);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList(new XYSeriesCollection(new XYSeries("No data found for Id=" + like)));
    }

    private List<XYSeriesCollection> tripleToXySeriesCollections(Optional<List<ImmutableTriple<String, Double, Double>>> betData) {
        List<XYSeriesCollection> result = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger(1);
        final XYSeries series1 = new XYSeries(betData.orElse(new ArrayList<>()).get(0).getLeft().split(" - ")[0]);
        final XYSeries  series2 = new XYSeries(betData.orElse(new ArrayList<>()).get(0).getLeft().split(" - ")[1]);
        betData
                .orElse(new ArrayList<>())
                .stream().filter(t->t.getMiddle()>0)
                .forEach(e->{
                    series1.add(new XYDataItem(new Double(counter.incrementAndGet()), e.getMiddle()));
                    series2.add(new XYDataItem(new Double(counter.get()), e.getRight()));
                });

        result.add(new XYSeriesCollection(series1));
        result.add(new XYSeriesCollection(series2));
        return result;
    }

    /**
     * Creates a random dataset.
     *
     * @param name  the series name.
     *
     * @return The dataset.
     */
    private XYSeriesCollection createRandomDataset(final String name) {
        XYSeries series = new XYSeries("Test");
        //final TimeSeries series = new TimeSeries(name);
        double valueX = 0;
        double valueY = 0;
        for (int i = 0; i <= Integer.parseInt(idText.getText()); i++) {
            series.add(valueX, valueY);
            valueX =  i;
            valueY =  (Math.random() * 2);
            System.out.println(valueX + " " + valueY);
        }

        return new XYSeriesCollection(series);
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    *
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    /**
     * Handles a click on the button by adding new (random) data.
     *
     * @param e  the action event.
     */
    public void actionPerformed(final ActionEvent e) {

        if (e.getActionCommand().equals("ADD_DATASET")) {
            if (this.datasetIndex < 20) {
                this.datasetIndex++;
                List<XYSeriesCollection> datasetFromBetHistory = getDatasetFromBetHistory(this.idText.getText());

                addBetPlayersPlotAndSaveImage(datasetFromBetHistory);

            }
        }
        else if (e.getActionCommand().equals("REMOVE_DATASET")) {
                clearChart();
        } else if (e.getActionCommand().equals("SAVE_BETS_AS_IMAGES")){
            List<List<XYSeriesCollection>> allDatasetsFromDate = getAllDatasetsFromDate(dateFormatter.format(picker.getDate()));
            clearChart();
            for (List<XYSeriesCollection> item : allDatasetsFromDate) {
                addBetPlayersPlotAndSaveImage(item);
                clearChart();
            }
        } else if (e.getActionCommand().equals("SAVE_BETS_AS_IMAGES_FROM_QUERY")){
            List<List<XYSeriesCollection>> allDatasetsFromQuery =
                    getAllDatasetsFromQuery(queryText.getText());
            clearChart();
            for (List<XYSeriesCollection> item : allDatasetsFromQuery) {
                addBetPlayersPlotAndSaveImage(item);
                clearChart();
            }
        }

    }

    private void clearChart() {
        while (this.datasetIndex>0){
            this.plot.setDataset(this.datasetIndex, null);
            this.plot.setRenderer(this.datasetIndex, null);
            this.datasetIndex--;
        }
    }

    private void addBetPlayersPlotAndSaveImage(List<XYSeriesCollection> datasetFromBetHistory) {
        this.datasetIndex++;
        this.plot.setDataset(
                this.datasetIndex, datasetFromBetHistory.get(0)
        );
        this.plot.setRenderer(this.datasetIndex, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.00"), new DecimalFormat("0.00")
        )));

        this.datasetIndex++;
        this.plot.setDataset(
                this.datasetIndex, datasetFromBetHistory.get(1)
        );
        this.plot.setRenderer(this.datasetIndex, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new StandardXYToolTipGenerator()));
        String filename = datasetFromBetHistory.get(0).getSeriesKey(0).toString()
                + " - "+ datasetFromBetHistory.get(1).getSeriesKey(0).toString();
        filename = filename.replaceAll("/", "\\\\");
        saveChartAsImage(filename);
    }

    public boolean saveChartAsImage(String fileName){
        try {
            ChartUtilities.saveChartAsPNG(new File(FileSystemUtils.BET_IMAGES_PATH + fileName +".png"), chart,
                    chartPanel.getWidth(), chartPanel.getHeight());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final VisualizeById demo = new VisualizeById("Multiple Dataset Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}