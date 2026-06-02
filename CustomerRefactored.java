// File name: CustomerRefactored.java

// ==========================================
// 1. CUSTOMER DATA CLASS
// ==========================================
class Customer {
    private final String name;
    private final String address;
    private final int customerType;
    private final String email;
    private final boolean isVip;
    private double balance; // Maps to the original 'd' variable

    public Customer(String name, String address, int customerType, String email, boolean isVip, double balance) {
        if (customerType < 0) {
            throw new IllegalArgumentException("Customer type cannot be negative.");
        }
        this.name = name;
        this.address = address;
        this.customerType = customerType;
        this.email = email;
        this.isVip = isVip;
        this.balance = balance;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getCustomerType() { return customerType; }
    public String getEmail() { return email; }
    public boolean isVip() { return isVip; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

// ==========================================
// 2. REFACTORED PROCESSING ENGINE
// ==========================================
class OrderProcessor {

    /**
     * Orchestrates the customer order execution cycle.
     * Splitting logic into functional pieces addresses cohesion flaws.
     */
    public void processCustomer(Customer customer, double[] orders, int orderCount) {
        // Task 4: Defensive Input Validation
        validateInputs(orders, orderCount);

        // Core Calculations (Pure Functions)
        double rawSum = calculateOrderSum(orders, orderCount);
        double discountRate = getDiscountRate(customer.getCustomerType());
        double finalTotal = rawSum * (1.0 - discountRate);

        // Side-Effect Actions (Procedures)
        String notificationMessage = buildNotificationMessage(customer, finalTotal);
        sendNotifications(customer, notificationMessage);
        
        // Parameter tracking fix: Mutates the object's field directly
        customer.setBalance(finalTotal); 
    }

    // Input Validation Routine
    private void validateInputs(double[] orders, int orderCount) {
        if (orders == null || orderCount < 0 || orderCount > orders.length) {
            throw new IllegalArgumentException("Invalid order array or order count mapping.");
        }
        for (int i = 0; i < orderCount; i++) {
            if (orders[i] < 0) {
                throw new IllegalArgumentException("Order amounts cannot be negative.");
            }
        }
    }

    // Noun Function: Gathers calculation metrics
    private double calculateOrderSum(double[] orders, int orderCount) {
        double sum = 0;
        for (int i = 0; i < orderCount; i++) {
            sum += orders[i];
        }
        return sum;
    }

    // Noun Function: Determines business rule discount logic
    private double getDiscountRate(int customerType) {
        if (customerType == 1) return 0.1;
        if (customerType == 2) return 0.2;
        return 0.0;
    }

    // Noun Function: Decouples text formatting syntax
    private String buildNotificationMessage(Customer customer, double total) {
        String msg = "Hello " + customer.getName() + " of " + customer.getAddress() + ", your total is " + total;
        if (customer.isVip()) {
            msg += " (VIP)";
        }
        return msg;
    }

    // Verb-Object Procedure: Manages outward application interaction
    private void sendNotifications(Customer customer, String message) {
        System.out.println("[Console Output]: " + message);
        if (customer.getEmail() != null) {
            sendEmail(customer.getEmail(), message);
        }
    }

    // Mock dependency emulation
    private void sendEmail(String email, String msg) {
        System.out.println("[Email Sent to " + email + "]: " + msg);
    }
}

// ==========================================
// 3. RUNNABLE EXECUTION HARNESS
// ==========================================
public class CustomerRefactored {
    public static void main(String[] args) {
        // Instantiate our processors
        OrderProcessor processor = new OrderProcessor();

        // Task 3: Wrap high parameter counts inside a domain object
        Customer customer = new Customer(
            "Alice Smith", 
            "123 Main St", 
            2, // Customer type 2 receives a 20% discount
            "alice@example.com", 
            true, // VIP Status
            0.0 // Starting balance tracking
        );

        // Simulation transaction orders
        double[] orderHistory = { 100.0, 50.0, 250.0, 0.0 };
        int activeOrderCount = 3; // Process the first 3 indices

        System.out.println("--- Starting Execution Flow ---");
        System.out.println("Initial Customer Balance: $" + customer.getBalance());
        
        // Execute Refactored Logic
        processor.processCustomer(customer, orderHistory, activeOrderCount);

        System.out.println("Final Customer Balance (Successfully updated via reference): $" + customer.getBalance());
        System.out.println("--------------------------------");
    }
}