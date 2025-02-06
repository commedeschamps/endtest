package services;

import strategy.PricingStrategy;

public class OrderService {
    private PricingStrategy pricingStrategy;

    public OrderService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public double calculateTotal(double basePrice) {
        return pricingStrategy.calculatePrice(basePrice);
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }
}
