package vista;

import conexion.Conexion;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Formulario de gestion de clientes.
 * Permite buscar, registrar y actualizar clientes mediante sp_guardar_cliente.
 *
 * @author saram
 */
public class FrmClientes extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FrmClientes.class.getName());

    private final javax.swing.JFrame ventanaPadre;
    private DefaultTableModel modeloTabla;

    public FrmClientes(javax.swing.JFrame padre) {
        this.ventanaPadre = padre;
        initComponents();
        setLocationRelativeTo(null);
        aplicarDiseno();
        buscarClientes("");
    }

    private void aplicarDiseno() {
        Diseno.aplicarIconoVentana(this);
        Diseno.ponerImagen(lblLogoMini, "/imagenes/logo_circular.png", 36, 36);
        Diseno.ponerImagen(lblIconoCliente, "/imagenes/clientes.png", 44, 44);
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Diseno.BORDE_ROSA));
        Diseno.estilizarTarjeta(pnlFormulario);
        Diseno.estilizarTarjeta(pnlTabla);
        Diseno.estilizarCampo(txtcedula);
        Diseno.estilizarCampo(txtnombre);
        Diseno.estilizarCampo(txtapellido);
        Diseno.estilizarCampo(txttelefono);
        Diseno.estilizarCampo(txtcorreo);
        Diseno.estilizarCampo(txtbuscar);
        Diseno.estilizarBotonPrimario(btnguardar);
        Diseno.estilizarBotonSecundario(btnlimpiar);
        Diseno.estilizarBotonSecundario(btnvolver);
        Diseno.estilizarTabla(tblclientes, jScrollPane1);
        Diseno.aplicarSoloNumeros(txtcedula, 15);
        Diseno.aplicarSoloNumeros(txttelefono, 15);
        Diseno.aplicarSoloLetras(txtnombre, 50);
        Diseno.aplicarSoloLetras(txtapellido, 50);
        Diseno.aplicarLimite(txtcorreo, 100);
    }

    private void buscarClientes(String filtro) {
        modeloTabla.setRowCount(0);
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT id_cliente, nombre, apellido, cedula, telefono, correo " +
                "FROM Cliente WHERE nombre LIKE ? OR apellido LIKE ? OR cedula LIKE ? " +
                "ORDER BY apellido, nombre");
            String like = "%" + filtro + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("cedula"),
                    rs.getString("telefono"),
                    rs.getString("correo")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error buscando clientes", ex);
        }
    }

    private void guardarCliente() {
        String cedula   = txtcedula.getText().trim();
        String nombre   = txtnombre.getText().trim();
        String apellido = txtapellido.getText().trim();
        String telefono = txttelefono.getText().trim();
        String correo   = txtcorreo.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cedula, nombre y apellido son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (NuevoCliente.guardarEnBD(this, cedula, nombre, apellido, telefono, correo)) {
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            buscarClientes("");
        }
    }

    private void limpiarFormulario() {
        txtcedula.setText(""); txtnombre.setText(""); txtapellido.setText("");
        txttelefono.setText(""); txtcorreo.setText("");
        tblclientes.clearSelection();
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
        lblIconoCliente = new javax.swing.JLabel();
        lblTituloForm = new javax.swing.JLabel();
        lblCedula = new javax.swing.JLabel();
        txtcedula = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtnombre = new javax.swing.JTextField();
        lblApellido = new javax.swing.JLabel();
        txtapellido = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txttelefono = new javax.swing.JTextField();
        lblCorreo = new javax.swing.JLabel();
        txtcorreo = new javax.swing.JTextField();
        btnguardar = new javax.swing.JButton();
        btnlimpiar = new javax.swing.JButton();
        pnlTabla = new javax.swing.JPanel();
        lblBuscarTitulo = new javax.swing.JLabel();
        txtbuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblclientes = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Sistema Optica - Clientes");
        setMinimumSize(new java.awt.Dimension(860, 540));

        pnlPrincipal.setBackground(Diseno.FONDO_APP);

        // Header
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new java.awt.Dimension(860, 54));
        lblLogoMini.setText("");
        lblTituloHeader.setText("Gestion de Clientes");
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
        pnlFormulario.setPreferredSize(new java.awt.Dimension(270, 440));
        lblIconoCliente.setText(""); lblIconoCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloForm.setText("Datos del Cliente"); lblTituloForm.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14)); lblTituloForm.setForeground(Diseno.VINO_TITULO);
        lblCedula.setText("Cedula"); lblCedula.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        txtcedula.setColumns(18);
        lblNombre.setText("Nombre"); lblNombre.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        txtnombre.setColumns(18);
        lblApellido.setText("Apellido"); lblApellido.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        txtapellido.setColumns(18);
        lblTelefono.setText("Telefono"); lblTelefono.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        txttelefono.setColumns(18);
        lblCorreo.setText("Correo"); lblCorreo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        txtcorreo.setColumns(18);
        btnguardar.setText("Guardar cliente"); btnguardar.addActionListener(evt -> btnguardarActionPerformed(evt));
        btnlimpiar.setText("Limpiar"); btnlimpiar.addActionListener(evt -> btnlimpiarActionPerformed(evt));

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(pnlFormularioLayout.createSequentialGroup().addGap(16)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIconoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTituloForm)
                    .addComponent(lblCedula).addComponent(txtcedula, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombre).addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblApellido).addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelefono).addComponent(txttelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCorreo).addComponent(txtcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnguardar, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createSequentialGroup().addGap(12)
                .addComponent(lblIconoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(6)
                .addComponent(lblTituloForm).addGap(10)
                .addComponent(lblCedula).addGap(3).addComponent(txtcedula, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(lblNombre).addGap(3).addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(lblApellido).addGap(3).addComponent(txtapellido, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(lblTelefono).addGap(3).addComponent(txttelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8)
                .addComponent(lblCorreo).addGap(3).addComponent(txtcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12)
                .addComponent(btnguardar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(6)
                .addComponent(btnlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // Panel tabla
        pnlTabla.setBackground(Color.WHITE);
        pnlTabla.setPreferredSize(new java.awt.Dimension(510, 440));
        lblBuscarTitulo.setText("Buscar:"); lblBuscarTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        txtbuscar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { buscarClientes(txtbuscar.getText().trim()); }
        });

        modeloTabla = new DefaultTableModel(new String[]{"#", "Nombre", "Apellido", "Cedula", "Telefono", "Correo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblclientes.setModel(modeloTabla);
        jScrollPane1.setViewportView(tblclientes);

        tblclientes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblclientes.getSelectedRow();
            if (fila >= 0) {
                txtnombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtapellido.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtcedula.setText(modeloTabla.getValueAt(fila, 3).toString());
                txttelefono.setText(modeloTabla.getValueAt(fila, 4) != null ? modeloTabla.getValueAt(fila, 4).toString() : "");
                txtcorreo.setText(modeloTabla.getValueAt(fila, 5) != null ? modeloTabla.getValueAt(fila, 5).toString() : "");
            }
        });

        javax.swing.GroupLayout pnlTablaLayout = new javax.swing.GroupLayout(pnlTabla);
        pnlTabla.setLayout(pnlTablaLayout);
        pnlTablaLayout.setHorizontalGroup(
            pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTablaLayout.createSequentialGroup().addGap(12)
                .addGroup(pnlTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBuscarTitulo)
                    .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12))
        );
        pnlTablaLayout.setVerticalGroup(
            pnlTablaLayout.createSequentialGroup().addGap(16)
                .addComponent(lblBuscarTitulo).addGap(4)
                .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
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

    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
        guardarCliente();
    }//GEN-LAST:event_btnguardarActionPerformed

    private void btnlimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlimpiarActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_btnlimpiarActionPerformed

    private void btnvolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnvolverActionPerformed
        if (ventanaPadre != null) ventanaPadre.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnvolverActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnguardar;
    private javax.swing.JButton btnlimpiar;
    private javax.swing.JButton btnvolver;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblBuscarTitulo;
    private javax.swing.JLabel lblCedula;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblIconoCliente;
    private javax.swing.JLabel lblLogoMini;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTituloForm;
    private javax.swing.JLabel lblTituloHeader;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JTable tblclientes;
    private javax.swing.JTextField txtapellido;
    private javax.swing.JTextField txtbuscar;
    private javax.swing.JTextField txtcedula;
    private javax.swing.JTextField txtcorreo;
    private javax.swing.JTextField txtnombre;
    private javax.swing.JTextField txttelefono;
    // End of variables declaration//GEN-END:variables
}
