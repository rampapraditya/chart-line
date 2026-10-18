package com.main;

/**
 *
 * @author rampa
 */


import com.chart.CurveLineChart;
import com.chart.ModelChart;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Main extends JFrame {

    private final CurveLineChart chart;
    private final Timer timer;
    private final JButton btnToggle;
    private final Random random = new Random();
    
    // List lokal untuk mengontrol pergerakan geser data ke kanan
    private final List<ModelChart> localDataList = new ArrayList<>();
    private final int MAX_VISIBLE_DATA = 7; // Batas maksimal kolom data yang tampil di layar
    private int dataCounter = 1; // Untuk penanda urutan label waktu/detik

    public Main() {
        setTitle("Multi Line Chart");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Layout Panel Utama
        JPanel panel = new JPanel(new BorderLayout());
        //panel.setBackground(new Color(25, 25, 25));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(panel);

        // 2. Inisialisasi Grafik
        chart = new CurveLineChart();
        chart.setForeground(new Color(200, 200, 200));
        panel.add(chart, BorderLayout.CENTER);

        // Daftarkan 3 Garis
        chart.addLegend("Garis Merah", new Color(235, 54, 54));
        chart.addLegend("Garis Hijau", new Color(54, 184, 86));
        chart.addLegend("Garis Biru", new Color(54, 131, 235));

        // 3. Masukkan Data Awal (Pre-load data kosong/nol terlebih dahulu agar grafik siap)
        for (int i = 0; i < MAX_VISIBLE_DATA; i++) {
            addNewRealtimeData();
        }
        refreshChartDisplay();

        // 4. Membuat Panel Tombol di bagian Atas Aplikasi
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelControl.setOpaque(false);
        
        btnToggle = new JButton("Mulai Realtime");
        btnToggle.setBackground(new Color(54, 131, 235));
        btnToggle.setForeground(Color.WHITE);
        btnToggle.setFocusPainted(false);
        panelControl.add(btnToggle);
        panel.add(panelControl, BorderLayout.NORTH);

        // 5. Membuat Mesin Timer Per Detik (1000ms = 1 detik)
        timer = new Timer(200, (ActionEvent e) -> {
            // Langkah pergerakan: tambah data baru di kanan
            addNewRealtimeData();
            
            // Jika total data melebihi batas tampilan, buang data paling kiri (indeks 0)
            if (localDataList.size() > MAX_VISIBLE_DATA) {
                localDataList.remove(0);
            }
            
            // Render ulang datanya ke komponen grafik
            refreshChartDisplay();
        });

        // Action Listener saat tombol diklik
        btnToggle.addActionListener((ActionEvent e) -> {
            if (timer.isRunning()) {
                timer.stop();
                btnToggle.setText("Mulai Realtime");
                btnToggle.setBackground(new Color(54, 131, 235));
            } else {
                timer.start();
                btnToggle.setText("Berhenti Realtime");
                btnToggle.setBackground(new Color(235, 54, 54));
            }
        });
    }

    // Fungsi pembantu untuk membuat data acak baru seolah-olah dari sensor/realtime
    private void addNewRealtimeData() {
        double valMerah = random.nextInt(600) + 100; // Acak angka dari 100 - 700
        double valHijau = random.nextInt(600) + 100;
        double valBiru = random.nextInt(600) + 100;
        
        String labelX = "T-" + dataCounter + "s";
        localDataList.add(new ModelChart(labelX, new double[]{valMerah, valHijau, valBiru}));
        dataCounter++;
    }

    // Fungsi pembantu untuk mendorong ulang List lokal yang sudah bergeser ke dalam CurveLineChart
    private void refreshChartDisplay() {
        chart.clear(); // Bersihkan render grafik lama
        
        // Daftarkan ulang legandanya karena method clear menghapus seluruh komponen dalam panel
        chart.addLegend("Garis Merah", new Color(235, 54, 54));
        chart.addLegend("Garis Hijau", new Color(54, 184, 86));
        chart.addLegend("Garis Biru", new Color(54, 131, 235));
        
        // Masukkan semua antrean data saat ini ke dalam grafik
        for (ModelChart data : localDataList) {
            chart.addData(data);
        }
        
        chart.revalidate();
        chart.repaint();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}


