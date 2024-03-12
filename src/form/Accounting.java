package form;

import javax.swing.table.*;
import java.sql.*;
import javax.swing.*;
public class Accounting extends javax.swing.JPanel {
    Connection conn = null;
    public int Role;
    
    public Accounting(int Role) {
        this.Role = Role;
        initComponents();
        setOpaque(false);
        table1.addTableStyle(jScrollPane1);
        connectDB();
        displayData();
    }

    public int getUserRole() {
        return Role;
    }
    
    //Kết nối DB
    private void connectDB() {
        try {
            String url = "jdbc:mysql://localhost:3306/java";
            String username = "root";
            String password = "";
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }
    
    // Hiển thị danh sách users
    private void displayData() {
        try {
            // Lấy vai trò của người dùng sau khi đăng nhập
            int userRole = getUserRole(); // Phương thức này cần được triển khai để lấy vai trò của người dùng

            String sql = null;

            if (userRole == 1) {
                sql = "SELECT * FROM users WHERE Role = 2";
            } 

            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("UserID"),
                    rs.getString("Email"),
                    rs.getString("Username"),
                    rs.getString("Fullname"),
                    rs.getString("Phone"),
                    rs.getString("Description")
                };
                model.addRow(row);
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error displaying data: " + e.getMessage());
        }
    }
    
    //xóa Người dùng
    private void deleteUser() {
        try {
            int selectedRow = table1.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a staff to delete.");
                return;
            }

            int userID = (int) table1.getValueAt(selectedRow, 0);

            String sql = "DELETE FROM users WHERE UserID = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, userID);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Staff deleted successfully!");
                displayData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        roundPanel2 = new swing.RoundPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new swing.Table();
        btnDel = new javax.swing.JButton();

        roundPanel2.setBackground(new java.awt.Color(60, 60, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("User Management");

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "UserI D", "Email", "User Name", "Full Name", "Phone", "Description"
            }
        ));
        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table1);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(0).setResizable(false);
            table1.getColumnModel().getColumn(3).setPreferredWidth(200);
            table1.getColumnModel().getColumn(4).setPreferredWidth(100);
        }

        btnDel.setText("Delete");
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundPanel2Layout = new javax.swing.GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void table1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_table1MouseClicked

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        // TODO add your handling code here:
        deleteUser();
    }//GEN-LAST:event_btnDelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private swing.RoundPanel roundPanel2;
    private swing.Table table1;
    // End of variables declaration//GEN-END:variables
}
