package game_list;

import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormGame extends JFrame {
    private String[] judul = { "id", "judul", "tahun_rilis", "genre" };
    DefaultTableModel df;
    JTable tab = new JTable();
    JScrollPane scp = new JScrollPane();
    JPanel pnl = new JPanel();
    JLabel lblIdGame = new JLabel("id");
    JTextField txIdGame = new JTextField(10);
    JLabel lblJudulGame = new JLabel("judul");
    JTextField txJudulGame = new JTextField(20);
    JLabel lblTahunRilis = new JLabel("tahun_rilis");
    JTextField txTahunRilis = new JTextField(10);
    JLabel lblGenreGame = new JLabel("genre");
    JTextField txGenreGame = new JTextField(10);
    JButton btAdd = new JButton("Simpan");
    JButton btNew = new JButton("Baru");
    JButton btDel = new JButton("Hapus");
    JButton btEdit = new JButton("Ubah");
    Connection cn; // Added Connection object

    FormGame() {
        super("game");
        setSize(460, 300);
        pnl.setLayout(null);

        pnl.add(lblIdGame);
        lblIdGame.setBounds(20, 10, 100, 20);
        pnl.add(txIdGame);
        txIdGame.setBounds(125, 10, 100, 20);

        pnl.add(lblJudulGame);
        lblJudulGame.setBounds(20, 33, 100, 20);
        pnl.add(txJudulGame);
        txJudulGame.setBounds(125, 33, 175, 20);

        pnl.add(lblTahunRilis);
        lblTahunRilis.setBounds(20, 56, 100, 20);
        pnl.add(txTahunRilis);
        txTahunRilis.setBounds(125, 56, 175, 20);

        pnl.add(lblGenreGame);
        lblGenreGame.setBounds(20, 79, 100, 20);
        pnl.add(txGenreGame);
        txGenreGame.setBounds(125, 79, 175, 20);

        pnl.add(btNew);
        btNew.setBounds(320, 10, 100, 20);
        btNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btNewAksi(e);
            }
        });

        pnl.add(btAdd);
        btAdd.setBounds(320, 33, 100, 20);
        btAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btAddAksi(e);
            }
        });

        pnl.add(btEdit);
        btEdit.setBounds(320, 56, 100, 20);
        btEdit.setEnabled(false);
        btEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btEditAksi(e);
            }
        });

        pnl.add(btDel);
        btDel.setBounds(320, 79, 100, 20);
        btDel.setEnabled(false);
        btDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btDelAksi(e);
            }
        });

        df = new DefaultTableModel(null, judul);
        tab.setModel(df);
        scp.getViewport().add(tab);
        tab.setEnabled(true);
        tab.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });

        scp.setBounds(20, 110, 405, 130);
        pnl.add(scp);
        getContentPane().add(pnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Connect to the database
        cn = new Connect_DB().getConnect();
        if (cn == null) {
            System.err.println("Failed to connect to the database");
            System.exit(1);
        }

        // Load data from the database
        loadData();
    }

    void loadData() {
        try (Statement st = cn.createStatement()) {
            String sql = "SELECT * FROM game";
            try (ResultSet rs = st.executeQuery(sql)) {
                clearTable();
                while (rs.next()) {
                    String IdGame = rs.getString("Id_Game");
                    String JudulGame = rs.getString("Judul_Game");
                    String TahunRilis = rs.getString("Tahun_Rilis");
                    String GenreGame = rs.getString("Genre_Game");
                    String[] data = { IdGame, JudulGame, TahunRilis, GenreGame};
                    df.addRow(data);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void clearTable() {
        int numRow = df.getRowCount();
        for (int i = 0; i < numRow; i++) {
            df.removeRow(0);
        }
    }

    void clearTextField() {
        txIdGame.setText(null);
        txJudulGame.setText(null);
        txTahunRilis.setText(null);
        txGenreGame.setText(null);
    }

    void simpanData(Tabel_Game B) {
        try {
            String sql = "INSERT INTO game (Id_Game, Judul_Game, Tahun_Rilis, Genre_Game) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, B.getIdGame());
            ps.setString(2, B.getJudulGame());
            ps.setString(3, B.getTahunRilis());
            ps.setString(4, B.getGenreGame());
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan",
                    "Info Proses", JOptionPane.INFORMATION_MESSAGE);
            String[] data = { B.getIdGame(), B.getJudulGame(), B.getTahunRilis(), B.getGenreGame()};
            df.addRow(data);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void hapusData(String kode) {
        try {
            String sql = "DELETE FROM game WHERE game = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, kode);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus", "Info Proses",
                    JOptionPane.INFORMATION_MESSAGE);
            df.removeRow(tab.getSelectedRow());
            clearTextField();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void ubahData(Tabel_Game B, String kode) {
        try {
            String sql = "UPDATE game SET Id_Game = ?, Judul_game = ?, Tahun_Rilis = ?, Genre_Game = ?, Rating_Game = ?, WHERE game = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, B.getIdGame());
            ps.setString(2, B.getJudulGame());
            ps.setString(3, B.getTahunRilis());
            ps.setString(4, B.getGenreGame());
            ps.setString(6, kode);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Diubah", "Info Proses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearTable();
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btNewAksi(ActionEvent evt) {
        clearTextField();
        btEdit.setEnabled(false);
        btDel.setEnabled(false);
        btAdd.setEnabled(true);
    }

    private void btAddAksi(ActionEvent evt) {
        Tabel_Game B = new Tabel_Game();
        B.setIdGame(txIdGame.getText());
        B.setJudulGame(txJudulGame.getText());
        B.setTahunRilis(txTahunRilis.getText());
        B.setGenreGAme(txGenreGame.getText());
        simpanData(B);
    }

    private void btDelAksi(ActionEvent evt) {
        int status;
        status = JOptionPane.showConfirmDialog(null, "Yakin data akan dihapus?",
                "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if (status == 0) {
            hapusData(txIdGame.getText());
        }
    }

    private void btEditAksi(ActionEvent evt) {
        Tabel_Game B = new Tabel_Game();
        B.setIdGame(txIdGame.getText());
        B.setJudulGame(txJudulGame.getText());
        B.setTahunRilis(txTahunRilis.getText());
        B.setGenreGAme(txGenreGame.getText());
        ubahData(B, txIdGame.getText());
    }
    

    private void tabMouseClicked(MouseEvent evt) {
        int row = tab.getSelectedRow();
        txIdGame.setText(tab.getValueAt(row, 0).toString());
        txJudulGame.setText(tab.getValueAt(row, 1).toString());
        txTahunRilis.setText(tab.getValueAt(row, 2).toString());
        txGenreGame.setText(tab.getValueAt(row, 3).toString());
        btEdit.setEnabled(true);
        btDel.setEnabled(true);
        btAdd.setEnabled(false);
    }

    public static void main(String[] args) {
        new FormGame();
    }
}