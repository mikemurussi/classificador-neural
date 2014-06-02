package redeneural.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.Scrollable;

/**
 *
 * @author Michael Murussi
 */
public class ImageDisplay extends JComponent implements Scrollable, MouseListener, FocusListener, MouseMotionListener {

    private Image image;
    private double zoom = 1.0;

    public ImageDisplay() {
        setFocusable(true);
        addMouseListener(this);
        addFocusListener(this);
    }

    public void setImage(Image image) {
        this.image = image;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return new Dimension((int) Math.round(image.getWidth(this) * zoom), (int) Math.round(image.getHeight(this) * zoom));
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void focusGained(FocusEvent e) { }

    @Override
    public void focusLost(FocusEvent e) { }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics g = graphics.create();

        g.setColor(Color.WHITE);
        if (image != null) {
            g.fillRect(0, 0, image.getWidth(this), image.getHeight(this));
            Graphics2D g2D = (Graphics2D) g;
            g2D.scale(zoom, zoom);
            g2D.drawImage(image, 0, 0, this);
        }

        g.dispose();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        revalidate();
        repaint();
    }

    public void transformPoint(Point2D orig, Point2D dest) {
        dest.setLocation(orig.getX() * zoom, orig.getY() * zoom);
    }

    public void restorePoint(Point2D orig, Point2D dest) {
        dest.setLocation(orig.getX() / zoom, orig.getY() / zoom);
    }

}
