
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;


public class koneksi {
    private String url ="jdbc:mysql://localhost/p4_icaarpl1";
    private String username="root";
    private String password="";
    private Connection con;
    
    public void connect (){
        try {
            con =DriverManager.getConnection(url, username,password);
            System.out.println("koneksi berhasil");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"koneksi database gagal");
        }
    }
    public Connection getCon(){
        return con;
    }
}
