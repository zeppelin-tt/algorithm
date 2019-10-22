package algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Car {
    int index;
    private double money = 0;
    private double limit;
    private boolean filled = false;
    private Vertex currentVertex;
    private double roadMinutesLeft = -1.0;

    Car(int index, double limit) {
        this.index = index;
        this.limit = limit;
    }

    void setMinusTime(double min) {
        this.roadMinutesLeft -= min;
    }

    double collectCurrentVertex() {
        double money = this.currentVertex.money;
        this.money += money;
        this.currentVertex.setMoney(0.0);
        return money;
    }

    @Override
    public String toString() {
        return "Car{" +
                "index=" + index +
                ", money=" + money +
                ", limit=" + limit +
                ", filled=" + filled +
                ", currentVertex=" + currentVertex +
                ", roadMinutesLeft=" + roadMinutesLeft +
                '}';
    }

}
