import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    
    public Main() {
        setTitle("Sales & Delivery System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Test database connection first
        testDatabaseConnection();
        
        // Create main panel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Sales & Delivery Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Username:"), gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(buttonPanel, gbc);
        
        add(panel);
        
        // Add action listeners
        loginButton.addActionListener(e -> authenticateUser());
        cancelButton.addActionListener(e -> System.exit(0));
        
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void testDatabaseConnection() {
        try {
            System.out.println("Testing database connection...");
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("✓ Database connection successful!");
                conn.close();
            } else {
                System.out.println("✗ Database connection failed!");
                JOptionPane.showMessageDialog(this, 
                    "Database connection failed!\nPlease check:\n1. XAMPP MySQL is running\n2. Database 'sales_db' exists\n3. MySQL Connector JAR is in classpath",
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("✗ Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);
        
        try {
            System.out.println("Attempting login for user: " + username);
            Connection conn = DBConnection.getConnection();
            
            if (conn == null) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot connect to database!\nPlease check if XAMPP MySQL is running.",
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Login successful for: " + username);
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                MainGUI mainGUI = new MainGUI();
                mainGUI.setVisible(true);
            } else {
                System.out.println("Login failed for: " + username);
                JOptionPane.showMessageDialog(this, "Invalid username or password!", 
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException ex) {
            System.out.println("SQL Error: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage() + "\nCheck console for details.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Reset cursor
            setCursor(Cursor.getDefaultCursor());
            loginButton.setEnabled(true);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            // Use the new blue themed login
            new BlueThemedLoginGUI().setVisible(true);
        });
    }
}