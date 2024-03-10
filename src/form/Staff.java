package form;
//mã hóa MK

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class Staff extends javax.swing.JPanel {

    Connection conn = null;
    public int Role;

    public Staff(int Role) {
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

    // Hiển thị danh sách nhân viên
    private void displayData() {
        try {
            // Lấy vai trò của người dùng sau khi đăng nhập
            int userRole = getUserRole(); // Phương thức này cần được triển khai để lấy vai trò của người dùng

            String sql = null;

            if (userRole == 0) {
                sql = "SELECT * FROM users WHERE Role = 1";
            } else if (userRole == 1) {
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
                    rs.getString("Password"),
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

    //mã hóa MD5
    private String hashPasswordMD5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        // Chuyển byte thành chuỗi hex
        StringBuilder sb = new StringBuilder();
        for (byte b : byteData) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    //thêm nhân viên
    private void addStaff() {
        try {
            int userRole = getUserRole();
            if (userRole == 0) {
                String email = txtEmail.getText();
                String username = txtUserName.getText();
                String fullname = txtFullName.getText();
                String pass = txtPass.getText();
                String phone = txtPhone.getText();
                String des = txtDes.getText();

                // Kiểm tra xem các ô text có để trống hay không
                if (email.isEmpty() || username.isEmpty() || fullname.isEmpty() || pass.isEmpty() || phone.isEmpty() || des.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin");
                } else {
                    // Mã hóa mật khẩu bằng MD5 trước khi thêm vào cơ sở dữ liệu
                    String hashedPass = hashPasswordMD5(pass);

                    // Tạo câu lệnh SQL để chèn dữ liệu
                    String sql = "INSERT INTO users (Email, Username, Fullname, Password, Phone, Description, Role) VALUES (?, ?, ?, ?, ?, ?, 1)";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, email);
                    statement.setString(2, username);
                    statement.setString(3, fullname);
                    statement.setString(4, hashedPass);
                    statement.setString(5, phone);
                    statement.setString(6, des);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(this, "Nhân viên được thêm thành công!");
                        txtEmail.setText("");
                        txtUserName.setText("");
                        txtFullName.setText("");
                        txtPass.setText("");
                        txtPhone.setText("");
                        txtDes.setText("");
                        displayData();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(this, "Lỗi mã hóa mật khẩu: " + e.getMessage());
        }
    }

    //xóa nhân viên
    private void deleteStaff() {
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

    //Update nhân viên
    private void editStaff() {
        try {
            int selectedRow = table1.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to edit.");
                return;
            }

            int userID = (int) table1.getValueAt(selectedRow, 0);

            String email = txtEmail.getText();
            String username = txtUserName.getText();
            String fullname = txtFullName.getText();
            String pass = txtPass.getText();
            String phone = txtPhone.getText();
            String des = txtDes.getText();

            // Tạo câu lệnh SQL để cập nhật sản phẩm trong cơ sở dữ liệu
            String sql = "UPDATE users SET Email = ?, Username = ?, Fullname = ?, Password = ?, Phone = ?, Description = ? WHERE UserID = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, username);
            statement.setString(3, fullname);
            statement.setString(4, pass);
            statement.setString(5, phone);
            statement.setString(6, des);
            statement.setInt(7, userID);

            // Thực thi câu lệnh
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Staff updated successfully!");
                displayData(); // Hiển thị lại dữ liệu sau khi sửa đổi
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating Staff: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        roundPanel1 = new swing.RoundPanel();
        jLabel2 = new javax.swing.JLabel();
        lbEmail = new javax.swing.JLabel();
        lbUserName = new javax.swing.JLabel();
        lbFullName = new javax.swing.JLabel();
        lbPhone = new javax.swing.JLabel();
        lbPass = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtUserName = new javax.swing.JTextField();
        txtPass = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        txtFullName = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        lbDes = new javax.swing.JLabel();
        txtDes = new javax.swing.JTextField();
        btnDel = new javax.swing.JButton();
        btnadd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new swing.Table();
        jLabel3 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        roundPanel2 = new swing.RoundPanel();
        jLabel1 = new javax.swing.JLabel();

        roundPanel1.setBackground(new java.awt.Color(60, 60, 60));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Add Staff");

        lbEmail.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbEmail.setForeground(new java.awt.Color(255, 255, 255));
        lbEmail.setText("Email");

        lbUserName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbUserName.setForeground(new java.awt.Color(255, 255, 255));
        lbUserName.setText("User Name");

        lbFullName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbFullName.setForeground(new java.awt.Color(255, 255, 255));
        lbFullName.setText("Full Name");

        lbPhone.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbPhone.setForeground(new java.awt.Color(255, 255, 255));
        lbPhone.setText("Phone");

        lbPass.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbPass.setForeground(new java.awt.Color(255, 255, 255));
        lbPass.setText("PassWord");

        btnUpdate.setText("Update Staff");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        lbDes.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbDes.setForeground(new java.awt.Color(255, 255, 255));
        lbDes.setText("Description");

        btnDel.setText("Delete Staff");
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelActionPerformed(evt);
            }
        });

        btnadd.setText("Add New Staff");
        btnadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddActionPerformed(evt);
            }
        });

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "Email", "User Name", "Full Name", "Password", "Phone", "Description"
            }
        ));
        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table1);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(4).setPreferredWidth(200);
            table1.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Search");

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbFullName)
                                    .addComponent(lbUserName)
                                    .addComponent(lbEmail))
                                .addGap(6, 6, 6)
                                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(roundPanel1Layout.createSequentialGroup()
                                        .addComponent(lbDes)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDes, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(roundPanel1Layout.createSequentialGroup()
                                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lbPass)
                                            .addComponent(lbPhone))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtPhone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtPass, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(17, 17, 17))))))
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addComponent(btnadd)
                        .addGap(52, 52, 52)
                        .addComponent(btnUpdate)
                        .addGap(80, 80, 80)
                        .addComponent(btnDel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(41, 41, 41)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbEmail)
                    .addComponent(lbPass)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbUserName)
                    .addComponent(lbPhone)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFullName)
                    .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDes)
                    .addComponent(txtDes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnDel)
                    .addComponent(btnadd))
                .addGap(23, 23, 23))
        );

        roundPanel2.setBackground(new java.awt.Color(60, 60, 60));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Staff Management");

        javax.swing.GroupLayout roundPanel2Layout = new javax.swing.GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                .addContainerGap())
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        // TODO add your handling code here:
        deleteStaff();
    }//GEN-LAST:event_btnDelActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        editStaff();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddActionPerformed
        // TODO add your handling code here:
        addStaff();
    }//GEN-LAST:event_btnaddActionPerformed

    private void table1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table1MouseClicked
        // TODO add your handling code here:

        int selectedRow = table1.getSelectedRow();

        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        txtEmail.setText(model.getValueAt(selectedRow, 1).toString());
        txtUserName.setText(model.getValueAt(selectedRow, 2).toString());
        txtFullName.setText(model.getValueAt(selectedRow, 3).toString());
        txtPass.setText(model.getValueAt(selectedRow, 4).toString());
        txtPhone.setText(model.getValueAt(selectedRow, 5).toString());
        txtDes.setText(model.getValueAt(selectedRow, 6).toString());
    }//GEN-LAST:event_table1MouseClicked

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        try {

            int userRole = getUserRole();

            String searchText = txtSearch.getText().trim();
            String sql = null;

            if (userRole == 0) {
                sql = "SELECT * FROM users WHERE Role = 1";
            } else if (userRole == 1) {
                sql = "SELECT * FROM users WHERE Role = 2";
            }

            if (!searchText.isEmpty()) {
                sql += " AND (Email LIKE '%" + searchText + "%' OR Username LIKE '%" + searchText + "%' OR Fullname LIKE '%" + searchText + "%' "
                        + "OR Phone LIKE '%" + searchText + "%' " + "OR Description LIKE '%" + searchText + "%' " + "OR UserID LIKE '%" + searchText + "%')";
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
                    rs.getString("Password"),
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
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnadd;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbDes;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbFullName;
    private javax.swing.JLabel lbPass;
    private javax.swing.JLabel lbPhone;
    private javax.swing.JLabel lbUserName;
    private swing.RoundPanel roundPanel1;
    private swing.RoundPanel roundPanel2;
    private swing.Table table1;
    private javax.swing.JTextField txtDes;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtPass;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
