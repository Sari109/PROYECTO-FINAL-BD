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
 * Formulario para gestionar las citas de la optica.
 * Administrador ve todas; Optometrista solo ve las suyas.
 *
 * @author saram
 */
public class FrmCitas extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FrmCitas.class.getName());

    private final JFrame ventanaPadre;
    private final String rolActual;
    private DefaultTableModel modeloTabla;

    private JPanel pnlHeader;
    private JLabel lblLogoMini;
    private JLabel lblTitulo;
    private JButton btnvolver;

    private JPanel pnlFormulario;
    private JComboBox<String> cmbcliente;
    private JComboBox<String> cmboptometrista;
    private JTextField txtfecha;
    private JTextField txthora;
    private JComboBox<String> cmbfiltro;
    private JButton btnAgendar;
    private JButton btnAtendida;
    private JButton btnCancelada;
    private JButton btnlimpiar;

    private JPanel pnlTabla;
    private JTextField txtbuscar;
    private JTable tblcitas;
    private JScrollPane scroll;

    private int[] idsClientes;
    private int[] idsUsuarios;
    private int idCitaSeleccionada = -1;

    public FrmCitas(JFrame padre, String rol) {
        this.ventanaPadre = padre;
        this.rolActual = rol;
        initComponents();
        setLocationRelativeTo(null);
        Diseno.aplicarIconoVentana(this);
        aplicarDiseno();
        cargarClientes();
        cargarOptometristas();
        cargarCitas(null);
    }

    private void initComponents() {
        setTitle("Sistema Optica - Citas");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(940, 640));
        getContentPane().setBackground(Diseno.FONDO_APP);
        getContentPane().setLayout(new BorderLayout());

        pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(900, 56));
        lblLogoMini = new JLabel();
        Diseno.ponerImagen(lblLogoMini, "/imagenes/logo_circular.png", 36, 36);
        lblTitulo = new JLabel("Gestion de Citas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Diseno.VINO_TITULO);
        btnvolver = new JButton("Volver");
        btnvolver.addActionListener(e -> volverMenu());
        pnlHeader.add(lblLogoMini);
        pnlHeader.add(lblTitulo);
        pnlHeader.add(Box.createHorizontalStrut(400));
        pnlHeader.add(btnvolver);
        getContentPane().add(pnlHeader, BorderLayout.NORTH);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centro.setBackground(Diseno.FONDO_APP);

        pnlFormulario = new JPanel();
        pnlFormulario.setLayout(new BoxLayout(pnlFormulario, BoxLayout.Y_AXIS));
        pnlFormulario.setPreferredSize(new Dimension(300, 500));
        pnlFormulario.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel lblTituloForm = new JLabel("Agendar Cita");
        lblTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloForm.setForeground(Diseno.VINO_TITULO);
        lblTituloForm.setAlignmentX(LEFT_ALIGNMENT);

        cmbcliente = new JComboBox<>(); cmbcliente.setMaximumSize(new Dimension(260, 32)); cmbcliente.setAlignmentX(LEFT_ALIGNMENT);
        cmboptometrista = new JComboBox<>(); cmboptometrista.setMaximumSize(new Dimension(260, 32)); cmboptometrista.setAlignmentX(LEFT_ALIGNMENT);
        cmboptometrista.setEnabled("Administrador".equals(rolActual));
        txtfecha = new JTextField(); txtfecha.setMaximumSize(new Dimension(260, 32)); txtfecha.setAlignmentX(LEFT_ALIGNMENT);
        txthora  = new JTextField(); txthora.setMaximumSize(new Dimension(260, 32));  txthora.setAlignmentX(LEFT_ALIGNMENT);
        cmbfiltro = new JComboBox<>(new String[]{"Todos","Programada","Atendida","Cancelada"});
        cmbfiltro.setMaximumSize(new Dimension(260, 32)); cmbfiltro.setAlignmentX(LEFT_ALIGNMENT);
        cmbfiltro.addActionListener(e -> { String s = (String) cmbfiltro.getSelectedItem(); cargarCitas("Todos".equals(s) ? null : s); });

        btnAgendar   = new JButton("Agendar cita");   btnAgendar.setMaximumSize(new Dimension(260,36));   btnAgendar.setAlignmentX(LEFT_ALIGNMENT);
        btnAtendida  = new JButton("Marcar Atendida"); btnAtendida.setMaximumSize(new Dimension(260,36));  btnAtendida.setAlignmentX(LEFT_ALIGNMENT);
        btnCancelada = new JButton("Marcar Cancelada");btnCancelada.setMaximumSize(new Dimension(260,36)); btnCancelada.setAlignmentX(LEFT_ALIGNMENT);
        btnlimpiar   = new JButton("Limpiar");         btnlimpiar.setMaximumSize(new Dimension(260,36));   btnlimpiar.setAlignmentX(LEFT_ALIGNMENT);

        btnAgendar.addActionListener(e -> agendarCita());
        btnAtendida.addActionListener(e -> cambiarEstado("Atendida"));
        btnCancelada.addActionListener(e -> cambiarEstado("Cancelada"));
        btnlimpiar.addActionListener(e -> limpiarFormulario());

        pnlFormulario.add(lblTituloForm);                   pnlFormulario.add(Box.createVerticalStrut(10));
        pnlFormulario.add(etiqueta("Cliente:"));            pnlFormulario.add(cmbcliente);       pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Optometrista:"));       pnlFormulario.add(cmboptometrista);  pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Fecha (aaaa-mm-dd):")); pnlFormulario.add(txtfecha);         pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(etiqueta("Hora (hh:mm):"));       pnlFormulario.add(txthora);          pnlFormulario.add(Box.createVerticalStrut(10));
        pnlFormulario.add(etiqueta("Filtrar por estado:")); pnlFormulario.add(cmbfiltro);        pnlFormulario.add(Box.createVerticalStrut(14));
        pnlFormulario.add(btnAgendar);  pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(btnAtendida); pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(btnCancelada);pnlFormulario.add(Box.createVerticalStrut(6));
        pnlFormulario.add(btnlimpiar);

        pnlTabla = new JPanel(new BorderLayout(0, 8));
        pnlTabla.setPreferredSize(new Dimension(540, 500));
        txtbuscar = new JTextField(); txtbuscar.setToolTipText("Buscar...");
        modeloTabla = new DefaultTableModel(new String[]{"#","Fecha","Hora","Cliente","Optometrista","Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblcitas = new JTable(modeloTabla);
        scroll = new JScrollPane(tblcitas); scroll.setPreferredSize(new Dimension(540, 430));
        tblcitas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblcitas.getSelectedRow();
            if (fila >= 0) idCitaSeleccionada = (int) modeloTabla.getValueAt(fila, 0);
        });
        JPanel barraBuscar = new JPanel(new BorderLayout(6,0)); barraBuscar.setOpaque(false);
        barraBuscar.add(new JLabel("Buscar: "), BorderLayout.WEST); barraBuscar.add(txtbuscar, BorderLayout.CENTER);
        pnlTabla.add(barraBuscar, BorderLayout.NORTH);
        pnlTabla.add(scroll, BorderLayout.CENTER);

        centro.add(pnlFormulario);
        centro.add(pnlTabla);
        getContentPane().add(centro, BorderLayout.CENTER);
        pack();
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Diseno.TEXTO_OSCURO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private void aplicarDiseno() {
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Diseno.BORDE_ROSA));
        Diseno.estilizarTarjeta(pnlFormulario); Diseno.estilizarTarjeta(pnlTabla);
        Diseno.estilizarCampo(txtfecha); Diseno.estilizarCampo(txthora); Diseno.estilizarCampo(txtbuscar);
        Diseno.estilizarCombo(cmbcliente); Diseno.estilizarCombo(cmboptometrista); Diseno.estilizarCombo(cmbfiltro);
        Diseno.estilizarBotonPrimario(btnAgendar);
        Diseno.estilizarBotonSecundario(btnAtendida); Diseno.estilizarBotonSecundario(btnCancelada);
        Diseno.estilizarBotonSecundario(btnlimpiar); Diseno.estilizarBotonSecundario(btnvolver);
        Diseno.estilizarTabla(tblcitas, scroll);
    }

    private void cargarClientes() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_cliente, CONCAT(nombre,' ',apellido) FROM Cliente ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            java.util.List<String> n = new java.util.ArrayList<>(); java.util.List<Integer> ids = new java.util.ArrayList<>();
            while (rs.next()) { ids.add(rs.getInt(1)); n.add(rs.getString(2)); }
            rs.close(); ps.close();
            idsClientes = ids.stream().mapToInt(Integer::intValue).toArray();
            cmbcliente.setModel(new DefaultComboBoxModel<>(n.toArray(new String[0])));
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando clientes", ex); }
    }

    private void cargarOptometristas() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement("SELECT id_usuario, nombre FROM Usuario ORDER BY nombre");
            ResultSet rs = ps.executeQuery();
            java.util.List<String> n = new java.util.ArrayList<>(); java.util.List<Integer> ids = new java.util.ArrayList<>();
            while (rs.next()) { ids.add(rs.getInt(1)); n.add(rs.getString(2)); }
            rs.close(); ps.close();
            idsUsuarios = ids.stream().mapToInt(Integer::intValue).toArray();
            cmboptometrista.setModel(new DefaultComboBoxModel<>(n.toArray(new String[0])));
            if (!"Administrador".equals(rolActual)) {
                for (int i = 0; i < idsUsuarios.length; i++) {
                    if (idsUsuarios[i] == Login.idUsuario) { cmboptometrista.setSelectedIndex(i); break; }
                }
            }
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando usuarios", ex); }
    }

    private void cargarCitas(String estado) {
        modeloTabla.setRowCount(0);
        try {
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_listar_citas(?)}");
            cs.setString(1, estado);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_cita"), rs.getString("fecha"), rs.getString("hora"),
                    rs.getString("cliente"), rs.getString("optometrista"), rs.getString("estado")
                });
            }
            rs.close(); cs.close();
        } catch (SQLException ex) { logger.log(Level.WARNING, "Error cargando citas", ex); }
    }

    private void agendarCita() {
        if (idsClientes == null || idsClientes.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay clientes registrados.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        String fecha = txtfecha.getText().trim().replace("/", "-");
        String hora  = txthora.getText().trim();
        if (fecha.isEmpty() || hora.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa la fecha y la hora.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        // Permitir hora con un solo digito, ej: 9:00 → 09:00
        if (hora.matches("\\d:\\d{2}")) hora = "0" + hora;
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}") || !hora.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Fecha: aaaa-mm-dd (ej. 2026-11-27)  |  Hora: hh:mm (ej. 09:00)", "Formato incorrecto", JOptionPane.ERROR_MESSAGE); return;
        }
        try {
            int idCliente = idsClientes[cmbcliente.getSelectedIndex()];
            int idUsuario = idsUsuarios[cmboptometrista.getSelectedIndex()];
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_guardar_cita(?,?,?,?)}");
            cs.setInt(1, idCliente); cs.setInt(2, idUsuario); cs.setString(3, fecha); cs.setString(4, hora + ":00");
            ResultSet rs = cs.executeQuery();
            String msg = ""; if (rs.next()) msg = rs.getString("mensaje");
            rs.close(); cs.close();
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            cargarCitas(null); limpiarFormulario();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error agendando cita", ex);
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstado(String nuevoEstado) {
        if (idCitaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_cambiar_estado_cita(?,?)}");
            cs.setInt(1, idCitaSeleccionada); cs.setString(2, nuevoEstado);
            cs.executeQuery(); cs.close();
            JOptionPane.showMessageDialog(this, "Cita marcada como: " + nuevoEstado, "Listo", JOptionPane.INFORMATION_MESSAGE);
            idCitaSeleccionada = -1; cargarCitas(null);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error cambiando estado", ex);
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtfecha.setText(""); txthora.setText(""); idCitaSeleccionada = -1; tblcitas.clearSelection();
    }

    private void volverMenu() {
        if (ventanaPadre != null) ventanaPadre.setVisible(true);
        this.dispose();
    }
}
