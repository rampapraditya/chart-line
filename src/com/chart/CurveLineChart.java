package com.chart;

/**
 *
 * @author rampa
 */
import com.chart.blankchart.BlankPlotChart;
import com.chart.blankchart.BlankPlotChatRender;
import com.chart.blankchart.SeriesSize;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class CurveLineChart extends JPanel {

    private final BlankPlotChart blankPlotChart;
    private final JPanel panelLegend;
    private final List<ModelLegend> legends = new ArrayList<>();
    private final List<ModelChart> model = new ArrayList<>();

    public CurveLineChart() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // 1. Inisialisasi area bidang grafik
        blankPlotChart = new BlankPlotChart();
        add(blankPlotChart, BorderLayout.CENTER);

        // 2. Inisialisasi panel untuk menampung legenda warna di bagian bawah grafik
        panelLegend = new JPanel();
        panelLegend.setOpaque(false);
        panelLegend.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
        add(panelLegend, BorderLayout.SOUTH);

        // 3. Pasang render kustom untuk menggambar garis kurva (Bézier Curve)
        blankPlotChart.setBlankPlotChatRender(new BlankPlotChatRender() {
            @Override
            public String getLabelText(int index) {
                return model.get(index).getLabel();
            }

            @Override
            public void renderSeries(BlankPlotChart chart, Graphics2D g2, SeriesSize size, int index) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Menentukan warna garis berdasarkan indeks legenda yang terdaftar
                if (index < legends.size()) {
                    g2.setColor(legends.get(index).getColor());
                } else {
                    g2.setColor(Color.WHITE);
                }

                // Mengatur ketebalan garis grafik
                g2.setStroke(new java.awt.BasicStroke(3f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));

                // Melakukan perulangan untuk menghubungkan titik koordinat dari kiri ke kanan
                for (int i = 0; i < model.size(); i++) {
                    // Mendapatkan koordinat X dan Y aktual dari mesin BlankPlotChart
                    double x = chart.getXAxis(i, size.getWidth(), model.size());
                    double y = chart.getYAxis(model.get(i).getValues()[index], size.getHeight());

                    if (i > 0) {
                        double prevX = chart.getXAxis(i - 1, size.getWidth(), model.size());
                        double prevY = chart.getYAxis(model.get(i - 1).getValues()[index], size.getHeight());

                        // Menghitung titik kontrol tengah untuk membuat efek lekukan halus (Bézier Spline)
                        double ctrlx1 = prevX + (x - prevX) / 2.0;
                        double ctrlx2 = ctrlx1;

                        java.awt.geom.CubicCurve2D c = new java.awt.geom.CubicCurve2D.Double(
                                prevX, prevY, ctrlx1, prevY, ctrlx2, y, x, y);

                        g2.draw(c);
                    }
                }
            }
        });
    }

    // Method untuk mendaftarkan nama dan warna garis grafik
    public void addLegend(String name, Color color) {
        ModelLegend data = new ModelLegend(name, color);
        legends.add(data);
        panelLegend.add(new LegendItem(data));
        panelLegend.revalidate();
        panelLegend.repaint();
    }

    // Method untuk memasukkan baris data koordinat ganda
    public void addData(ModelChart data) {
        model.add(data);
        blankPlotChart.addSeries(data.getValues());

        // Atur labelCount secara otomatis mengikuti total data sumbu X yang masuk
        blankPlotChart.setLabelCount(model.size());

        // Cek nilai tertinggi secara dinamis untuk menentukan batas atas skala sumbu Y
        if (data.getMaxValues() > blankPlotChart.getMaxValues()) {
            blankPlotChart.setMaxValues(data.getMaxValues());
        }
    }

    // Method untuk mengosongkan grafik
    public void clear() {
        model.clear();
        legends.clear();
        panelLegend.removeAll();
        blankPlotChart.clear();
        blankPlotChart.setMaxValues(0);
    }
}
