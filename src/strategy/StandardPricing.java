package strategy;

public class StandardPricing implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) {
        return basePrice; // No discount
    }
}
