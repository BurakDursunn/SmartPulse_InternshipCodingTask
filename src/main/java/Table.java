public class Table {
    private String date;
    private double totalTransactionQuantity;
    private double totalTransactionAmount;
    private double weightedAveragePrice;

    public Table() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalTransactionQuantity() {
        return totalTransactionQuantity;
    }

    public void setTotalTransactionQuantity(double totalTransactionQuantity) {
        this.totalTransactionQuantity = totalTransactionQuantity;
    }

    public double getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public void setTotalTransactionAmount(double totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }

    public double getWeightedAveragePrice() {
        return weightedAveragePrice;
    }

    public void setWeightedAveragePrice(double weightedAveragePrice) {
        this.weightedAveragePrice = weightedAveragePrice;
    }

    public String toString() {
        return "Date: " + date + "\n" + "Total Transaction Quantity: " + totalTransactionQuantity + "\n" + "Total Transaction Amount: " + totalTransactionAmount + "\n" + "Weighted Average Price: " + weightedAveragePrice;
    }
}
