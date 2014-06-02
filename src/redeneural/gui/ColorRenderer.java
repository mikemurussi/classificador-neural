package redeneural.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Michael Murussi
 */
public class ColorRenderer extends JLabel implements TableCellRenderer {

    Border unselectedBorder = null;
    Border selectedBorder = null;

    public ColorRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {

        Color newColor;
        if (color == null) {
            newColor = Color.white;
        } else {
            newColor = (Color) color;
        }
        setBackground(newColor);

        if (isSelected) {
            if (selectedBorder == null) {
                selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
            }
            setBorder(selectedBorder);
        } else {
            if (unselectedBorder == null) {
                unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
            }
            setBorder(unselectedBorder);
        }

        setToolTipText("RGB " + newColor.getRed() + ", " + newColor.getGreen() + ", " + newColor.getBlue());

        return this;
    }

}
