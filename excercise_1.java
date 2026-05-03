class Inventory {
    boolean checkstock(String productId) { return true; }
    void reserve(String productId) { System.out.println("Reserved " + productId); }
    void release(String productId) { System.out.println("Released " + productId); }
}

class Payment {
    boolean charge(String userId, double amount) { return true; }
    void refund(String userId, double amount) { System.out.println("Refunded " + amount); }
}

class Shipping {
    String createLabel(String address) { return "TRK" + System.currentTimeMillis(); }
    void schedulePickup(String label) { System.out.println("Pickup scheduled for " + label); }
    boolean isAvailable() { return true; } // for rollback demo
}

class Email {
    void send(String to, String subject, String body) { 
        System.out.println("Email sent to " + to + ": " + subject); 
    }
}


class OrderResult {
    private final boolean success;
    private final String trackingNumber;
    private final String message;

    public OrderResult(boolean success, String trackingNumber, String message) {
        this.success = success;
        this.trackingNumber = trackingNumber;
        this.message = message;
    }

    // Getters for encapsulation
    public boolean isSuccess() { return success; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getMessage() { return message; }
}

// --- CheckoutFacade Implementation ---

public class CheckoutFacade {
    private Inventory inventory;
    private Payment payment;
    private Shipping shipping;
    private Email email;

    public CheckoutFacade() {
        this.inventory = new Inventory();
        this.payment = new Payment();
        this.shipping = new Shipping();
        this.email = new Email();
    }

    public OrderResult checkout(String userId, String productId, double price, String address) {
        System.out.println("--- Starting Checkout Process ---");

        // 1. Check Stock
        if (!inventory.checkstock(productId)) {
            return new OrderResult(false, null, "Item out of stock");
        }

        // 2. Reserve Item
        inventory.reserve(productId);

        // 3. Process Payment
        if (!payment.charge(userId, price)) {
            inventory.release(productId); // Rollback
            return new OrderResult(false, null, "Payment failed");
        }

        // 4. Handle Shipping
        if (!shipping.isAvailable()) {
            payment.refund(userId, price); // Rollback
            inventory.release(productId); // Rollback
            return new OrderResult(false, null, "Shipping service unavailable");
        }

        String tracking = shipping.createLabel(address);
        shipping.schedulePickup(tracking);

        // 5. Send Confirmation
        email.send(userId, "Order Confirmed", "Your tracking number is: " + tracking);

        return new OrderResult(true, tracking, "Order completed successfully!");
    }

    // Main method to run the demo
    public static void main(String[] args) {
        CheckoutFacade store = new CheckoutFacade();
        
        // Execute a test checkout
        OrderResult result = store.checkout("user_123", "PROD_99", 25000.00, "Lekki, Lagos");

        System.out.println("\n--- Final Result ---");
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        if (result.isSuccess()) {
            System.out.println("Tracking: " + result.getTrackingNumber());
        }
    }
}