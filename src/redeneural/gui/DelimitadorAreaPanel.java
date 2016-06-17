package redeneural.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import redeneural.io.SelecaoClasseReader;
import redeneural.io.SelecaoClasseWriter;
import redeneural.model.Classe;
import redeneural.model.Projeto;

/**
 *
 * @author Michael Murussi
 */
public class DelimitadorAreaPanel extends javax.swing.JPanel implements UpdateContent {

    private enum Ferramenta {
        CIRCLE, SQUARE
    };

    private Projeto projeto;
    private List<Classe> classes;
    private final ImageDisplay imageDisplay;
    private final JFileChooser fileChooserImage;
    private final JFileChooser fileChooserSelecao;
    private BufferedImage image;    
    private ClassesComboBoxModel classesComboBoxModel;
    private AmostrasListModel amostrasListModel;
    private Point pointStart;
    private Point pointEnd;

    private Ferramenta ferramentaSelecionada;

    /**
     * Creates new form ColetaAmostraPanel
     *
     * @param projeto
     * @param amostrasPanel
     */
    public DelimitadorAreaPanel(Projeto projeto) {
        initComponents();
        
        setProjeto(projeto);

        this.fileChooserImage = FileChooserUtil.getNewImageFileChooser();
        this.fileChooserSelecao = FileChooserUtil.getNewFileChooser(FileChooserUtil.FILE_FILTER_TXT);

        // configura display
        imageDisplay = new ImageDisplay() {

            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                
                Graphics2D g = (Graphics2D) graphics.create();

                if(pointStart != null && pointEnd != null) {
                    if (ferramentaSelecionada.equals(DelimitadorAreaPanel.Ferramenta.CIRCLE)) {
                        Point p2 = new Point();
                        imageDisplay.transformPoint(pointStart, p2);
                        Point p3 = new Point();
                        imageDisplay.transformPoint(pointEnd, p3);

                        g.setColor(Color.red);
                        g.setStroke(new BasicStroke(4));
                        int size = Math.min(Math.abs(p3.x - p2.x), Math.abs(p3.y - p2.y));
                        int x = Math.max(p2.x - size, 0);
                        int y = Math.max(p2.y - size, 0);
                        size =  Math.min(Math.min(p2.x - x, p2.y - y), size);
                        x = p2.x - size;
                        y = p2.y - size;
                        g.drawOval(x, y, size * 2, size * 2);
                        
                        g.fillRect(p2.x - 1, p2.y - 1, 2, 2);
                    } else if(ferramentaSelecionada.equals(DelimitadorAreaPanel.Ferramenta.SQUARE)) {
                        Point p2 = new Point();
                        imageDisplay.transformPoint(pointStart, p2);
                        Point p3 = new Point();
                        imageDisplay.transformPoint(pointEnd, p3);

                        g.setColor(Color.red);
                        g.setStroke(new BasicStroke(4));
                        int x = Math.min(p2.x, p3.x);
                        int y = Math.min(p2.y, p3.y);
                        g.drawRect(p2.x, p2.y, Math.abs(p3.x - p2.x), Math.abs(p3.y - p2.y));
                    }
                }

                g.dispose();
            }

        };
        JScrollPane imageScrollPane = new JScrollPane(imageDisplay);
        imageScrollPane.setPreferredSize(new Dimension(300, 250));

        add(imageScrollPane, java.awt.BorderLayout.CENTER);

        imageDisplay.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Point p = new Point(e.getPoint());
                imageDisplay.restorePoint(e.getPoint(), p);
                updateStatusBar(p);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Point p = new Point(e.getPoint());
                imageDisplay.restorePoint(e.getPoint(), p);
                pointEnd = p;
                
                amostrasListModel.fireModelChanged();
                imageDisplay.repaint();
            }

        });

        imageDisplay.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                
                Point p = new Point(e.getPoint());
                imageDisplay.restorePoint(e.getPoint(), p);

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        if (ferramentaSelecionada.equals(Ferramenta.CIRCLE)) {
                            pointStart = p;
                        } else if(ferramentaSelecionada.equals(Ferramenta.SQUARE)) {
                            pointStart = p;
                        }
                        break;
                }
                
                amostrasListModel.fireModelChanged();
                imageDisplay.repaint();
            }
        });

        lblPosicao.setText("");
        lblPixel.setText("");
        lblPixelColor.setText("");
        lblPixelColor.setOpaque(true);
        lblPixelColor.setPreferredSize(new Dimension(21, 21));

        this.ferramentaSelecionada = Ferramenta.CIRCLE;
        btnPixelTool.setSelected(true);
    }

    public final void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        this.classes = projeto.getClasses();

        // configura classes
        classesComboBoxModel = new ClassesComboBoxModel(classes);

        amostrasListModel = new AmostrasListModel(classes);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ferramentasGroup = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnAbrirImagem = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnLimparSelecaoAtual = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPixelTool = new javax.swing.JToggleButton();
        btnPixelTool1 = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        edtZoom = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        lblPosicao = new javax.swing.JLabel();
        lblPixelColor = new javax.swing.JLabel();
        lblPixel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        btnAbrirImagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/fileopenimage.png"))); // NOI18N
        btnAbrirImagem.setToolTipText("Abrir Imagem");
        btnAbrirImagem.setFocusable(false);
        btnAbrirImagem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrirImagem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrirImagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirImagemActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAbrirImagem);
        jToolBar1.add(jSeparator1);

        btnLimparSelecaoAtual.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/tool_delete.png"))); // NOI18N
        btnLimparSelecaoAtual.setToolTipText("Limpar seleção atual");
        btnLimparSelecaoAtual.setFocusable(false);
        btnLimparSelecaoAtual.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLimparSelecaoAtual.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLimparSelecaoAtual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparSelecaoAtualActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLimparSelecaoAtual);
        jToolBar1.add(jSeparator2);

        ferramentasGroup.add(btnPixelTool);
        btnPixelTool.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/circle-tool.png"))); // NOI18N
        btnPixelTool.setToolTipText("Ferramenta seleção de Pixel");
        btnPixelTool.setFocusable(false);
        btnPixelTool.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPixelTool.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPixelTool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCircleToolActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPixelTool);

        ferramentasGroup.add(btnPixelTool1);
        btnPixelTool1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/square-tool.png"))); // NOI18N
        btnPixelTool1.setSelected(true);
        btnPixelTool1.setToolTipText("Ferramenta seleção de Pixel");
        btnPixelTool1.setFocusable(false);
        btnPixelTool1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPixelTool1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPixelTool1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSquareToolActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPixelTool1);
        jToolBar1.add(jSeparator3);

        edtZoom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25%", "50%", "100%", "200%", "300%", "400%", "500%", "600%" }));
        edtZoom.setToolTipText("Zoom");
        edtZoom.setMaximumSize(new java.awt.Dimension(80, 20));
        edtZoom.setMinimumSize(new java.awt.Dimension(80, 20));
        edtZoom.setPreferredSize(new java.awt.Dimension(80, 20));
        edtZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtZoomActionPerformed(evt);
            }
        });
        jToolBar1.add(edtZoom);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jPanel1.setMinimumSize(new java.awt.Dimension(100, 29));
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 32));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblPosicao.setText("(0, 0)");
        jPanel1.add(lblPosicao);

        lblPixelColor.setText("C");
        lblPixelColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPixelColor.setPreferredSize(new java.awt.Dimension(21, 21));
        jPanel1.add(lblPixelColor);

        lblPixel.setText("R0 G0 B0");
        jPanel1.add(lblPixel);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirImagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirImagemActionPerformed

        if (fileChooserImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                abreImagem(fileChooserImage.getSelectedFile());
            } catch (IOException ex) {
                Logger.getLogger(DelimitadorAreaPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Falha ao carregar imagem!\n" + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(null);
            }
        }

    }//GEN-LAST:event_btnAbrirImagemActionPerformed

    private void btnCircleToolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCircleToolActionPerformed
        setFerramentaSelecionada(Ferramenta.CIRCLE);
    }//GEN-LAST:event_btnCircleToolActionPerformed

    private void btnLimparSelecaoAtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparSelecaoAtualActionPerformed

    }//GEN-LAST:event_btnLimparSelecaoAtualActionPerformed

    private void edtZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtZoomActionPerformed
        double zoomList[] = new double[8];
        zoomList[0] = 0.25;
        zoomList[1] = 0.5;
        zoomList[2] = 1;
        zoomList[3] = 2;
        zoomList[4] = 3;
        zoomList[5] = 4;
        zoomList[6] = 5;
        zoomList[7] = 6;
        imageDisplay.setZoom(zoomList[edtZoom.getSelectedIndex()]);
    }//GEN-LAST:event_edtZoomActionPerformed

    private void btnSquareToolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSquareToolActionPerformed
        setFerramentaSelecionada(Ferramenta.SQUARE);
    }//GEN-LAST:event_btnSquareToolActionPerformed

    private void setFerramentaSelecionada(Ferramenta ferramentaSelecionada) {
        if (!this.ferramentaSelecionada.equals(ferramentaSelecionada)) {
            this.ferramentaSelecionada = ferramentaSelecionada;
            imageDisplay.repaint();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirImagem;
    private javax.swing.JButton btnLimparSelecaoAtual;
    private javax.swing.JToggleButton btnPixelTool;
    private javax.swing.JToggleButton btnPixelTool1;
    private javax.swing.JComboBox edtZoom;
    private javax.swing.ButtonGroup ferramentasGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblPixel;
    private javax.swing.JLabel lblPixelColor;
    private javax.swing.JLabel lblPosicao;
    // End of variables declaration//GEN-END:variables

    private void abreImagem(File file) throws IOException {
        image = ImageIO.read(file);
        this.imageDisplay.setImage(image);
    }

    private void updateStatusBar(Point p) {
        lblPosicao.setText(String.format("(%d, %d)", p.x, p.y));
        if (image != null && p.x < image.getWidth() && p.y < image.getHeight()) {
            int rgb = image.getRGB(p.x, p.y);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            lblPixel.setText(String.format("(R %d, G %d, B %d)", r, g, b));
            lblPixelColor.setBackground(new Color(rgb));
        }
    }

    @Override
    public void updateContent() {
        this.classesComboBoxModel.fireContentsChanged();
        this.amostrasListModel.fireModelChanged();
    }

    private final class AmostrasListModel extends AbstractListModel<String> implements ListModel<String> {

        private final List<Classe> classes;

        public AmostrasListModel(List<Classe> classes) {
            this.classes = classes;
        }

        @Override
        public int getSize() {
            return classes.size();
        }

        @Override
        public String getElementAt(int index) {
            Classe c = classes.get(index);
            if (c != null) {
                return String.format("%s - %d", c.getNome(), c.getSelecaoAmostras().size());
            }
            return null;
        }

        public void fireModelChanged() {
            fireContentsChanged(this, -1, -1);
        }

    }

    private final class ClassesComboBoxModel extends AbstractListModel implements ComboBoxModel {

        private final List<Classe> classes;
        private String selection = null;

        public ClassesComboBoxModel(List<Classe> classes) {
            this.classes = classes;
            if (classes.size() > 0) {
                selection = classes.get(0).getNome();
            }
        }

        @Override
        public int getSize() {
            return classes.size();
        }

        @Override
        public String getElementAt(int index) {
            return classes.get(index).getNome();
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selection = (String) anItem;
            fireContentsChanged(this, -1, -1);
        }

        @Override
        public Object getSelectedItem() {
            return selection;
        }

        protected void fireContentsChanged() {
            if (!classes.isEmpty()) {
                super.fireContentsChanged(this, 0, classes.size() - 1);
            } else {
                super.fireContentsChanged(this, -1, -1);
            }
        }

        public void fireItemAdded(int index) {
            fireIntervalAdded(this, index, index);
            setSelectedItem(classes.get(index).getNome());
        }

        public void fireItemRemoved(int index) {
            if (getSize() == 0) {
                setSelectedItem(null);
            }
            if (index < getSize()) {
                setSelectedItem(getElementAt(index));
            } else if (index > 0) {
                setSelectedItem(getElementAt(index - 1));
            }

            fireIntervalRemoved(this, index, index);
        }

        @Override
        protected void fireIntervalAdded(Object source, int index0, int index1) {
            super.fireIntervalAdded(source, index0, index1);
        }

    }

}
