package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.JTableHeader;

/**
 * Colores y herramientas visuales compartidas por todas las vistas del sistema.
 *
 * @author saram
 */
public final class Diseno {

    public static final Color FONDO_APP    = new Color(255, 249, 251);
    public static final Color FONDO_PASTEL = new Color(253, 223, 233);
    public static final Color FONDO_BANNER = new Color(255, 223, 234);
    public static final Color ROSA_SUAVE   = new Color(252, 234, 241);
    public static final Color ROSA_ACENTO  = new Color(223, 79, 130);
    public static final Color ROSA_HOVER   = new Color(201, 59, 110);
    public static final Color VINO_TITULO  = new Color(143, 46, 86);
    public static final Color TEXTO_OSCURO = new Color(46, 34, 38);
    public static final Color TEXTO_SUAVE  = new Color(138, 107, 118);
    public static final Color BORDE_ROSA   = new Color(239, 202, 216);
    public static final Color FONDO_CAMPO  = new Color(255, 249, 251);

    private static final Logger LOGGER = Logger.getLogger(Diseno.class.getName());

    private Diseno() {}

    /** Pone el logo circular como icono de la ventana en varias resoluciones. */
    public static void aplicarIconoVentana(JFrame frame) {
        BufferedImage logo = cargarImagen("/imagenes/logo_circular.png");
        if (logo != null) {
            frame.setIconImages(Arrays.<Image>asList(
                    escalarProgresivo(logo, 16, 16),
                    escalarProgresivo(logo, 32, 32),
                    escalarProgresivo(logo, 48, 48),
                    escalarProgresivo(logo, 64, 64),
                    escalarProgresivo(logo, 128, 128)));
        }
    }

    /** Coloca una imagen nitida dentro de un JLabel. */
    public static void ponerImagen(JLabel label, String ruta, int ancho, int alto) {
        BufferedImage original = cargarImagen(ruta);
        if (original == null) return;
        label.setIcon(new Icon() {
            private BufferedImage cache;
            private int cacheW = -1, cacheH = -1;

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                AffineTransform tx = g2.getTransform();
                double sx = Math.max(1.0, tx.getScaleX());
                double sy = Math.max(1.0, tx.getScaleY());
                int pw = (int) Math.round(ancho * sx);
                int ph = (int) Math.round(alto  * sy);
                if (cache == null || cacheW != pw || cacheH != ph) {
                    cache = escalarProgresivo(original, pw, ph);
                    cacheW = pw; cacheH = ph;
                }
                if (tx.getShearX() == 0.0 && tx.getShearY() == 0.0) {
                    g2.translate(x, y);
                    g2.scale(1.0 / sx, 1.0 / sy);
                    g2.drawImage(cache, 0, 0, null);
                } else {
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(cache, x, y, ancho, alto, null);
                }
                g2.dispose();
            }
            @Override public int getIconWidth()  { return ancho; }
            @Override public int getIconHeight() { return alto; }
        });
    }

    /** Estiliza un campo de texto con borde rosado y resaltado al enfocar. */
    public static void estilizarCampo(JTextField campo) {
        campo.setBackground(FONDO_CAMPO);
        campo.setForeground(TEXTO_OSCURO);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setCaretColor(ROSA_ACENTO);
        Border normal  = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_ROSA, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10));
        Border foco    = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_ACENTO, 2, true),
                BorderFactory.createEmptyBorder(4, 9, 4, 9));
        campo.setBorder(normal);
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { campo.setBorder(foco); }
            @Override public void focusLost(FocusEvent e)   { campo.setBorder(normal); }
        });
    }

    /** Estiliza un JComboBox con la paleta rosa. */
    public static void estilizarCombo(JComboBox<?> combo) {
        combo.setBackground(FONDO_CAMPO);
        combo.setForeground(TEXTO_OSCURO);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(BORDE_ROSA, 1, true));
    }

    /** Boton principal: fondo rosa intenso, texto blanco. */
    public static void estilizarBotonPrimario(JButton btn) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(ROSA_ACENTO);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(ROSA_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(ROSA_ACENTO); }
        });
    }

    /** Boton secundario: fondo blanco, borde rosado. */
    public static void estilizarBotonSecundario(JButton btn) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(Color.WHITE);
        btn.setForeground(VINO_TITULO);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_ROSA, 1, true),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(ROSA_SUAVE); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
    }

    /** Panel blanco con borde rosado tipo tarjeta. */
    public static void estilizarTarjeta(JPanel panel) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDE_ROSA, 1, true));
    }

    /** Estiliza la cabecera, seleccion y colores de una JTable y su JScrollPane. */
    public static void estilizarTabla(JTable tabla, JScrollPane scroll) {
        tabla.setDefaultEditor(Object.class, null);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setForeground(TEXTO_OSCURO);
        tabla.setBackground(Color.WHITE);
        tabla.setGridColor(new Color(245, 224, 232));
        tabla.setSelectionBackground(ROSA_SUAVE);
        tabla.setSelectionForeground(VINO_TITULO);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(ROSA_SUAVE);
        header.setForeground(VINO_TITULO);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 32));
        header.setReorderingAllowed(false);

        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE_ROSA, 1, true));
    }

    /** Solo digitos, con limite de caracteres. */
    public static void aplicarSoloNumeros(JTextField campo, int max) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                boolean esBorrado = (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE);
                if (!esBorrado && (!Character.isDigit(c) || campo.getText().length() >= max)) evt.consume();
            }
        });
    }

    /** Solo letras y espacios, con limite de caracteres. */
    public static void aplicarSoloLetras(JTextField campo, int max) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                boolean esBorrado = (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE);
                if (!esBorrado && (!(Character.isLetter(c) || c == ' ') || campo.getText().length() >= max)) evt.consume();
            }
        });
    }

    /** Limite de caracteres general. */
    public static void aplicarLimite(JTextField campo, int max) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                boolean esBorrado = (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE);
                if (!esBorrado && campo.getText().length() >= max) evt.consume();
            }
        });
    }

    private static BufferedImage cargarImagen(String ruta) {
        URL url = Diseno.class.getResource(ruta);
        if (url == null) return null;
        try {
            return ImageIO.read(url);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "No se pudo cargar: " + ruta, ex);
            return null;
        }
    }

    private static BufferedImage escalarProgresivo(BufferedImage origen, int w, int h) {
        if (origen.getWidth() == w && origen.getHeight() == h) return origen;
        Image area = new ImageIcon(origen.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING)).getImage();
        BufferedImage resultado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resultado.createGraphics();
        g2.drawImage(area, 0, 0, null);
        g2.dispose();
        return resultado;
    }
}
