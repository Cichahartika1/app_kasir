

import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;


public class penjualan extends javax.swing.JFrame {
    private DefaultTableModel model=null;
    private PreparedStatement stat;
    private ResultSet rs;
    koneksi k = new koneksi();
    
        public penjualan() {
        initComponents();
        k.connect();
        tabelkosongdetail();
        tabelpenjualan();
        tabelkosongpelanggan();
        tabelkosongproduk();
        utama();
        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        txttglpenjualan.setText(s.format(date));
        txttotalharga.setText("0");
        txtnamapelanggan.requestFocus();
    }

    public void tabelkosongdetail() {
        //Create Table
        model = new DefaultTableModel();
        tabeldetailpenjualan.setModel(model);
        model.addColumn("Penjualan ID");
        model.addColumn("Produk ID");
        model.addColumn("Jumlah");
        model.addColumn("Harga");
        model.addColumn("Sub Total");
    }

    public void tabelpenjualan() {
        try {
            this.stat = k.getCon().prepareStatement("select * from penjualan");
            this.rs = this.stat.executeQuery();
            tabelpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }

    public void tabelkosongproduk() {
        //Create Table
        model = new DefaultTableModel();
        tabelproduk.setModel(model);
        model.addColumn("Produk ID");
        model.addColumn("Nama Produk");
        model.addColumn("Harga");
        model.addColumn("Stok");
    }

    public void tabelkosongpelanggan() {
        //Create Table
        model = new DefaultTableModel();
        tabelpelanggan.setModel(model);
        model.addColumn("Pelanggan ID");
        model.addColumn("Nama PLG");
        model.addColumn("Alamat");
        model.addColumn("No. TLP");
    }

    public void utama() {
        txtpelangganid.setText("");
        txtprodukid.setText("");
        txtnamaproduk.setText("");
        txtharga.setText("");
        txtjumlahproduk.setText("");
        autonumber();
    }

    public void clear() {
        txtpelangganid.setText("");
        txtnamapelanggan.setText("");
        txttotalharga.setText("0");
        txTampil.setText("0");
    }

    class FPenjualan {

        int PenjualanID, PelangganID, TtlHarga;
        String TanggalPenjualan;

        public FPenjualan() {
            this.PenjualanID = 0;
            this.TanggalPenjualan = txttglpenjualan.getText();
            this.TtlHarga = Integer.parseInt(txttotalharga.getText());
            this.PelangganID = 0;
        }
    }

    public void refreshTable() {
        try {
            this.stat = k.getCon().prepareStatement("select * from detailpenjualan");
            this.rs = this.stat.executeQuery();
            tabeldetailpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("Koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }

    public void refresTabelPelanggan() {
        try {
            this.stat = k.getCon().prepareStatement("select * from Pelanggan where PelangganID like '%" + txtnamapelanggan.getText() + "%' OR NamaPelanggan like '%" + txtnamapelanggan.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelpelanggan.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
    }

    private void autonumber() {
        try {
            Connection c = k.getCon();
            Statement s = c.createStatement();
            String sql = "SELECT * FROM penjualan ORDER BY PenjualanID DESC";
            ResultSet r = s.executeQuery(sql);
            if (r.next()) {
                String NoFaktur = r.getString("PenjualanID").substring(2);
                String TR = "" + (Integer.parseInt(NoFaktur) + 1);
                String Nol = "";

                if (TR.length() == 1) {
                    Nol = "000";
                } else if (TR.length() == 2) {
                    Nol = "00";
                } else if (TR.length() == 3) {
                    Nol = "0";
                } else if (TR.length() == 4) {
                    Nol = "";
                }
                txtpenjualanid.setText("TR" + Nol + TR);
            } else {
                txtpenjualanid.setText("TR0001");
            }
            r.close();
            s.close();
        } catch (Exception e) {
            System.out.println("autonumber error");
        }
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    public void inputpenjualan() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        String penjID = txtpenjualanid.getText();
        String tglpenj = txttglpenjualan.getText();
        String idPLG = txtpelangganid.getText();
        String total = txttotalharga.getText();

        try {
            Connection c = k.getCon();
            String sql = "INSERT INTO penjualan VALUES (?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, penjID);
            p.setString(2, tglpenj);
            p.setString(3, total);
            p.setString(4, idPLG);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("simpan penjualan error");
        }

        try {
            Connection c = k.getCon();
            int baris = tabeldetailpenjualan.getRowCount();

            for (int i = 0; i < baris; i++) {
                String sql = "INSERT INTO detailpenjualan (PenjualanID, ProdukID, JumlahProduk, Harga, Subtotal) VALUES ('" + tabeldetailpenjualan.getValueAt(i, 0) + "','" + tabeldetailpenjualan.getValueAt(i, 1) + "','" + tabeldetailpenjualan.getValueAt(i, 2) + "','" + tabeldetailpenjualan.getValueAt(i, 3) + "','" + tabeldetailpenjualan.getValueAt(i, 4) + "')";
                PreparedStatement p = c.prepareStatement(sql);
                p.executeUpdate();
                p.close();
            }
        } catch (Exception e) {
            System.out.println("simpan detail penjualan error");
        }
        clear();
        utama();
        autonumber();
        kosong();
        txTampil.setText("Rp. 0");
    }

    public void refreshTabelProduk() {
        try {
            this.stat = k.getCon().prepareStatement("select * from produk where NamaProduk like '%" + txtnamaproduk.getText() + "%' OR ProdukID like '%" + txtnamaproduk.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelproduk.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
    }

    public void totalHarga() {
        int jumlahBaris = tabeldetailpenjualan.getRowCount();
        int totalHarga = 0;
        int jumlahProduk, hargaProduk;
        for (int i = 0; i < jumlahBaris; i++) {
            jumlahProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 2).toString());
            hargaProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 3).toString());
            totalHarga = totalHarga + (jumlahProduk * hargaProduk);
        }
        txttotalharga.setText(String.valueOf(totalHarga));
        txTampil.setText("Rp " + totalHarga + ",00");
    }

    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        model.addRow(new Object[]{
            txtpenjualanid.getText(),
            txtprodukid.getText(),
            txtjumlahproduk.getText(),
            txtharga.getText(),
            txtsubtotal.getText()
        });
    }

    public void tambahDetailPenjualan() {
        int jumlah, harga, total;

        jumlah = Integer.valueOf(txtjumlahproduk.getText());
        harga = Integer.valueOf(txtharga.getText());
        total = jumlah * harga;

        txtsubtotal.setText(String.valueOf(total));

        loadData();
        totalHarga();
        clear2();
        txtnamaproduk.requestFocus();
    }

    public void clear2() {
        txtprodukid.setText("");
        txtnamaproduk.setText("");
        txtharga.setText("");
        txtjumlahproduk.setText("");
    }
        

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtpenjualanid = new javax.swing.JTextField();
        txtnamapelanggan = new javax.swing.JTextField();
        txttglpenjualan = new javax.swing.JTextField();
        txtpelangganid = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        txtnamaproduk = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtharga = new javax.swing.JTextField();
        txtjumlahproduk = new javax.swing.JTextField();
        txtprodukid = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtsubtotal = new javax.swing.JTextField();
        txttotalharga = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelpenjualan = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabeldetailpenjualan = new javax.swing.JTable();
        txTampil = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelpelanggan = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelproduk = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Nama Produk");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 0, 0);
        getContentPane().add(jLabel5, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("TanggalPenjualan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 39;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        getContentPane().add(jLabel4, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("NamaPelanggan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 0, 0);
        getContentPane().add(jLabel3, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("PenjualanID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 21;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("PENJUALAN");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 37;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(40, 30, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        txtpenjualanid.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 14, 0, 0);
        getContentPane().add(txtpenjualanid, gridBagConstraints);

        txtnamapelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnamapelangganActionPerformed(evt);
            }
        });
        txtnamapelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnamapelangganKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 14, 0, 0);
        getContentPane().add(txtnamapelanggan, gridBagConstraints);

        txttglpenjualan.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 97;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        getContentPane().add(txttglpenjualan, gridBagConstraints);

        txtpelangganid.setBackground(new java.awt.Color(204, 204, 255));
        txtpelangganid.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 39;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 94;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        getContentPane().add(txtpelangganid, gridBagConstraints);

        jButton1.setText("Tambah Pelanggan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 97;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        getContentPane().add(jButton1, gridBagConstraints);

        txtnamaproduk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnamaprodukKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 14, 0, 0);
        getContentPane().add(txtnamaproduk, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Harga");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 20, 0, 0);
        getContentPane().add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Jumlah");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 0);
        getContentPane().add(jLabel7, gridBagConstraints);

        txtharga.setBackground(new java.awt.Color(0, 153, 204));
        txtharga.setEnabled(false);
        txtharga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txthargaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 14, 0, 0);
        getContentPane().add(txtharga, gridBagConstraints);

        txtjumlahproduk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtjumlahprodukKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 14, 0, 0);
        getContentPane().add(txtjumlahproduk, gridBagConstraints);

        txtprodukid.setBackground(new java.awt.Color(204, 204, 255));
        txtprodukid.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 39;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 94;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        getContentPane().add(txtprodukid, gridBagConstraints);

        jLabel8.setText("Sub Total");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        getContentPane().add(jLabel8, gridBagConstraints);

        jLabel9.setText("Total Harga");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 38;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(jLabel9, gridBagConstraints);

        txtsubtotal.setBackground(new java.awt.Color(255, 153, 153));
        txtsubtotal.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 97;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 0, 0);
        getContentPane().add(txtsubtotal, gridBagConstraints);

        txttotalharga.setBackground(new java.awt.Color(255, 153, 153));
        txttotalharga.setEnabled(false);
        txttotalharga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttotalhargaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 97;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getContentPane().add(txttotalharga, gridBagConstraints);

        jButton2.setText("Tambah Produk");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 97;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 33;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        getContentPane().add(jButton2, gridBagConstraints);

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton3.setText("SIMPAN DETAIL PENJUALAN");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        getContentPane().add(jButton3, gridBagConstraints);

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton4.setText("HAPUS DETAIL PENJUALAN");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 59;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        getContentPane().add(jButton4, gridBagConstraints);

        tabelpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelpenjualan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 138;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 347;
        gridBagConstraints.ipady = 153;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 42, 28);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jButton5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton5.setText("INPUT PENJUALAN");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.ipadx = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 40, 0, 0);
        getContentPane().add(jButton5, gridBagConstraints);

        jButton6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton6.setText("CETAK NOTA");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 40, 0, 0);
        getContentPane().add(jButton6, gridBagConstraints);

        tabeldetailpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tabeldetailpenjualan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 138;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 487;
        gridBagConstraints.ipady = 83;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 20, 0, 0);
        getContentPane().add(jScrollPane2, gridBagConstraints);

        txTampil.setBackground(new java.awt.Color(255, 153, 153));
        txTampil.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txTampil.setText("Rp. 0");
        txTampil.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 97;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 41;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 124;
        gridBagConstraints.ipady = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 30, 42, 0);
        getContentPane().add(txTampil, gridBagConstraints);

        tabelpelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelpelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelpelangganMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabelpelanggan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 138;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 347;
        gridBagConstraints.ipady = 53;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 28);
        getContentPane().add(jScrollPane3, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Tabel Pelanggan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 138;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 0);
        getContentPane().add(jLabel10, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Tabel Produk");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 138;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        getContentPane().add(jLabel11, gridBagConstraints);

        tabelproduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelproduk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelprodukMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabelproduk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 138;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 347;
        gridBagConstraints.ipady = 53;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 28);
        getContentPane().add(jScrollPane4, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Tabel Penjualan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 138;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 0, 0);
        getContentPane().add(jLabel12, gridBagConstraints);

        setSize(new java.awt.Dimension(954, 591));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtnamapelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnamapelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnamapelangganActionPerformed

    private void txthargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txthargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txthargaActionPerformed

    private void txttotalhargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttotalhargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttotalhargaActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        inputpenjualan();
        tabelpenjualan();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void tabelprodukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelprodukMouseClicked
        // kode tabel produk di klik
        int r = tabelproduk.getSelectedRow();
        String ProdukID = tabelproduk.getValueAt(r,0).toString();
        String NamaPrd = tabelproduk.getValueAt(r,1).toString();
        String Hrg = tabelproduk.getValueAt(r,2).toString();
        String Ttl = tabelproduk.getValueAt(r,3).toString();
        txtprodukid.setText(ProdukID);
        txtnamaproduk.setText(NamaPrd);
        txtharga.setText(Hrg);
        txtjumlahproduk.requestFocus();
        
    }//GEN-LAST:event_tabelprodukMouseClicked

    private void txtnamapelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnamapelangganKeyReleased
        refresTabelPelanggan();
        txtpelangganid.setText("");
    }//GEN-LAST:event_txtnamapelangganKeyReleased

    private void tabelpelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelpelangganMouseClicked
        // kode tabel pelanggan di klik
        int r = tabelpelanggan.getSelectedRow();
        String PlgId = tabelpelanggan.getValueAt(r, 0).toString();
        String NamaPlg = tabelpelanggan.getValueAt(r, 1).toString();
        String Alamat = tabelpelanggan.getValueAt(r, 2).toString();
        String Tlp = tabelpelanggan.getValueAt(r, 3).toString();
        txtpelangganid.setText(PlgId);
        txtnamapelanggan.setText(NamaPlg);
        txtnamaproduk.requestFocus();
    }//GEN-LAST:event_tabelpelangganMouseClicked

    private void txtnamaprodukKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnamaprodukKeyReleased
        refreshTabelProduk();;
        txtprodukid.setText("");
    }//GEN-LAST:event_txtnamaprodukKeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        tambahDetailPenjualan();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        int row = tabeldetailpenjualan.getSelectedRow();
        model.removeRow(row);
        totalHarga();
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // buka form pelanggan
        pelanggan a = new pelanggan();
        a.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        prodk a = new prodk();
        a.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtjumlahprodukKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtjumlahprodukKeyTyped
        // validasi hanya input angka
        char karakter = evt.getKeyChar();
        if (!(((karakter >= '0') && (karakter <= '9') || (karakter == KeyEvent.VK_BACK_SPACE) || (karakter == KeyEvent.VK_DELETE)))) {
            JOptionPane.showMessageDialog(null, "Hanya boleh input angka");
            evt.consume();
        }
       
    }//GEN-LAST:event_txtjumlahprodukKeyTyped

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // KODING CETAK NOTA
        try {
            File namafile = new File("src/report1.jasper");
            JasperPrint jp = JasperFillManager.fillReport(namafile.getPath(), null, k.getCon());
            JasperViewer.viewReport(jp,false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "GAGAL");
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new penjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tabeldetailpenjualan;
    private javax.swing.JTable tabelpelanggan;
    private javax.swing.JTable tabelpenjualan;
    private javax.swing.JTable tabelproduk;
    private javax.swing.JTextField txTampil;
    public static javax.swing.JTextField txtharga;
    public static javax.swing.JTextField txtjumlahproduk;
    public static javax.swing.JTextField txtnamapelanggan;
    public static javax.swing.JTextField txtnamaproduk;
    public static javax.swing.JTextField txtpelangganid;
    public static javax.swing.JTextField txtpenjualanid;
    public static javax.swing.JTextField txtprodukid;
    private javax.swing.JTextField txtsubtotal;
    private javax.swing.JTextField txttglpenjualan;
    private javax.swing.JTextField txttotalharga;
    // End of variables declaration//GEN-END:variables
}
