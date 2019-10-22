package algorithm;


class Result {
    double allMoney;
    double money;
    double timeLost;
    double avgMoneyInCar;

    Result(double allMoney, double money, double timeLost, double avgMoneyInCar) {
        this.allMoney = allMoney;
        this.money = money;
        this.timeLost = timeLost;
        this.avgMoneyInCar = avgMoneyInCar;
    }

    @Override
    public String toString() {
        return "Result{" +
                "allMoney=" + allMoney +
                ", money=" + money +
                ", timeLost=" + timeLost +
                ", avgMoneyInCar=" + avgMoneyInCar +
                '}';
    }
}

