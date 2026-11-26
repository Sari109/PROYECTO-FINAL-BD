package vista;

import conexion.Conexion;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Dialogo para registrar un cliente nuevo rapidamente desde el modulo de ventas.
 *
 * @author saram
 */
public class NuevoCliente extends JDialog {

    private static final Logger logger = Logger.getLogger(NuevoCliente.class.getName());

    private boolean guardado = false;

    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public NuevoCliente(Frame owner) {
        super(owner, "Registrar nuevo cliente", true);
        construirVentana();
        setLocationRelativeTo(owner);
        // Cerrar con Escape
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void construirVentana() {
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Diseno.FONDO_APP);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel icono = new JLabel();
        Diseno.ponerImagen(icono, "/imagenes/clientes.png", 38, 38);

        JLabel titulo = new JLabel("Nuevo Cliente");
        titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        titulo.setForeground(Diseno.VINO_TITULO);

        JPanel card = new JPanel(new GridBagLayout());
        Diseno.estilizarTarjeta(card);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Diseno.BORDE_ROSA, 1, true),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        txtCedula   = campo("Cedula");
        txtNombre   = campo("Nombre");
        txtApellido = campo("Apellido");
        txtTelefono = campo("Telefono");
        txtCorreo   = campo("Correo");

        Diseno.aplicarSoloNumeros(txtCedula, 15);
        Diseno.aplicarSoloLetras(txtNombre, 50);
        Diseno.aplicarSoloLetras(txtApellido, 50);
        Diseno.aplicarSoloNumeros(txtTelefono, 15);
        Diseno.aplicarLimite(txtCorreo, 100);

        btnCancelar = new JButton("Cancelar");
        btnGuardar  = new JButton("Guardar");
        Diseno.estilizarBotonSecundario(btnCancelar);
        Diseno.estilizarBotonPrimario(btnGuardar);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardar());

        // Armar el card
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        int fila  = 0;
        for (String[] par : new String[][]{
                {"Cedula:", null}, {null, null},
                {"Nombre:", null}, {null, null},
                {"Apellido:", null}, {null, null},
                {"Telefono:", null}, {null, null},
                {"Correo:", null}}) {
            // No usamos esto, mejor los agrego directo
        }
        agregarFila(card, "Cedula:",    txtCedula,   0);
        agregarFila(card, "Nombre:",    txtNombre,   1);
        agregarFila(card, "Apellido:",  txtApellido, 2);
        agregarFila(card, "Telefono:",  txtTelefono, 3);
        agregarFila(card, "Correo:",    txtCorreo,   4);

        gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        gc.insets = new Insets(14, 4, 4, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        card.add(botones, gc);

        // Armar root
        GridBagConstraints gr = new GridBagConstraints();
        gr.gridx = 0; gr.gridy = 0; gr.gridwidth = 2; gr.anchor = GridBagConstraints.CENTER;
        gr.insets = new Insets(0, 0, 4, 0);
        root.add(icono, gr);
        gr.gridy = 1; gr.insets = new Insets(0, 0, 14, 0);
        root.add(titulo, gr);
        gr.gridy = 2; gr.fill = GridBagConstraints.BOTH; gr.insets = new Insets(0, 0, 0, 0);
        root.add(card, gr);

        setContentPane(root);
        pack();
    }

    private void agregarFila(JPanel panel, String etiqueta, JTextField campo, int fila) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 8);
        gc.anchor = GridBagConstraints.WEST;

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lbl.setForeground(Diseno.TEXTO_OSCURO);
        gc.gridx = 0; gc.gridy = fila; gc.fill = GridBagConstraints.NONE;
        panel.add(lbl, gc);

        Diseno.estilizarCampo(campo);
        campo.setPreferredSize(new java.awt.Dimension(200, 32));
        gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gc);
    }

    private JTextField campo(String placeholder) {
        JTextField tf = new JTextField(18);
        tf.setToolTipText(placeholder);
        return tf;
    }

    private void guardar() {
        String cedula   = txtCedula.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo   = txtCorreo.getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cedula, nombre y apellido son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (guardarEnBD(this, cedula, nombre, apellido, telefono, correo)) {
            guardado = true;
            dispose();
        }
    }

    /** Llama a sp_guardar_cliente y muestra el resultado. Devuelve true si tuvo exito. */
    public static boolean guardarEnBD(java.awt.Component padre,
                                      String cedula, String nombre, String apellido,
                                      String telefono, String correo) {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_guardar_cliente(?,?,?,?,?)}");
            cs.setString(1, nombre);
            cs.setString(2, apellido);
            cs.setString(3, cedula);
            cs.setString(4, telefono);
            cs.setString(5, correo);
            cs.execute();
            cs.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(NuevoCliente.class.getName()).log(Level.SEVERE, "Error guardando cliente", ex);
            JOptionPane.showMessageDialog(padre, "Error al guardar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /** Devuelve true si el usuario confirmo y se guardo correctamente. */
    public boolean fueGuardado() {
        return guardado;
    }

    /** Devuelve el id del cliente recien registrado buscando por cedula. */
    public int getIdClienteRegistrado() {
        String cedula = txtCedula.getText().trim();
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT id_cliente FROM Cliente WHERE cedula = ?");
            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                rs.close(); ps.close();
                return id;
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error buscando cliente nuevo", ex);
        }
        return -1;
    }
}
