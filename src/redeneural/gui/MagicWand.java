package redeneural.gui;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Michael Murussi
 */
public class MagicWand {

    private BufferedImage image;
    private int limiar = 5;
    private final Set<Point> selection = new HashSet<>(200);
    private static final double DISTANCIA_MAXIMA = distanciaEuclidiana(new int[]{255, 255, 255}, new int[]{0, 0, 0});

    public void select(Point point) {

        selection.clear();
        Stack<Point> stack = new Stack<>();
        Set<Point> visited = new HashSet<>(200);

        stack.push(point);
        visited.add(point);
        selection.add(point);

        int w = image.getWidth();
        int h = image.getHeight();

        int rgb = image.getRGB(point.x, point.y);

        while (!stack.empty()) {
            Point p = stack.pop();            

            Point[] vizinhos = getVizinhos(p);
            for (Point pv : vizinhos) {
                if ((pv.x >= 0) && (pv.x < w) && (pv.y >= 0) && (pv.y < h)) {
                    if (!visited.contains(pv)) {
                        visited.add(pv);
                        double d = distanciaEuclidiana(rgb, image.getRGB(pv.x, pv.y));
                        if ((d / DISTANCIA_MAXIMA) <= (limiar / 100.0d)) {
                            stack.push(pv);
                            selection.add(pv);
                        }
                    }
                }
            }
        }

    }

    private static double distanciaEuclidiana(int rgb1, int rgb2) {
        int[] a = new int[]{(rgb1 >> 16) & 0xff, (rgb1 >> 8) & 0xFF, rgb1 & 0xff};
        int[] b = new int[]{(rgb2 >> 16) & 0xff, (rgb2 >> 8) & 0xFF, rgb2 & 0xff};
        return distanciaEuclidiana(a, b);
    }

    private static double distanciaEuclidiana(int[] a, int[] b) {
        return Math.sqrt((a[0]-b[0]) * (a[0]-b[0]) + (a[1]-b[1]) * (a[1]-b[1]) + (a[2]-b[2]) * (a[2]-b[2]));
    }

    private static Point[] getVizinhos(Point p) {

        return new Point[]{
            // em y-1
            new Point(p.x - 1, p.y - 1),
            new Point(p.x, p.y - 1),
            new Point(p.x + 1, p.y - 1),
            // em y
            new Point(p.x - 1, p.y),
            new Point(p.x + 1, p.y),
            // em y+1
            new Point(p.x - 1, p.y + 1),
            new Point(p.x, p.y + 1),
            new Point(p.x + 1, p.y + 1)
        };

    }

    public int getLimiar() {
        return limiar;
    }

    public void setLimiar(int limiar) {
        this.limiar = limiar;
    }

    public Set<Point> getSelection() {
        return selection;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
