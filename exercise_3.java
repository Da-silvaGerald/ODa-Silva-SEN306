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