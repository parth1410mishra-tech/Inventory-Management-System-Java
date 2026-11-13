# Inventory Management System — Java JDBC + MySQL

Menu-driven console application for the assignment's five inventory operations.

## Requirements
- JDK 17+, Maven, MySQL Server 8+, IntelliJ IDEA

## Setup
1. Run `database/schema.sql` in MySQL Workbench.
2. In `DBConnection.java`, replace `YOUR_MYSQL_PASSWORD` with your local MySQL password.
3. Open this folder in IntelliJ as a Maven project and wait for dependencies to download.
4. Run `com.parth.inventory.InventoryManagementApp`.
5. Select option 1 and add at least three products.

Example products:
- `101, Wireless Mouse, Accessories, 12, 599.00`
- `102, Keyboard, Accessories, 8, 899.00`
- `103, Monitor, Electronics, 25, 7499.00`

## Assignment coverage
1. Insert with `PreparedStatement`; duplicate IDs are skipped with a message.
2. Filter category and quantity below 20; order by price descending.
3. Increase quantity for an existing product ID.
4. Search and update price only when product exists and new price is positive.
5. Calculate `SUM(quantity * price)` and delete only when quantity is zero.

## Three AI prompts used
1. "Help me create an Inventory Management System using Java JDBC and MySQL. Provide a Maven structure, JDBC connection class and database setup."
2. "Write menu-driven Java code to insert product records using PreparedStatement and handle duplicate primary keys."
3. "Implement category filtering with quantity below 20 and descending price order, stock updates, positive-price validation, inventory SUM, and deletion only for zero-quantity products."

## Submission checklist
Run each operation and capture your own screenshots of IntelliJ, console output and MySQL data. Keep real credentials out of GitHub.