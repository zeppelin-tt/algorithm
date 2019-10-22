package algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Vertex {
    int index;
    double operationTime;
    double money;
    double weightUp;

    Vertex(int index, double operationTime, double money) {
        this.index = index;
        this.operationTime = operationTime;
        this.money = money;
    }

}
