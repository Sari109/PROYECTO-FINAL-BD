package vista;

import conexion.Conexion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Catalogo de productos.
 * Administrador puede crear y editar; Optometrista solo consulta.
 *
 * @author saram
 */
public class FrmProductos extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FrmProductos.class.getName());

    private final JFrame ventanaPadre;
    private final String rolActual;
    private DefaultTableModel modeloTabla;

    private JPanel pnlHeader;
    private JLabel lblLogoMini;
    private JLabel lblTitulo;
    private JButton btnvolver;

    private JPanel pnlFormulario;
    private JComboBox<String> cmbcategoria;
    private JTextField txtnombre;
    private JTextField txtprecio;
    private JTextField txtstock;
    private JButton btnguardar;
    private JButton btnlimpiar;

    private JPanel pnlTabla;
    private JTextField txtbuscar;
    private JTable tblproductos;
    private JScrollPane scroll;

    private int[] idsCategorias;
    private int idProductoSeleccionado = 0;

    public FrmProductos(JFrame padre, String rol) {
        this.ventanaPadre = padre;
        this.rolActual = rol;
        initComponents();
        setLocationRelativeTo(null);
        Diseno.aplicarIconoVentana(this);
        aplicarDiseno();
        cargarCategorias();
        buscarProductos("");
    }

    private void initComponents() {
        setTitle("Sistema Optica - Productos");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        getContentPane().setBackground(Diseno.FONDO_APP);
        getContentPane().setLayout(new BorderLayout());

        pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(900, 56));
        lblLogoMini = new JLabel();
        Diseno.ponerImagen(lblLogoMini, "/imagenes/logo_circular.png", 36, 36);
        lblTitulo = new JLabel("Catalogo de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Diseno.VINO_TITULO);
        btnvolver = new JButton("Volver");
        btnvolver.addActionListener(e -> volverMenu());
        pnlHeader.add(lblLogoMini); pnlHeader.add(lblTitulo); pnlHeader.add(Box.createHorizontalStrut(400)); pnlHeader.add(btnvolver);
        getContentPane().add(pnlHeader, BorderLayout.NORTH);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centro.setBackground(Diseno.FONDO_APP);

        boolean esAdmin = "Administrador".equals(rolActual);

        pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setPreferredSize(new Dimension(260, 380));
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel lblTituloForm = new JLabel("Datos del Producto");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloForm.setForeground(Diseno.VINO_TITULO); lblTituloForm.setAlignmentX(LEFT_ALIGNMENT);

        cmbcategoria = new JComboBox<>(); cmbcategoria.setMaximumSize(new Dimension(240,32)); cmbcategoria.setAlignmentX(LEFT_ALIGNMENT); cmbcategoria.setEnabled(esAdmin);
        txtnombre = new JTextField(); txtnombre.setMaximumSize(new Dimension(240,32)); txtnombre.setAlignmentX(LEFT_ALIGNMENT); txtnombre.setEditable(esAdmin);
        txtprecio = new JTextField(); txtprecio.setMaximumSize(new Dimension(240,32)); txtprecio.setAlignmentX(LEFT_ALIGNMENT); txtprecio.setEditable(esAdmin);
        txtstock  = new JTextField(); txtstock.setMaximumSize(new Dimension(240,32));  txtstock.setAlignmentX(LEFT_ALIGNMENT);  txtstock.setEditable(esAdmin);

        btnguardar = new JButton("Guardar producto"); btnguardar.setMaximumSize(new Dimension(240,36)); btnguardar.setAlignmentX(LEFT_ALIGNMENT); btnguardar.setEnabled(esAdmin);
        btnlimpiar = new JButton("Limpiar");          btnlimpiar.setMaximumSize(new Dimension(240,36)); btnlimpiar.setAlignmentX(LEFT_ALIGNMENT);

        btnguardar.addActionListener(e -> guardarProducto());
        btnlimpiar.addActionListener(e -> limpiarFormulario());

        pnlFormulario.add(lblTituloForm); pnlFormulario.add(Box.createVerticalStrut(10));
        pnlFormulario.add(etiqueta("Categoria:")); pnlFormulario.add(cmbcategoria); pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Nombre:")); pnlFormulario.add(txtnombre); pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Precio de venta:")); pnlFormulario.add(txtprecio); pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Stock (unidades):")); pnlFormulario.add(txtstock); pnlFormulario.add(Box.createVerticalStrut(14));
        pnlFormulario.add(btnguardar); pnlFormulario.add(Box.createVerticalStrut(6)); pnlFormulario.add(btnlimpiar);
        if (!esAdmin) {
            JLabel aviso = new JLabel("Modo consulta (solo lectura)");
            aviso.setFont(new Font("Segoe UI", Font.ITALIC, 11)); aviso.setForeground(Diseno.TEXTO_SUAVE); aviso.setAlignmentX(LEFT_ALIGNMENT);
            pnlFormulario.add(Box.createVerticalStrut(8)); pnlFormulario.add(aviso);
        }

        pnlTabla = new JPanel(new BorderLayout(0, 8));
        pnlTabla.setPreferredSize(new Dimension(560, 380));
        txtbuscar = new JTextField(); txtbuscar.setToolTipText("Buscar...");
        txtbuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { buscarProductos(txtbuscar.getText().trim()); }
        });
        modeloTabla = new DefaultTableModel(new String[]{"#","Nombre","Categoria","Precio","Stock"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblproductos = new JTable(modeloTabla);
        tblproductos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                Object stockVal = modeloTabla.getValueAt(row, 4);
                int stock = 0;
                try { stock = Integer.parseInt(stockVal.toString()); } catch (Exception ex) {}
                if (!sel) { c.setBackground(stock < 5 ? new Color(255,235,200) : Color.WHITE); c.setForeground(Diseno.TEXTO_OSCURO); }
                return c;
            }
        });
        scroll = new JScrollPane(tblproductos); scroll.setPreferredSize(new Dimension(560,330));
        tblproductos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblproductos.getSelectedRow();
            if (fila >= 0 && esAdmin) {
                idProductoSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
                txtnombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtprecio.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtstock.setText(modeloTabla.getValueAt(fila, 4).toString());
                String cat = modeloTabla.getValueAt(fila, 2).toString();
                for (int i = 0; i < cmbcategoria.getItemCount(); i++) { if (cmbcategoria.getItemAt(i).equals(cat)) { cmbcategoria.setSelectedIndex(i); break; } }
            }
        });
        JPanel barraBuscar = new JPanel(new BorderLayout(6,0)); barraBuscar.setOpaque(false);
        barraBuscar.add(new JLabel("Buscar: "), BorderLayout.WEST); barraBuscar.add(txtbuscar, BorderLayout.CENTER);
        JLabel stockAviso = new JLabel("  Fondo naranja = stock bajo (menos de 5 unidades)");
        stockAviso.setFont(new Font("Segoe UI", Font.ITALIC, 11)); stockAviso.setForeground(Diseno.TEXTO_SUAVE);
        pnlTabla.add(barraBuscar, BorderLayout.NORTH);
        pnlTabla.add(scroll, BorderLayout.CENTER);
        pnlTabla.add(stockAviso, BorderLayout.SOUTH);

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
        Diseno.estilizarCampo(txtnombre); Diseno.estilizarCampo(txtprecio); Diseno.estilizarCampo(txtstock); Diseno.estilizarCampo(txtbuscar);
        Diseno.estilizarCombo(cmbcategoria);
        Diseno.estilizarBotonPrimario(btnguardar); Diseno.estilizarBotonSecundario(btnlimpiar); Diseno.estilizarBotonSecundario(btnvolver);
        Diseno.estilizarTabla(tblproductos, scroll);
        Diseno.aplicarSoloNumeros(txtstock, 6);
    }

    private void cargarCategorias() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_categoria, nombre FROM Categoria ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            java.util.List<String> n = new java.util.ArrayList<>(); java.util.List<Integer> ids = new java.util.ArrayList<>();
            while (rs.next()) { ids.add(rs.getInt(1)); n.add(rs.getString(2)); }
            rs.close(); ps.close();
            idsCategorias = ids.stream().mapToInt(Integer::intValue).toArray();
            cmbcategoria.setModel(new DefaultComboBoxModel<>(n.toArray(new String[0])));
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando categorias", ex); }
    }

    private void buscarProductos(String busqueda) {
        modeloTabla.setRowCount(0);
        try {
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_listar_productos(?)}");
            cs.setString(1, busqueda.isEmpty() ? null : busqueda);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_producto"), rs.getString("nombre"), rs.getString("categoria"),
                    rs.getBigDecimal("precio_venta"), rs.getInt("stock")
                });
            }
            rs.close(); cs.close();
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error buscando productos", ex); }
    }

    private void guardarProducto() {
        if (idsCategorias == null || idsCategorias.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay categorias registradas.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        String nombre = txtnombre.getText().trim(); String precioTxt = txtprecio.getText().trim(); String stockTxt = txtstock.getText().trim();
        if (nombre.isEmpty() || precioTxt.isEmpty() || stockTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            double precio = Double.parseDouble(precioTxt); int stock = Integer.parseInt(stockTxt);
            int idCat = idsCategorias[cmbcategoria.getSelectedIndex()];
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_guardar_producto(?,?,?,?,?)}");
            cs.setInt(1, idProductoSeleccionado); cs.setInt(2, idCat); cs.setString(3, nombre); cs.setDouble(4, precio); cs.setInt(5, stock);
            ResultSet rs = cs.executeQuery();
            String msg = ""; if (rs.next()) msg = rs.getString("mensaje");
            rs.close(); cs.close();
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario(); buscarProductos("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio y el stock deben ser numeros.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error guardando producto", ex);
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() { txtnombre.setText(""); txtprecio.setText(""); txtstock.setText(""); idProductoSeleccionado = 0; tblproductos.clearSelection(); }
    private void volverMenu() { if (ventanaPadre != null) ventanaPadre.setVisible(true); this.dispose(); }
}
