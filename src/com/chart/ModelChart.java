package com.chart;

/**
 *
 * @author rampa
 */

public class ModelChart {

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public ModelChart(String label, double[] values) {
        this.label = label;
        this.values = values;
    }

    public ModelChart() {
    }

    private String label;
    private double values[];

    // PERBAIKAN: Validasi kondisi null/kosong agar aplikasi tidak crash saat runtime
    public double getMaxValues() {
        if (values == null || values.length == 0) {
            return 0; // Mengembalikan 0 jika data belum diisi
        }
        
        double max = 0;
        for (double v : values) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }
}

        
