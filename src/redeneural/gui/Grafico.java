package redeneural.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Michael Murussi
 */
public class Grafico extends JPanel {

    private final XYSeries erroTreinamento;
    private final XYSeries erroValidacao;

    public Grafico() {
        super(new BorderLayout());

        this.erroTreinamento = new XYSeries("EMQ Treinamento");
        this.erroValidacao = new XYSeries("EMQ Validação");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(this.erroTreinamento);
        dataset.addSeries(this.erroValidacao);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Backpropagation - EMQ",
                "Iteração",
                "EMQ",
                dataset,
                PlotOrientation.VERTICAL, true, true, false);

        chart.getTitle().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        chart.setBackgroundPaint(Color.white);

        chart.getPlot().setBackgroundPaint(Color.white);
        chart.getXYPlot().setDomainGridlinePaint(Color.black);
        chart.getXYPlot().setRangeGridlinePaint(Color.black);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createLineBorder(Color.darkGray)));
        
        add(chartPanel);
    }

    public void limpa() {
        this.erroTreinamento.clear();
        this.erroValidacao.clear();
    }

    public void addIteracao(int iteracao, double emqTreinamento, double emqValidacao) {
        this.erroTreinamento.add(iteracao, emqTreinamento);
        this.erroValidacao.add(iteracao, emqValidacao);
    }
}
