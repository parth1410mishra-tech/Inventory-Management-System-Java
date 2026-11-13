package com.parth.inventory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class InventoryManagementApp {
    private static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            createTable(con);
            System.out.println("Connected to retail_db.");
            menu(con);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("Check MySQL is running and DBConnection credentials are correct.");
        }
    }
    private static void createTable(Connection con) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
              product_id INT PRIMARY KEY, product_name VARCHAR(100) NOT NULL,
              category VARCHAR(60) NOT NULL, quantity INT NOT NULL DEFAULT 0,
              price DECIMAL(10,2) NOT NULL, CHECK(quantity >= 0), CHECK(price > 0)
            )
            """;
        try (Statement st = con.createStatement()) { st.execute(sql); }
    }
    private static void menu(Connection con) throws SQLException {
        while (true) {
            System.out.println("\n===== INVENTORY MANAGEMENT =====");
            System.out.println("1. Insert product records");
            System.out.println("2. Retrieve by category (quantity < 20), price descending");
            System.out.println("3. Add stock");
            System.out.println("4. Search product and update price");
            System.out.println("5. Display total inventory value");
            System.out.println("6. Delete product only if quantity is zero");
            System.out.println("0. Exit");
            int choice = integer("Choice: ");
            switch (choice) {
                case 1 -> insertProducts(con);
                case 2 -> retrieveByCategory(con);
                case 3 -> addStock(con);
                case 4 -> updatePrice(con);
                case 5 -> inventoryValue(con);
                case 6 -> deleteZeroStock(con);
                case 0 -> { System.out.println("Goodbye."); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    private static void insertProducts(Connection con) {
        String sql = "INSERT INTO products VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int n = integer("How many products? ");
            for (int i = 0; i < n; i++) {
                int id = integer("Product ID: ");
                System.out.print("Product name: "); String name = sc.nextLine().trim();
                System.out.print("Category: "); String category = sc.nextLine().trim();
                int qty = integer("Quantity: ");
                BigDecimal price = decimal("Price: ");
                if (name.isBlank() || category.isBlank() || qty < 0 || price.signum() <= 0) {
                    System.out.println("Invalid values; record skipped."); continue;
                }
                ps.setInt(1, id); ps.setString(2, name); ps.setString(3, category);
                ps.setInt(4, qty); ps.setBigDecimal(5, price);
                try { ps.executeUpdate(); System.out.println("Inserted."); }
                catch (SQLIntegrityConstraintViolationException ex) {
                    System.out.println("Duplicate product ID " + id + "; skipped.");
                }
            }
        } catch (SQLException e) { System.out.println("Insert error: " + e.getMessage()); }
    }
    private static void retrieveByCategory(Connection con) {
        System.out.print("Category: "); String category = sc.nextLine().trim();
        String sql = "SELECT * FROM products WHERE category = ? AND quantity < 20 ORDER BY price DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) { printRows(rs); }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    private static void addStock(Connection con) {
        int id = integer("Product ID: "); int amount = integer("Quantity to add: ");
        if (amount <= 0) { System.out.println("Added quantity must be positive."); return; }
        String sql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, amount); ps.setInt(2, id);
            System.out.println(ps.executeUpdate() == 0 ? "Product does not exist." : "Stock updated.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    private static void updatePrice(Connection con) {
        int id = integer("Product ID: "); BigDecimal price = decimal("New price: ");
        if (price.signum() <= 0) { System.out.println("Price must be greater than zero."); return; }
        String find = "SELECT * FROM products WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(find)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { System.out.println("Product does not exist."); return; }
            }
            try (PreparedStatement update = con.prepareStatement("UPDATE products SET price = ? WHERE product_id = ?")) {
                update.setBigDecimal(1, price); update.setInt(2, id); update.executeUpdate();
            }
            try (PreparedStatement again = con.prepareStatement(find)) {
                again.setInt(1, id);
                try (ResultSet rs = again.executeQuery()) { printRows(rs); }
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    private static void inventoryValue(Connection con) {
        String sql = "SELECT COALESCE(SUM(quantity * price), 0) AS total FROM products";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) System.out.println("Total inventory value: " + rs.getBigDecimal("total"));
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    private static void deleteZeroStock(Connection con) {
        int id = integer("Product ID to delete: ");
        String sql = "DELETE FROM products WHERE product_id = ? AND quantity = 0";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            System.out.println(ps.executeUpdate() == 0
                ? "Cannot delete: product missing or quantity is not zero." : "Product deleted.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    private static void printRows(ResultSet rs) throws SQLException {
        boolean found = false;
        System.out.printf("%-8s %-20s %-15s %-10s %-10s%n", "ID", "NAME", "CATEGORY", "QUANTITY", "PRICE");
        while (rs.next()) {
            found = true;
            System.out.printf("%-8d %-20s %-15s %-10d %-10s%n",
                rs.getInt("product_id"), rs.getString("product_name"),
                rs.getString("category"), rs.getInt("quantity"), rs.getBigDecimal("price"));
        }
        if (!found) System.out.println("No matching products.");
    }
    private static int integer(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a whole number."); }
        }
    }
    private static BigDecimal decimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return new BigDecimal(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a valid amount."); }
        }
    }
}