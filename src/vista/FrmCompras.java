package vista;

import conexion.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Formulario para registrar compras a proveedores (solo Administrador).
 * Al registrar una compra el stock del producto aumenta automaticamente.
 *
 * @author saram
 */
public class FrmCompras extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FrmCompras.class.getName());

    private final JFrame ventanaPadre;
    private DefaultTableModel modeloTabla;

    private JPanel pnlHeader;
    private JLabel lblLogoMini;
    private JLabel lblTitulo;
    private JButton btnvolver;

    private JPanel pnlFormulario;
    private JComboBox<String> cmbproveedor;
    private JComboBox<String> cmbproducto;
    private JTextField txtcantidad;
    private JTextField txtpreciocompra;
    private JButton btnregistrar;
    private JButton btnlimpiar;

    private JPanel pnlTabla;
    private JTable tblcompras;
    private JScrollPane scroll;

    private int[] idsProveedores;
    private int[] idsProductos;

    public FrmCompras(JFrame padre) {
        this.ventanaPadre = padre;
        initComponents();
        setLocationRelativeTo(null);
        Diseno.aplicarIconoVentana(this);
        aplicarDiseno();
        cargarProveedores();
        cargarProductos();
        cargarCompras();
    }

    private void initComponents() {
        setTitle("Sistema Optica - Compras");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 540));
        getContentPane().setBackground(Diseno.FONDO_APP);
        getContentPane().setLayout(new BorderLayout());

        pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(900, 56));
        lblLogoMini = new JLabel();
        Diseno.ponerImagen(lblLogoMini, "/imagenes/logo_circular.png", 36, 36);
        lblTitulo = new JLabel("Registro de Compras");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16)); lblTitulo.setForeground(Diseno.VINO_TITULO);
        btnvolver = new JButton("Volver"); btnvolver.addActionListener(e -> volverMenu());
        pnlHeader.add(lblLogoMini); pnlHeader.add(lblTitulo); pnlHeader.add(Box.createHorizontalStrut(400)); pnlHeader.add(btnvolver);
        getContentPane().add(pnlHeader, BorderLayout.NORTH);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centro.setBackground(Diseno.FONDO_APP);

        pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setPreferredSize(new Dimension(260, 360));
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel lblTituloForm = new JLabel("Nueva Compra");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloForm.setForeground(Diseno.VINO_TITULO); lblTituloForm.setAlignmentX(LEFT_ALIGNMENT);

        cmbproveedor   = new JComboBox<>(); cmbproveedor.setMaximumSize(new Dimension(240,32));   cmbproveedor.setAlignmentX(LEFT_ALIGNMENT);
        cmbproducto    = new JComboBox<>(); cmbproducto.setMaximumSize(new Dimension(240,32));    cmbproducto.setAlignmentX(LEFT_ALIGNMENT);
        txtcantidad    = new JTextField(); txtcantidad.setMaximumSize(new Dimension(240,32));    txtcantidad.setAlignmentX(LEFT_ALIGNMENT);
        txtpreciocompra= new JTextField(); txtpreciocompra.setMaximumSize(new Dimension(240,32));txtpreciocompra.setAlignmentX(LEFT_ALIGNMENT);

        btnregistrar = new JButton("Registrar compra"); btnregistrar.setMaximumSize(new Dimension(240,36)); btnregistrar.setAlignmentX(LEFT_ALIGNMENT);
        btnlimpiar   = new JButton("Limpiar");          btnlimpiar.setMaximumSize(new Dimension(240,36));   btnlimpiar.setAlignmentX(LEFT_ALIGNMENT);
        btnregistrar.addActionListener(e -> registrarCompra());
        btnlimpiar.addActionListener(e -> limpiarFormulario());

        pnlFormulario.add(lblTituloForm); pnlFormulario.add(Box.createVerticalStrut(10));
        pnlFormulario.add(etiqueta("Proveedor:")); pnlFormulario.add(cmbproveedor); pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Producto:")); pnlFormulario.add(cmbproducto); pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Cantidad:")); pnlFormulario.add(txtcantidad); pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Precio de compra (por unidad):")); pnlFormulario.add(txtpreciocompra); pnlFormulario.add(Box.createVerticalStrut(14));
        pnlFormulario.add(btnregistrar); pnlFormulario.add(Box.createVerticalStrut(6)); pnlFormulario.add(btnlimpiar);

        pnlTabla = new JPanel(new BorderLayout(0,8));
        pnlTabla.setPreferredSize(new Dimension(560, 360));
        modeloTabla = new DefaultTableModel(new String[]{"#Compra","Proveedor","Producto","Cantidad","Precio Unit.","Total","Fecha"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblcompras = new JTable(modeloTabla);
        scroll = new JScrollPane(tblcompras); scroll.setPreferredSize(new Dimension(560,330));
        JLabel lblHistorial = new JLabel("Historial de compras");
        lblHistorial.setFont(new Font("Segoe UI", Font.BOLD, 13)); lblHistorial.setForeground(Diseno.VINO_TITULO);
        pnlTabla.add(lblHistorial, BorderLayout.NORTH);
        pnlTabla.add(scroll, BorderLayout.CENTER);

        centro.add(pnlFormulario); centro.add(pnlTabla);
        getContentPane().add(centro, BorderLayout.CENTER);
        pack();
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Diseno.TEXTO_OSCURO); lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private void aplicarDiseno() {
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Diseno.BORDE_ROSA));
        Diseno.estilizarTarjeta(pnlFormulario); Diseno.estilizarTarjeta(pnlTabla);
        Diseno.estilizarCampo(txtcantidad); Diseno.estilizarCampo(txtpreciocompra);
        Diseno.estilizarCombo(cmbproveedor); Diseno.estilizarCombo(cmbproducto);
        Diseno.estilizarBotonPrimario(btnregistrar); Diseno.estilizarBotonSecundario(btnlimpiar); Diseno.estilizarBotonSecundario(btnvolver);
        Diseno.estilizarTabla(tblcompras, scroll);
        Diseno.aplicarSoloNumeros(txtcantidad, 6);
    }

    private void cargarProveedores() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_proveedor, nombre FROM Proveedor ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            java.util.List<String> n = new java.util.ArrayList<>(); java.util.List<Integer> ids = new java.util.ArrayList<>();
            while (rs.next()) { ids.add(rs.getInt(1)); n.add(rs.getString(2)); }
            rs.close(); ps.close();
            idsProveedores = ids.stream().mapToInt(Integer::intValue).toArray();
            cmbproveedor.setModel(new DefaultComboBoxModel<>(n.toArray(new String[0])));
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando proveedores", ex); }
    }

    private void cargarProductos() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_producto, nombre FROM Producto ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            java.util.List<String> n = new java.util.ArrayList<>(); java.util.List<Integer> ids = new java.util.ArrayList<>();
            while (rs.next()) { ids.add(rs.getInt(1)); n.add(rs.getString(2)); }
            rs.close(); ps.close();
            idsProductos = ids.stream().mapToInt(Integer::intValue).toArray();
            cmbproducto.setModel(new DefaultComboBoxModel<>(n.toArray(new String[0])));
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando productos", ex); }
    }

    private void cargarCompras() {
        modeloTabla.setRowCount(0);
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT c.id_compra, pv.nombre AS proveedor, p.nombre AS producto, " +
                "       dc.cantidad, dc.precio_compra, (dc.cantidad * dc.precio_compra) AS total, c.fecha " +
                "FROM Compra c " +
                "INNER JOIN Proveedor pv ON c.id_proveedor = pv.id_proveedor " +
                "INNER JOIN Detalle_Compra dc ON c.id_compra = dc.id_compra " +
                "INNER JOIN Producto p ON dc.id_producto = p.id_producto " +
                "ORDER BY c.fecha DESC, c.id_compra");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_compra"), rs.getString("proveedor"), rs.getString("producto"),
                    rs.getInt("cantidad"), rs.getBigDecimal("precio_compra"), rs.getBigDecimal("total"), rs.getString("fecha")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando compras", ex); }
    }

    private void registrarCompra() {
        if (idsProveedores == null || idsProveedores.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay proveedores registrados.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        String cantTxt = txtcantidad.getText().trim(); String precioTxt = txtpreciocompra.getText().trim();
        if (cantTxt.isEmpty() || precioTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa la cantidad y el precio de compra.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            int cantidad = Integer.parseInt(cantTxt); double precio = Double.parseDouble(precioTxt);
            int idProv = idsProveedores[cmbproveedor.getSelectedIndex()];
            int idProd = idsProductos[cmbproducto.getSelectedIndex()];
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_registrar_compra(?,?,?,?)}");
            cs.setInt(1, idProv); cs.setInt(2, idProd); cs.setInt(3, cantidad); cs.setDouble(4, precio);
            ResultSet rs = cs.executeQuery();
            String msg = ""; if (rs.next()) msg = rs.getString("mensaje");
            rs.close(); cs.close();
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario(); cargarCompras();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad y el precio deben ser numeros.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error registrando compra", ex);
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() { txtcantidad.setText(""); txtpreciocompra.setText(""); }
    private void volverMenu() { if (ventanaPadre != null) ventanaPadre.setVisible(true); this.dispose(); }
}
