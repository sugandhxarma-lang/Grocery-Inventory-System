import java.io.*;
import java.util.*;

/**
 * Grocery Inventory System (PS No. 28)
 * Author: Sugandh Vishwakarma | ERP ID: RU-25-11762
 * Concepts: ArrayList, HashMap, manual Sorting, Methods, File Handling
 */
public class GroceryInventory {

    // ---------- Item record ----------
    static class Item {
        String id, name;
        int qty;
        double price;

        Item(String id, String name, int qty, double price) {
            this.id = id; this.name = name; this.qty = qty; this.price = price;
        }

        String fmtPrice() {
            return (price == (long) price) ? String.valueOf((long) price) : String.valueOf(price);
        }

        public String toString() {
            return id + " | " + name + " | Qty: " + qty + " | Price: " + fmtPrice();
        }
    }

    static final String FILE = "inventory.txt";
    static final int LOW_STOCK = 5;
    static ArrayList<Item> items = new ArrayList<>();
    static HashMap<String, Item> lookup = new HashMap<>();
    static int idCounter = 101;   // first ID = G101
    static Scanner sc = new Scanner(System.in);

    // ---------- File handling ----------
    static void loadFile() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length != 4) continue;
                Item it = new Item(p[0], p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3]));
                items.add(it);
                lookup.put(it.id, it);
                int num = Integer.parseInt(it.id.substring(1));
                if (num >= idCounter) idCounter = num + 1;
            }
        } catch (Exception e) {
            System.out.println("Could not read file: " + e.getMessage());
        }
    }

    static void saveFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Item it : items)
                pw.println(it.id + "," + it.name + "," + it.qty + "," + it.price);
        } catch (IOException e) {
            System.out.println("Could not save file: " + e.getMessage());
        }
    }

    // ---------- Input helpers ----------
    static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid whole number."); }
        }
    }

    static double readDouble(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }

    // ---------- Features ----------
    static void addItem() {
        System.out.print("Item: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty() || name.contains(",")) { System.out.println("Invalid item name."); return; }
        int qty = readInt("Quantity: ");
        double price = readDouble("Price: ");
        if (qty < 0 || price < 0) { System.out.println("Quantity and price cannot be negative."); return; }

        String id = "G" + idCounter++;
        Item it = new Item(id, name, qty, price);
        items.add(it);
        lookup.put(id, it);
        System.out.println("Item added. ID: " + it);
    }

    static void sellItem() {
        System.out.print("Enter ID: ");
        String id = sc.nextLine().trim().toUpperCase();
        Item it = lookup.get(id);                 // O(1) lookup via HashMap
        if (it == null) { System.out.println("Item not found."); return; }
        int q = readInt("Quantity: ");
        if (q <= 0) { System.out.println("Quantity must be greater than 0."); return; }
        if (q > it.qty) {
            System.out.println("Insufficient stock. Available: " + it.qty);
            return;
        }
        it.qty -= q;
        System.out.print("Sold " + q + " of " + it.name + ". Stock: " + it.qty);
        System.out.println(it.qty == 0 ? " (Out of stock)" : "");
    }

    // Manual insertion sort by name (case-insensitive) - no built-in sort used
    static ArrayList<Item> sortedByName() {
        ArrayList<Item> copy = new ArrayList<>(items);
        for (int i = 1; i < copy.size(); i++) {
            Item key = copy.get(i);
            int j = i - 1;
            while (j >= 0 && compareNames(copy.get(j).name, key.name) > 0) {
                copy.set(j + 1, copy.get(j));
                j--;
            }
            copy.set(j + 1, key);
        }
        return copy;
    }

    static int compareNames(String a, String b) {
        a = a.toLowerCase(); b = b.toLowerCase();
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            if (a.charAt(i) != b.charAt(i)) return a.charAt(i) - b.charAt(i);
        }
        return a.length() - b.length();
    }

    static void displaySorted() {
        if (items.isEmpty()) { System.out.println("Inventory is empty."); return; }
        ArrayList<Item> sorted = sortedByName();
        for (int i = 0; i < sorted.size(); i++)
            System.out.println((i + 1) + ". " + sorted.get(i));
    }

    static void lowStockReport() {
        boolean found = false;
        for (Item it : items) {
            if (it.qty < LOW_STOCK) {
                System.out.println("Low stock: " + it.id + " | " + it.name + " | Qty: " + it.qty);
                found = true;
            }
        }
        if (!found) System.out.println("No low-stock items.");
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        loadFile();
        while (true) {
            System.out.println("\n=== Grocery Inventory System ===");
            System.out.println("1. Add Item");
            System.out.println("2. Sell Item");
            System.out.println("3. Display Sorted by Name");
            System.out.println("4. Low-Stock Report (quantity below 5)");
            System.out.println("5. Save & Exit");
            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1: addItem(); break;
                case 2: sellItem(); break;
                case 3: displaySorted(); break;
                case 4: lowStockReport(); break;
                case 5:
                    saveFile();
                    System.out.println("Data saved to " + FILE + ". Goodbye!");
                    return;
                default: System.out.println("Invalid option. Choose 1-5.");
            }
        }
    }
}
