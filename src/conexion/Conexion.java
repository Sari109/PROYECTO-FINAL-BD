package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author saram
 */
public class Conexion {
    
        // Datos de la base de datos (XAMPP trae root sin contraseña por defecto)
    private static final String URL = "jdbc:mariadb://localhost:3306/optica";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    // Aquí se guarda la única instancia de la clase (Singleton)
    private static Conexion instancia;
    private Connection conexion;

    // Constructor privado: nadie puede crear una Conexion con "new" desde afuera
    private Conexion() {
        conectar();
    }

    // Si ya existe la instancia la devuelvo, y si no, la creo
    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    // Devuelve la conexión; si se cerró o nunca se abrió, intenta conectar otra vez
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException e) {
            conexion = null;
        }
        return conexion;
    }

    // Abre la conexión y avisa con un mensaje si el servidor no está encendido
    private void conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (SQLException e) {
            conexion = null;
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con la base de datos.\n"
                    + "Revisa que MySQL esté encendido en XAMPP.",
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
