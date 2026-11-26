package vista;

import conexion.Conexion;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Menu principal del Sistema Optica.
 * Muestra los modulos disponibles segun el rol del usuario que inicio sesion.
 *
 * @author saram
 */
public class MenuPrincipal extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(MenuPrincipal.class.getName());

    private String rolActual = "";

    public MenuPrincipal() {
        initComponents();
        setLocationRelativeTo(null);
        aplicarDiseno();
    }

    public MenuPrincipal(String nombre, String rol) {
        this.rolActual = rol;
        initComponents();
        setLocationRelativeTo(null);
        aplicarDiseno();

        lblbienvenida.setText("Bienvenido(a), " + nombre);
        if (nombre != null && rol != null && nombre.toLowerCase().contains(rol.toLowerCase())) {
            lblSubBanner.setText("Selecciona un modulo para comenzar");
        } else {
            lblSubBanner.setText("Rol: " + rol + "  -  Selecciona un modulo para comenzar");
        }

        boolean esAdmin = "Administrador".equals(rol);
        btnventas.setVisible(esAdmin);
        btncompras.setVisible(esAdmin);

        if (esAdmin) {
            // Admin: 5 botones en dos filas, tamaño normal
            btncitas.setPreferredSize(new Dimension(190, 150));
            btnclientes.setPreferredSize(new Dimension(190, 150));
            btnproductos.setPreferredSize(new Dimension(190, 150));
            btnventas.setPreferredSize(new Dimension(190, 150));
            btncompras.setPreferredSize(new Dimension(190, 150));
            pnlModulos.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        } else {
            // Optometrista: 3 botones en una sola fila, mas grandes
            btncitas.setPreferredSize(new Dimension(200, 160));
            btnclientes.setPreferredSize(new Dimension(200, 160));
            btnproductos.setPreferredSize(new Dimension(200, 160));
            pnlModulos.setLayout(new FlowLayout(FlowLayout.CENTER, 24, 20));
        }
        pnlModulos.revalidate();

        cargarResumenDia();
    }

    private void aplicarDiseno() {
        Diseno.aplicarIconoVentana(this);
        Diseno.ponerImagen(lblLogoMini, "/imagenes/logo_circular.png", 36, 36);
        Diseno.ponerImagen(lblBannerImg, "/imagenes/banner_gafas.png", 200, 134);

        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Diseno.BORDE_ROSA));
        pnlBanner.setBorder(BorderFactory.createLineBorder(Diseno.BORDE_ROSA, 1, true));
        Diseno.estilizarBotonSecundario(btncerrar);

        estilizarTarjeta(btncitas,     "/imagenes/citas.jpg");
        estilizarTarjeta(btnclientes,  "/imagenes/clientes.png");
        estilizarTarjeta(btnproductos, "/imagenes/productos.jpg");
        estilizarTarjeta(btnventas,    "/imagenes/ventas.png");
        estilizarTarjeta(btncompras,   "/imagenes/compras.jpg");

        Dimension tam = new Dimension(190, 150);
        btncitas.setPreferredSize(tam);
        btnclientes.setPreferredSize(tam);
        btnproductos.setPreferredSize(tam);
        btnventas.setPreferredSize(tam);
        btncompras.setPreferredSize(tam);

        pnlModulos.setOpaque(false);
    }

    private void estilizarTarjeta(JButton btn, String rutaImagen) {
        // Iconos JPG tienen fondo blanco, se renderizan mas grandes para compensar
        boolean esJpg = rutaImagen.toLowerCase().endsWith(".jpg");
        int tamIcono = esJpg ? 100 : 80;

        btn.setUI(new BasicButtonUI());
        btn.setBackground(Color.WHITE);
        btn.setForeground(Diseno.VINO_TITULO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Diseno.BORDE_ROSA, 1, true),
                esJpg ? BorderFactory.createEmptyBorder(4, 8, 4, 8)
                       : BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setIconTextGap(6);

        javax.swing.JLabel temp = new javax.swing.JLabel();
        Diseno.ponerImagen(temp, rutaImagen, tamIcono, tamIcono);
        btn.setIcon(temp.getIcon());

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(Diseno.ROSA_SUAVE); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
    }

    private void cargarResumenDia() {
        try {
            Connection con = Conexion.getInstancia().getConexion();
            CallableStatement cs = con.prepareCall("{CALL sp_resumen_dia()}");
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                int citas     = rs.getInt("citas_hoy");
                double ventas = rs.getDouble("ventas_hoy");
                int bajo      = rs.getInt("bajo_stock");
                lblResumen.setText("  Citas hoy: " + citas + "   |   Ventas hoy: $" + String.format("%,.0f", ventas) + "   |   Stock bajo: " + bajo + " producto(s)");
            }
            rs.close(); cs.close();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "No se pudo cargar el resumen del dia", ex);
            lblResumen.setText("  Resumen del dia no disponible");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        lblLogoMini = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        btncerrar = new javax.swing.JButton();
        pnlBanner = new javax.swing.JPanel();
        lblbienvenida = new javax.swing.JLabel();
        lblSubBanner = new javax.swing.JLabel();
        lblResumen = new javax.swing.JLabel();
        lblBannerImg = new javax.swing.JLabel();
        pnlModulos = new javax.swing.JPanel();
        btncitas = new javax.swing.JButton();
        btnclientes = new javax.swing.JButton();
        btnproductos = new javax.swing.JButton();
        btnventas = new javax.swing.JButton();
        btncompras = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema Optica - Menu Principal");
        setBackground(Diseno.FONDO_APP);
        setMinimumSize(new java.awt.Dimension(760, 520));

        jPanel2.setBackground(Diseno.FONDO_APP);

        pnlHeader.setBackground(java.awt.Color.WHITE);
        pnlHeader.setPreferredSize(new java.awt.Dimension(760, 56));
        lblLogoMini.setText("");
        lblTitulo.setText("Sistema Optica");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblTitulo.setForeground(Diseno.VINO_TITULO);
        btncerrar.setText("Cerrar sesion");
        btncerrar.addActionListener(evt -> btncerrarActionPerformed(evt));

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(16).addComponent(lblLogoMini, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10).addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btncerrar).addGap(16))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lblLogoMini, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblTitulo).addComponent(btncerrar)
        );

        pnlBanner.setBackground(Diseno.FONDO_BANNER);
        pnlBanner.setPreferredSize(new java.awt.Dimension(700, 152));
        lblbienvenida.setText("Bienvenido(a)");
        lblbienvenida.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblbienvenida.setForeground(Diseno.VINO_TITULO);
        lblbienvenida.setMaximumSize(new java.awt.Dimension(380, 28));
        lblbienvenida.setPreferredSize(new java.awt.Dimension(380, 28));
        lblSubBanner.setText("Selecciona un modulo para comenzar");
        lblSubBanner.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        lblSubBanner.setForeground(Diseno.TEXTO_SUAVE);
        lblResumen.setText("  Citas hoy: -   |   Ventas hoy: -   |   Stock bajo: -");
        lblResumen.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        lblResumen.setForeground(Diseno.TEXTO_SUAVE);
        lblBannerImg.setText("");

        javax.swing.GroupLayout pnlBannerLayout = new javax.swing.GroupLayout(pnlBanner);
        pnlBanner.setLayout(pnlBannerLayout);
        pnlBannerLayout.setHorizontalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addGap(24)
                .addGroup(pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblbienvenida, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubBanner)
                    .addComponent(lblResumen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblBannerImg, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16))
        );
        pnlBannerLayout.setVerticalGroup(
            pnlBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(pnlBannerLayout.createSequentialGroup()
                .addGap(20).addComponent(lblbienvenida, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6).addComponent(lblSubBanner)
                .addGap(8).addComponent(lblResumen)
                .addGap(20))
            .addComponent(lblBannerImg, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pnlModulos.setBackground(Diseno.FONDO_APP);
        pnlModulos.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        pnlModulos.setPreferredSize(new java.awt.Dimension(700, 360));

        btncitas.setText("Citas");     btncitas.addActionListener(evt -> btncitasActionPerformed(evt));
        btnclientes.setText("Clientes"); btnclientes.addActionListener(evt -> btnclientesActionPerformed(evt));
        btnproductos.setText("Productos"); btnproductos.addActionListener(evt -> btnproductosActionPerformed(evt));
        btnventas.setText("Ventas");   btnventas.addActionListener(evt -> btnventasActionPerformed(evt));
        btncompras.setText("Compras"); btncompras.addActionListener(evt -> btncomprasActionPerformed(evt));

        pnlModulos.add(btncitas); pnlModulos.add(btnclientes); pnlModulos.add(btnproductos); pnlModulos.add(btnventas); pnlModulos.add(btncompras);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlBanner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlModulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22)
                .addComponent(pnlBanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(pnlModulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btncitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncitasActionPerformed
        new FrmCitas(this, rolActual).setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btncitasActionPerformed

    private void btnclientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclientesActionPerformed
        new FrmClientes(this).setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnclientesActionPerformed

    private void btnproductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnproductosActionPerformed
        new FrmProductos(this, rolActual).setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnproductosActionPerformed

    private void btnventasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnventasActionPerformed
        new FrmVentas(this).setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnventasActionPerformed

    private void btncomprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncomprasActionPerformed
        new FrmCompras(this).setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btncomprasActionPerformed

    private void btncerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncerrarActionPerformed
        new Login().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btncerrarActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { javax.swing.UIManager.setLookAndFeel(info.getClassName()); break; }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncerrar;
    private javax.swing.JButton btncitas;
    private javax.swing.JButton btnclientes;
    private javax.swing.JButton btncompras;
    private javax.swing.JButton btnproductos;
    private javax.swing.JButton btnventas;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBannerImg;
    private javax.swing.JLabel lblLogoMini;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblSubBanner;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblbienvenida;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlModulos;
    // End of variables declaration//GEN-END:variables
}
