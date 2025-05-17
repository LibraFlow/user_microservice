package backend2.domain;

public enum SubscriptionType {
    MONTHLY(30, 9.99),
    QUARTERLY(90, 24.99),
    YEARLY(365, 89.99);

    private final int durationInDays;
    private final double price;

    SubscriptionType(int durationInDays, double price) {
        this.durationInDays = durationInDays;
        this.price = price;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public double getPrice() {
        return price;
    }
} 