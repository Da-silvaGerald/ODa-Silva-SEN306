// DO NOT MODIFY THIS CLASS
public class LegacyOrderProcessor {
    public void processorder(String customerEmail, String itemCode,
                            double amount, String deliveryAddress) {
        // Hardcoded dependencies inside
        Inventory inv = new Inventory();
        Payment pay = new Payment();
        Shipping ship = new Shipping();
        Email email = new Email();

        if (!inv.checkStock(itemCode)) {
            System.out.println("Out of stock");
            return;
        }

        if (!pay.charge(customerEmail, amount)) {
            System.out.println("Payment fail");
            return;
        }

        inv.reserve(itemCode);
        String label = ship.createLabel(deliveryAddress);
        ship.schedulePickup(label);
        email.send(customerEmail, "Order", "Shipped");
        System.out.println("Order complete");
    }
}

public class LegacyOrderFacade {
    // Composition: Wrapping the legacy class instance
    private final LegacyOrderProcessor legacyProcessor;

    public LegacyOrderFacade() {
        this.legacyProcessor = new LegacyOrderProcessor();
    }

    /**
     * Modernized interface for the legacy system.
     * Clients call this instead of the messy legacy method.
     */
    public void placeOrder(String email, String product, double price, String address) {
        // Delegating the call to the legacy object
        legacyProcessor.processorder(email, product, price, address);
    }
}