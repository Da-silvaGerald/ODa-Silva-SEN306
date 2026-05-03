import java.time.LocalDateTime;

// --- Subsystem Classes ---

class Inventory {
    boolean checkstock(String productId) { return true; }
    void reserve(String productId) { System.out.println("Inventory: Reserved " + productId); }
    void release(String productId) { System.out.println("Inventory: Released " + productId); }
}

class Payment {
    boolean charge(String userId, double amount) { 
        System.out.println("Payment: Charged $" + amount + " to " + userId);
        return true; 
    }
    void refund(String userId, double amount) { System.out.println("Payment: Refunded $" + amount); }
}

class Shipping {
    String createLabel(String address) { return "TRK" + System.currentTimeMillis(); }
    void schedulePickup(String label) { System.out.println("Shipping: Pickup scheduled for " + label); }
    boolean isAvailable() { return true; }
}

class Email {
    void send(String to, String subject, String body) { 
        System.out.println("Email to " + to + " [" + subject + "]: " + body); 
    }
}

// --- New Subsystems ---

class TaxCalculator {
    double calculateTax(double amount, String state) {
        if ("CA".equalsIgnoreCase(state)) {
            return amount * 0.08; // 8% Tax for California
        }
        return 0.0;
    }
}

class Logger {
    void log(String userId, boolean success) {
        String timestamp = LocalDateTime.now().toString();
        String status = success ? "SUCCESS" : "FAIL";
        System.out.println("[LOG - " + timestamp + "] User: " + userId + " Status: " + status);
    }
}

// --- OrderResult Class (Screenshot 2026-05-03 142321.png) ---

class OrderResult {
    private final boolean success;
    private final String trackingNumber;
    private final String message;

    public OrderResult(boolean success, String trackingNumber, String message) {
        this.success = success;
        this.trackingNumber = trackingNumber;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getMessage() { return message; }
}

// --- The Facade Implementation ---

public class CheckoutFacade {
    private Inventory inventory;
    private Payment payment;
    private Shipping shipping;
    private Email email;
    private TaxCalculator taxCalculator;
    private Logger logger;

    public CheckoutFacade() {
        this.inventory = new Inventory();
        this.payment = new Payment();
        this.shipping = new Shipping();
        this.email = new Email();
        this.taxCalculator = new TaxCalculator();
        this.logger = new Logger();
    }

    public OrderResult checkout(String userId, String productId, double price, String address) {
        // Determine state from address (simple logic: looking for "CA" at the end)
        String state = address.trim().toUpperCase().endsWith("CA") ? "CA" : "OTHER";
        
        // Step 1: Calculate Tax and Total
        double tax = taxCalculator.calculateTax(price, state);
        double totalAmount = price + tax;

        // Step 2: Check Stock
        if (!inventory.checkstock(productId)) {
            logger.log(userId, false);
            return new OrderResult(false, null, "Item out of stock");
        }

        inventory.reserve(productId);

        // Step 3: Process Payment (Using the new total amount)
        if (!payment.charge(userId, totalAmount)) {
            inventory.release(productId); // Rollback
            logger.log(userId, false);
            return new OrderResult(false, null, "Payment failed");
        }

        // Step 4: Shipping
        if (!shipping.isAvailable()) {
            payment.refund(userId, totalAmount); // Rollback
            inventory.release(productId); // Rollback
            logger.log(userId, false);
            return new OrderResult(false, null, "Shipping service currently unavailable");
        }

        String tracking = shipping.createLabel(address);
        shipping.schedulePickup(tracking);

        // Step 5: Send Email with final price breakdown
        String emailBody = String.format("Success! Total: $%.2f (Base: $%.2f + Tax: $%.2f). Tracking: %s", 
                                          totalAmount, price, tax, tracking);
        email.send(userId, "Order Confirmation", emailBody);

        // Final Log and Return
        logger.log(userId, true);
        return new OrderResult(true, tracking, "Order processed successfully");
    }

    // --- Main Method for Demo ---
    public static void main(String[] args) {
        CheckoutFacade store = new CheckoutFacade();
        
        // Scenario: User in California (CA)
        System.out.println("--- Scenario 1: California User ---");
        store.checkout("Oluwasegun", "Laptop_XYZ", 1200.00, "123 Tech Way, Los Angeles, CA");
        
        System.out.println("\n--- Scenario 2: Other State ---");
        store.checkout("Deji", "Mouse_ABC", 50.00, "456 Palm St, Lagos, NG");
    }
}