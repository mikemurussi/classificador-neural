package redeneural.gui;

import redeneural.classificador.MascaraSelecaoBuilder;
import redeneural.classificador.AmostraColetaBuilder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class ColetaAmostraPanel extends javax.swing.JPanel implements UpdateContent {

    private enum Ferramenta {
        PIXEL, MAGICWAND
    };

    private Projeto projeto;
    private List<Classe> classes;
    private final AmostrasJPanel amostrasPanel;
    private final ImageDisplay imageDisplay;
    private final JFileChooser fileChooserImage;
    private final JFileChooser fileChooserSelecao;
    private BufferedImage image;    
    private ClassesComboBoxModel classesComboBoxModel;
    private AmostrasListModel amostrasListModel;
    private final MagicWand magicWand = new MagicWand();

    private Ferramenta ferramentaSelecionada;

    /**
     * Creates new form ColetaAmostraPanel
     *
     * @param projeto
     * @param amostrasPanel
     */
    public ColetaAmostraPanel(Projeto projeto, AmostrasJPanel amostrasPanel) {
        initComponents();
        
        this.amostrasPanel = amostrasPanel;
        setProjeto(projeto);

        this.fileChooserImage = FileChooserUtil.getNewImageFileChooser();
        this.fileChooserSelecao = FileChooserUtil.getNewFileChooser(FileChooserUtil.FILE_FILTER_TXT);

        // configura display
        imageDisplay = new ImageDisplay() {

            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Classe c = getClasseSelecionada();
                if (c != null) {
                    Graphics g = graphics.create();

                    if (ferramentaSelecionada.equals(Ferramenta.MAGICWAND)) {
                        Graphics2D g2D = (Graphics2D) g;
                        g2D.scale(getZoom(), getZoom());
                        g2D.setColor(c.getCor());
                        for (Point p : c.getSelecaoAmostras()) {
                            g2D.fillRect(p.x, p.y, 1, 1);
                        }
                    } else if (ferramentaSelecionada.equals(Ferramenta.PIXEL)) {
                        Point p2 = new Point();
                        for (Point p : c.getSelecaoAmostras()) {
                            imageDisplay.transformPoint(p, p2);

                            g.setColor(Color.white);
                            g.fillRect(p2.x - 8, p2.y - 2, 6, 3);
                            g.fillRect(p2.x + 1, p2.y - 2, 6, 3);
                            g.fillRect(p2.x - 2, p2.y - 8, 3, 6);
                            g.fillRect(p2.x - 2, p2.y + 1, 3, 6);

                            g.setColor(Color.black);
                            g.drawRect(p2.x - 8, p2.y - 2, 6, 3);
                            g.drawRect(p2.x + 1, p2.y - 2, 6, 3);
                            g.drawRect(p2.x - 2, p2.y - 8, 3, 6);
                            g.drawRect(p2.x - 2, p2.y + 1, 3, 6);
                        }
                    }

                    g.dispose();
                }
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

        });

        imageDisplay.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                Classe c = getClasseSelecionada();
                if (c != null) {
                    Point p = new Point(e.getPoint());
                    imageDisplay.restorePoint(e.getPoint(), p);

                    switch (e.getButton()) {
                        case MouseEvent.BUTTON1:
                            if (ferramentaSelecionada.equals(Ferramenta.PIXEL)) {
                                c.addAmostra(p);
                            } else if (ferramentaSelecionada.equals(Ferramenta.MAGICWAND)) {
                                magicWand.setImage(image);
                                magicWand.setLimiar(getMagicWandValue());
                                magicWand.select(p);
                                for (Point p2 : magicWand.getSelection()) {
                                    c.addAmostra(p2);
                                }
                            }
                            break;
                        case MouseEvent.BUTTON3:
                            if (ferramentaSelecionada.equals(Ferramenta.PIXEL)) {
                                c.removeAmostra(p);
                            } else if (ferramentaSelecionada.equals(Ferramenta.MAGICWAND)) {
                                magicWand.setImage(image);
                                magicWand.setLimiar(getMagicWandValue());
                                magicWand.select(p);
                                for (Point p2 : magicWand.getSelection()) {
                                    c.removeAmostra(p2);
                                }
                            }
                            break;
                    }
                    amostrasListModel.fireModelChanged();
                    imageDisplay.repaint();
                }
            }

        });

        lblPosicao.setText("");
        lblPixel.setText("");
        lblPixelColor.setText("");
        lblPixelColor.setOpaque(true);
        lblPixelColor.setPreferredSize(new Dimension(21, 21));

        this.ferramentaSelecionada = Ferramenta.PIXEL;
        btnPixelTool.setSelected(true);
    }

    public final void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        this.classes = projeto.getClasses();

        // configura classes
        classesComboBoxModel = new ClassesComboBoxModel(classes);
        comboClasse.setModel(classesComboBoxModel);

        amostrasListModel = new AmostrasListModel(classes);
        listAmostras.setModel(amostrasListModel);
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
        btnAbrirSelecao = new javax.swing.JButton();
        btnSalvarSelecao = new javax.swing.JButton();
        btnImportarMascaraSelecao = new javax.swing.JButton();
        btnLimparSelecaoAtual = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPixelTool = new javax.swing.JToggleButton();
        btnPixelTool1 = new javax.swing.JToggleButton();
        btnMagicWandTool = new javax.swing.JToggleButton();
        edtMagicWandValue = new javax.swing.JSpinner();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnGerarAmostras = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        edtZoom = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        lblPosicao = new javax.swing.JLabel();
        lblPixelColor = new javax.swing.JLabel();
        lblPixel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnAdicionarClasse = new javax.swing.JButton();
        btnRemoverClasse = new javax.swing.JButton();
        comboClasse = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        listAmostras = new javax.swing.JList();

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

        btnAbrirSelecao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/fileopen.png"))); // NOI18N
        btnAbrirSelecao.setToolTipText("Abrir Seleção");
        btnAbrirSelecao.setFocusable(false);
        btnAbrirSelecao.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrirSelecao.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrirSelecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirSelecaoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAbrirSelecao);

        btnSalvarSelecao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/filesave.png"))); // NOI18N
        btnSalvarSelecao.setToolTipText("Salvar Seleção");
        btnSalvarSelecao.setFocusable(false);
        btnSalvarSelecao.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalvarSelecao.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalvarSelecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarSelecaoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSalvarSelecao);

        btnImportarMascaraSelecao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/folder_download.png"))); // NOI18N
        btnImportarMascaraSelecao.setToolTipText("Importar seleção a partir de máscara");
        btnImportarMascaraSelecao.setFocusable(false);
        btnImportarMascaraSelecao.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImportarMascaraSelecao.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImportarMascaraSelecao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarMascaraSelecaoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnImportarMascaraSelecao);

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
        btnPixelTool.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/pixel-tool.png"))); // NOI18N
        btnPixelTool.setToolTipText("Ferramenta seleção de Pixel");
        btnPixelTool.setFocusable(false);
        btnPixelTool.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPixelTool.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPixelTool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPixelToolActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPixelTool);

        ferramentasGroup.add(btnPixelTool1);
        btnPixelTool1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/pixel-delete-tool.png"))); // NOI18N
        btnPixelTool1.setSelected(true);
        btnPixelTool1.setToolTipText("Ferramenta seleção de Pixel");
        btnPixelTool1.setFocusable(false);
        btnPixelTool1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPixelTool1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPixelTool1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPixelTool1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPixelTool1);

        ferramentasGroup.add(btnMagicWandTool);
        btnMagicWandTool.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/magic-wand-tool.png"))); // NOI18N
        btnMagicWandTool.setToolTipText("Ferramenta Magic Wand");
        btnMagicWandTool.setFocusable(false);
        btnMagicWandTool.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMagicWandTool.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMagicWandTool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMagicWandToolActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMagicWandTool);

        edtMagicWandValue.setModel(new javax.swing.SpinnerNumberModel(2, null, null, 1));
        edtMagicWandValue.setToolTipText("Limiar (Magic Wand)");
        edtMagicWandValue.setMaximumSize(new java.awt.Dimension(40, 20));
        edtMagicWandValue.setMinimumSize(new java.awt.Dimension(40, 20));
        edtMagicWandValue.setPreferredSize(new java.awt.Dimension(50, 20));
        jToolBar1.add(edtMagicWandValue);
        jToolBar1.add(jSeparator4);

        btnGerarAmostras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redeneural/gui/images/compfile.png"))); // NOI18N
        btnGerarAmostras.setText("Gerar amostras");
        btnGerarAmostras.setToolTipText("Gerar amostras a partir dos dados coletados");
        btnGerarAmostras.setFocusable(false);
        btnGerarAmostras.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnGerarAmostras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarAmostrasActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGerarAmostras);
        jToolBar1.add(jSeparator3);

        edtZoom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100%", "200%", "300%", "400%", "500%", "600%" }));
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

        jPanel2.setMinimumSize(new java.awt.Dimension(130, 150));
        jPanel2.setPreferredSize(new java.awt.Dimension(130, 100));

        btnAdicionarClasse.setText("+");
        btnAdicionarClasse.setToolTipText("Adicionar classe");
        btnAdicionarClasse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarClasseActionPerformed(evt);
            }
        });

        btnRemoverClasse.setText("-");
        btnRemoverClasse.setToolTipText("Remover classe");
        btnRemoverClasse.setPreferredSize(new java.awt.Dimension(41, 23));
        btnRemoverClasse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClasseActionPerformed(evt);
            }
        });

        comboClasse.setToolTipText("Classe selecionada");
        comboClasse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboClasseActionPerformed(evt);
            }
        });

        listAmostras.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listAmostras.setToolTipText("Amostras coletadas por classe");
        listAmostras.setFocusable(false);
        listAmostras.setRequestFocusEnabled(false);
        listAmostras.setSelectionBackground(javax.swing.UIManager.getDefaults().getColor("List.background"));
        listAmostras.setSelectionForeground(javax.swing.UIManager.getDefaults().getColor("List.foreground"));
        jScrollPane1.setViewportView(listAmostras);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboClasse, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAdicionarClasse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverClasse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 22, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAdicionarClasse, btnRemoverClasse});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoverClasse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdicionarClasse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboClasse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAdicionarClasse, btnRemoverClasse});

        add(jPanel2, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirImagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirImagemActionPerformed

        if (fileChooserImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                abreImagem(fileChooserImage.getSelectedFile());
            } catch (IOException ex) {
                Logger.getLogger(ColetaAmostraPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Falha ao carregar imagem!\n" + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(null);
            }
        }

    }//GEN-LAST:event_btnAbrirImagemActionPerformed

    private void btnAdicionarClasseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarClasseActionPerformed

        String c = JOptionPane.showInputDialog(this, "Nova classe");
        if (c != null && !c.isEmpty()) {
            addClasse(c);
        }

    }//GEN-LAST:event_btnAdicionarClasseActionPerformed

    private void btnRemoverClasseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClasseActionPerformed

        int idx = comboClasse.getSelectedIndex();
        if (idx > -1) {
            removeClasse(idx);
        }

    }//GEN-LAST:event_btnRemoverClasseActionPerformed

    private void comboClasseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboClasseActionPerformed
        imageDisplay.repaint();
    }//GEN-LAST:event_comboClasseActionPerformed

    private void edtZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtZoomActionPerformed
        imageDisplay.setZoom(edtZoom.getSelectedIndex() + 1);
    }//GEN-LAST:event_edtZoomActionPerformed

    private void btnSalvarSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarSelecaoActionPerformed

        if (!classes.isEmpty()) {
            if (fileChooserSelecao.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    salvaSelecao(FileChooserUtil.getSelectedFileWithExtension(fileChooserSelecao));
                } catch (IOException ex) {
                    Logger.getLogger(ColetaAmostraPanel.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, ex, "Erro", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(null);
                }
            }
        }

    }//GEN-LAST:event_btnSalvarSelecaoActionPerformed

    private void btnAbrirSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirSelecaoActionPerformed

        if (fileChooserSelecao.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                abreSelecao(fileChooserSelecao.getSelectedFile());
            } catch (IOException ex) {
                Logger.getLogger(ColetaAmostraPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex, "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(null);
            }
        }

    }//GEN-LAST:event_btnAbrirSelecaoActionPerformed

    private void btnGerarAmostrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarAmostrasActionPerformed

        if (image != null && !classes.isEmpty()) {

            final GerarAmostraDialog dlg = new GerarAmostraDialog(null, true);
            dlg.btnOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    try {
                        projeto.setVizinhos(dlg.getVizinhanca());
                        gerarAmostras(dlg.getConjuntoValidacao());                        
                        dlg.doClose();
                    } catch (RuntimeException ex) {
                        Logger.getLogger(ColetaAmostraPanel.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, ex, "Erro", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(null);
                    }
                }
            });
            dlg.setVisible(true);

        }

    }//GEN-LAST:event_btnGerarAmostrasActionPerformed

    private void btnPixelToolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPixelToolActionPerformed
        setFerramentaSelecionada(Ferramenta.PIXEL);
    }//GEN-LAST:event_btnPixelToolActionPerformed

    private void btnMagicWandToolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMagicWandToolActionPerformed
        setFerramentaSelecionada(Ferramenta.MAGICWAND);
    }//GEN-LAST:event_btnMagicWandToolActionPerformed

    private void btnImportarMascaraSelecaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarMascaraSelecaoActionPerformed

        if (fileChooserImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                importaMascaraSelecao(fileChooserImage.getSelectedFile());
            } catch (IOException ex) {
                Logger.getLogger(ColetaAmostraPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex, "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(null);
            }
        }

    }//GEN-LAST:event_btnImportarMascaraSelecaoActionPerformed

    private void btnLimparSelecaoAtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparSelecaoAtualActionPerformed
        Classe c = getClasseSelecionada();
        if (c != null) {
            c.getSelecaoAmostras().clear();
            amostrasListModel.fireModelChanged();
            imageDisplay.repaint();
        }
    }//GEN-LAST:event_btnLimparSelecaoAtualActionPerformed

    private void btnPixelTool1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPixelTool1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPixelTool1ActionPerformed

    private void setFerramentaSelecionada(Ferramenta ferramentaSelecionada) {
        if (!this.ferramentaSelecionada.equals(ferramentaSelecionada)) {
            this.ferramentaSelecionada = ferramentaSelecionada;
            imageDisplay.repaint();
        }
    }

    private int getMagicWandValue() {
        return (Integer) edtMagicWandValue.getValue();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirImagem;
    private javax.swing.JButton btnAbrirSelecao;
    private javax.swing.JButton btnAdicionarClasse;
    private javax.swing.JButton btnGerarAmostras;
    private javax.swing.JButton btnImportarMascaraSelecao;
    private javax.swing.JButton btnLimparSelecaoAtual;
    private javax.swing.JToggleButton btnMagicWandTool;
    private javax.swing.JToggleButton btnPixelTool;
    private javax.swing.JToggleButton btnPixelTool1;
    private javax.swing.JButton btnRemoverClasse;
    private javax.swing.JButton btnSalvarSelecao;
    private javax.swing.JComboBox comboClasse;
    private javax.swing.JSpinner edtMagicWandValue;
    private javax.swing.JComboBox edtZoom;
    private javax.swing.ButtonGroup ferramentasGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblPixel;
    private javax.swing.JLabel lblPixelColor;
    private javax.swing.JLabel lblPosicao;
    private javax.swing.JList listAmostras;
    // End of variables declaration//GEN-END:variables

    private Classe getClasseSelecionada() {
        int idx = comboClasse.getSelectedIndex();
        if (idx > -1) {
            return classes.get(idx);
        } else {
            return null;
        }
    }

    private void addClasse(String nome) {
        Classe c = new Classe(nome);
        c.setNeuronio(classes.size());
        classes.add(c);
        classesComboBoxModel.fireItemAdded(classes.size() - 1);
        amostrasListModel.fireModelChanged();
    }

    private void removeClasse(int index) {
        classes.remove(index);
        classesComboBoxModel.fireItemRemoved(index);
        amostrasListModel.fireModelChanged();
    }

    private void abreImagem(File file) throws IOException {
        image = ImageIO.read(file);
        this.imageDisplay.setImage(image);
    }

    private void salvaSelecao(File file) throws IOException {
        SelecaoClasseWriter writer = new SelecaoClasseWriter(classes);
        writer.write(file);
    }

    private void abreSelecao(File file) throws IOException {

        SelecaoClasseReader reader = new SelecaoClasseReader(classes);
        List<Classe> list = reader.load(file);
        classes.clear();
        classes.addAll(list);

        classesComboBoxModel.fireContentsChanged();
        amostrasListModel.fireModelChanged();
        if (!list.isEmpty()) {
            comboClasse.setSelectedIndex(0);
        }

    }

    private void importaMascaraSelecao(File selectedFile) throws IOException {

        MascaraSelecaoBuilder builder = new MascaraSelecaoBuilder();
        List<Classe> list = builder.build(selectedFile);

        classes.clear();
        classes.addAll(list);

        classesComboBoxModel.fireContentsChanged();
        amostrasListModel.fireModelChanged();
        if (!classes.isEmpty()) {
            comboClasse.setSelectedIndex(0);
        }

    }

    private void gerarAmostras(int percValidacao) {

        AmostraColetaBuilder builder = new AmostraColetaBuilder(projeto);
        builder.setPercValidacao((double) percValidacao / 100.0d);
        builder.build(image, classes);
        this.amostrasPanel.setAmostrasTreinamento(builder.getAmostrasTreinamento());
        this.amostrasPanel.setAmostrasValidacao(builder.getAmostrasValidacao());

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
        if (!classes.isEmpty()) {
            comboClasse.setSelectedIndex(0);
        }
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
