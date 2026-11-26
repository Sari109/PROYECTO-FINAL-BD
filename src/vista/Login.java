package vista;

import conexion.Conexion;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Pantalla de inicio de sesion.
 * Verifica usuario y contrasena contra la tabla Usuario de la BD.
 *
 * @author saram
 */
public class Login extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(Login.class.getName());

    /** ID del usuario que inicia sesion; lo usan los demas formularios. */
    public static int idUsuario = 0;

    public Login() {
        initComponents();
        setLocationRelativeTo(null);
        aplicarDiseno();
    }

    private void aplicarDiseno() {
        Diseno.aplicarIconoVentana(this);
        // Panel izquierdo rosado
        pnlIzquierdo.setBackground(Diseno.FONDO_PASTEL);
        Diseno.ponerImagen(lblLogo, "/imagenes/logo_circular.png", 170, 170);
        lblNombreApp.setForeground(Diseno.VINO_TITULO);
        lblSlogan.setForeground(Diseno.TEXTO_SUAVE);
        // Panel derecho blanco
        pnlDerecho.setBackground(Color.WHITE);
        lblBienvenida.setForeground(Diseno.VINO_TITULO);
        lblInstruccion.setForeground(Diseno.TEXTO_SUAVE);
        Diseno.estilizarCampo(txtUsuario);
        Diseno.estilizarCampo(txtclave);
        Diseno.estilizarBotonPrimario(btnIngresar);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlIzquierdo = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblNombreApp = new javax.swing.JLabel();
        lblSlogan = new javax.swing.JLabel();
        pnlDerecho = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        lblInstruccion = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblClave = new javax.swing.JLabel();
        txtclave = new javax.swing.JPasswordField();
        btnIngresar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema Optica - Iniciar Sesion");
        setResizable(false);

        // Panel izquierdo
        pnlIzquierdo.setPreferredSize(new java.awt.Dimension(280, 400));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombreApp.setText("Sistema Optica");
        lblNombreApp.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblNombreApp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSlogan.setText("Cuidamos tu mirada con estilo");
        lblSlogan.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSlogan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnlIzquierdoLayout = new javax.swing.GroupLayout(pnlIzquierdo);
        pnlIzquierdo.setLayout(pnlIzquierdoLayout);
        pnlIzquierdoLayout.setHorizontalGroup(
            pnlIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(pnlIzquierdoLayout.createSequentialGroup()
                .addGap(20)
                .addGroup(pnlIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreApp)
                    .addComponent(lblSlogan))
                .addGap(20))
        );
        pnlIzquierdoLayout.setVerticalGroup(
            pnlIzquierdoLayout.createSequentialGroup()
                .addGap(60)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16)
                .addComponent(lblNombreApp)
                .addGap(8)
                .addComponent(lblSlogan)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // Panel derecho
        pnlDerecho.setPreferredSize(new java.awt.Dimension(320, 400));

        lblBienvenida.setText("¡Bienvenido(a)!");
        lblBienvenida.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        lblInstruccion.setText("Ingresa tu usuario y contraseña para continuar");
        lblInstruccion.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        lblUsuario.setText("Usuario");
        lblUsuario.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lblClave.setText("Contraseña");
        lblClave.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

        btnIngresar.setText("Ingresar");
        btnIngresar.addActionListener(evt -> btnIngresarActionPerformed(evt));

        javax.swing.GroupLayout pnlDerechoLayout = new javax.swing.GroupLayout(pnlDerecho);
        pnlDerecho.setLayout(pnlDerechoLayout);
        pnlDerechoLayout.setHorizontalGroup(
            pnlDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDerechoLayout.createSequentialGroup()
                .addGap(32)
                .addGroup(pnlDerechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBienvenida)
                    .addComponent(lblInstruccion)
                    .addComponent(lblUsuario)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClave)
                    .addComponent(txtclave, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32))
        );
        pnlDerechoLayout.setVerticalGroup(
            pnlDerechoLayout.createSequentialGroup()
                .addGap(60)
                .addComponent(lblBienvenida)
                .addGap(6)
                .addComponent(lblInstruccion)
                .addGap(28)
                .addComponent(lblUsuario)
                .addGap(5)
                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14)
                .addComponent(lblClave)
                .addGap(5)
                .addComponent(txtclave, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(btnIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // Layout general
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(pnlDerecho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlIzquierdo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlDerecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
        String usuario = txtUsuario.getText().trim();
        String clave   = new String(txtclave.getPassword()).trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa tu usuario y contrasena.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(
                "SELECT id_usuario, nombre, rol FROM Usuario WHERE usuario = ? AND contrasena = ?");
            ps.setString(1, usuario);
            ps.setString(2, clave);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idUsuario = rs.getInt("id_usuario");
                String nombre = rs.getString("nombre");
                String rol    = rs.getString("rol");
                rs.close(); ps.close();
                new MenuPrincipal(nombre, rol).setVisible(true);
                this.dispose();
            } else {
                rs.close(); ps.close();
                JOptionPane.showMessageDialog(this, "Usuario o contrasena incorrectos.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                txtclave.setText("");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error al iniciar sesion", ex);
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnIngresarActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIngresar;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblClave;
    private javax.swing.JLabel lblInstruccion;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblNombreApp;
    private javax.swing.JLabel lblSlogan;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlDerecho;
    private javax.swing.JPanel pnlIzquierdo;
    private javax.swing.JPasswordField txtclave;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
