package strategy;

public class DiscountPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * 0.9; // 10% discount
    }
}
