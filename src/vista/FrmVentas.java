package vista;

import conexion.Conexion;
import java.awt.Color;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Formulario de registro y consulta de ventas.
 * Las ventas se registran a traves del SP sp_registrar_venta.
 *
 * @author saram
 */
public class FrmVentas extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FrmVentas.class.getName());

    private final javax.swing.JFrame ventanaPadre;
    private DefaultTableModel modeloTabla;
    private int[] idsClientes;
    private int[] idsProductos;

    public FrmVentas(javax.swing.JFrame padre) {
        this.ventanaPadre = padre;
        initComponents();
        setLocationRelativeTo(null);
        aplicarDiseno();
        cargarClientes();
        cargarProductos();
        cargarVentas();
    }

    private void aplicarDiseno() {
        Diseno.aplicarIconoVentana(this);
        Diseno.ponerImagen(lblLogoMini, "/imagenes/logo_circular.png", 36, 36);
        Diseno.ponerImagen(lblIconoVenta, "/imagenes/ventas.png", 44, 44);
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Diseno.BORDE_ROSA));
        Diseno.estilizarTarjeta(pnlFormulario);
        Diseno.estilizarTarjeta(pnlTabla);
        pnlTotal.setBorder(BorderFactory.createLineBorder(Diseno.BORDE_ROSA, 1, true));
        Diseno.estilizarCombo(cmbcliente);
        Diseno.estilizarCombo(cmbproducto);
        Diseno.estilizarCampo(txtcantidad);
        Diseno.estilizarBotonSecundario(btnnuevocliente);
        Diseno.estilizarBotonPrimario(btnregistrar);
        Diseno.estilizarBotonSecundario(btnvolver);
        Diseno.estilizarTabla(tblventas, jScrollPane2);
        Diseno.aplicarSoloNumeros(txtcantidad, 4);

        cmbproducto.addActionListener(e -> actualizarPrecio());
    }

    private void cargarClientes() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT id_cliente, CONCAT(nombre,' ',apellido) FROM Cliente ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            java.util.List<String> nombres = new java.util.ArrayList<>();
            java.util.List<Integer> ids    = new java.util.ArrayList<>();
            while (rs.next()) { ids.add(rs.getInt(1)); nombres.add(rs.getString(2)); }
            rs.close(); ps.close();
            idsClientes = ids.stream().mapToInt(Integer::intValue).toArray();
            cmbcliente.setModel(new DefaultComboBoxModel<>(nombres.toArray(new String[0])));
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando clientes", ex); }
    }

    private void cargarProductos() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT id_producto, nombre, precio_venta FROM Producto WHERE stock > 0 ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            java.util.List<String> nombres = new java.util.ArrayList<>();
            java.util.List<Integer> ids    = new java.util.ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt(1));
                nombres.add(rs.getString(2) + "  ($" + String.format("%,.0f", rs.getDouble(3)) + ")");
            }
            rs.close(); ps.close();
            idsProductos = ids.stream().mapToInt(Integer::intValue).toArray();
            cmbproducto.setModel(new DefaultComboBoxModel<>(nombres.toArray(new String[0])));
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando productos", ex); }
    }

    private void actualizarPrecio() {
        // El precio ya se muestra en el combo; aqui podriamos calcular subtotal si se quiere
    }

    private void cargarVentas() {
        modeloTabla.setRowCount(0);
        try {
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_reporte_ventas()}");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_venta"),
                    rs.getString("fecha"),
                    rs.getString("nombre_cliente") + " " + rs.getString("apellido"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("precio_vendido"),
                    rs.getBigDecimal("subtotal")
                });
            }
            rs.close(); cs.close();
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando ventas", ex); }
    }

    private void registrarVenta() {
        if (idsClientes == null || idsClientes.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay clientes registrados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cantTxt = txtcantidad.getText().trim();
        if (cantTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa la cantidad.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int cantidad  = Integer.parseInt(cantTxt);
            int idCliente = idsClientes[cmbcliente.getSelectedIndex()];
            int idProd    = idsProductos[cmbproducto.getSelectedIndex()];
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_registrar_venta(?,?,?,?)}");
            cs.setInt(1, idCliente);
            cs.setInt(2, Login.idUsuario);
            cs.setInt(3, idProd);
            cs.setInt(4, cantidad);
            ResultSet rs = cs.executeQuery();
            String msg = "";
            if (rs.next()) msg = rs.getString("mensaje");
            rs.close(); cs.close();
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            txtcantidad.setText("");
            cargarProductos();
            cargarVentas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un numero.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error registrando venta", ex);
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        lblLogoMini = new javax.swing.JLabel();
        lblTituloHeader = new javax.swing.JLabel();
        btnvolver = new javax.swing.JButton();
        pnlContenido = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        lblIconoVenta = new javax.swing.JLabel();
        lblTituloForm = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        cmbcliente = new javax.swing.JComboBox<>();
        btnnuevocliente = new javax.swing.JButton();
        lblProducto = new javax.swing.JLabel();
        cmbproducto = new javax.swing.JComboBox<>();
        lblCantidad = new javax.swing.JLabel();
        txtcantidad = new javax.swing.JTextField();
        pnlTotal = new javax.swing.JPanel();
        btnregistrar = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        lblHistorial = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblventas = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Sistema Optica - Ventas");
        setMinimumSize(new java.awt.Dimension(880, 540));

        pnlPrincipal.setBackground(Diseno.FONDO_APP);

        // Header
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new java.awt.Dimension(880, 54));
        lblLogoMini.setText("");
        lblTituloHeader.setText("Modulo de Ventas");
        lblTituloHeader.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblTituloHeader.setForeground(Diseno.VINO_TITULO);
        btnvolver.setText("Volver");
        btnvolver.addActionListener(evt -> btnvolverActionPerformed(evt));

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(16).addComponent(lblLogoMini, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10).addComponent(lblTituloHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnvolver).addGap(16))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lblLogoMini, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblTituloHeader).addComponent(btnvolver)
        );

        // Panel contenido
        pnlContenido.setBackground(Diseno.FONDO_APP);
        pnlContenido.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));

        // Formulario
        pnlFormulario.setBackground(Color.WHITE);
        pnlFormulario.setPreferredSize(new java.awt.Dimension(270, 400));
        lblIconoVenta.setText(""); lblIconoVenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloForm.setText("Nueva Venta"); lblTituloForm.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14)); lblTituloForm.setForeground(Diseno.VINO_TITULO);
        lblCliente.setText("Cliente"); lblCliente.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        btnnuevocliente.setText("+ Nuevo cliente"); btnnuevocliente.addActionListener(evt -> btnnuevoclienteActionPerformed(evt));
        lblProducto.setText("Producto"); lblProducto.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblCantidad.setText("Cantidad"); lblCantidad.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        txtcantidad.setColumns(10);
        pnlTotal.setBackground(Diseno.ROSA_SUAVE);
        pnlTotal.setPreferredSize(new java.awt.Dimension(238, 30));
        btnregistrar.setText("Registrar venta"); btnregistrar.addActionListener(evt -> btnregistrarActionPerformed(evt));

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(pnlFormularioLayout.createSequentialGroup().addGap(16)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIconoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTituloForm)
                    .addComponent(lblCliente)
                    .addComponent(cmbcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnnuevocliente, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProducto)
                    .addComponent(cmbproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCantidad)
                    .addComponent(txtcantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createSequentialGroup().addGap(12)
                .addComponent(lblIconoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(6)
                .addComponent(lblTituloForm).addGap(10)
                .addComponent(lblCliente).addGap(3).addComponent(cmbcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(6)
                .addComponent(btnnuevocliente, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                .addComponent(lblProducto).addGap(3).addComponent(cmbproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                .addComponent(lblCantidad).addGap(3).addComponent(txtcantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                .addComponent(pnlTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                .addComponent(btnregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // Panel tabla
        pnlTabla.setBackground(Color.WHITE);
        pnlTabla.setPreferredSize(new java.awt.Dimension(530, 400));
        lblHistorial.setText("Historial de ventas"); lblHistorial.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13)); lblHistorial.setForeground(Diseno.VINO_TITULO);

        modeloTabla = new DefaultTableModel(new String[]{"#", "Fecha", "Cliente", "Producto", "Cant.", "Precio", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblventas.setModel(modeloTabla);
        jScrollPane2.setViewportView(tblventas);

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup().addGap(12)
                .addGroup(pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHistorial)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12))
        );
        pnlTablaLayout.setVerticalGroup(
            pnlTablaLayout.createSequentialGroup().addGap(16)
                .addComponent(lblHistorial).addGap(8)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addGap(16)
        );

        pnlContenido.add(pnlFormulario);
        pnlContenido.add(pnlTabla);

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(pnlContenido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnregistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnregistrarActionPerformed
        registrarVenta();
    }//GEN-LAST:event_btnregistrarActionPerformed

    private void btnnuevoclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnuevoclienteActionPerformed
        NuevoCliente dialogo = new NuevoCliente(this);
        dialogo.setVisible(true);
        if (dialogo.fueGuardado()) {
            cargarClientes();
            // Seleccionar el ultimo de la lista (el recien agregado suele quedar al final)
            if (cmbcliente.getItemCount() > 0) {
                cmbcliente.setSelectedIndex(cmbcliente.getItemCount() - 1);
            }
        }
    }//GEN-LAST:event_btnnuevoclienteActionPerformed

    private void btnvolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnvolverActionPerformed
        if (ventanaPadre != null) ventanaPadre.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnvolverActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnregistrar;
    private javax.swing.JButton btnnuevocliente;
    private javax.swing.JButton btnvolver;
    private javax.swing.JComboBox<String> cmbcliente;
    private javax.swing.JComboBox<String> cmbproducto;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblHistorial;
    private javax.swing.JLabel lblIconoVenta;
    private javax.swing.JLabel lblLogoMini;
    private javax.swing.JLabel lblProducto;
    private javax.swing.JLabel lblTituloForm;
    private javax.swing.JLabel lblTituloHeader;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JPanel pnlTotal;
    private javax.swing.JTable tblventas;
    private javax.swing.JTextField txtcantidad;
    // End of variables declaration//GEN-END:variables
}
