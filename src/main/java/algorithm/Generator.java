package algorithm;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static algorithm.Util.containsInt;

@Builder
class Generator {

    private int vertexCount;
    private double maxProcessingTime;
    private double vertexLimitMoney;
    private double maxRoadLength;
    private double maxJamsCoefficient;

    int getRndCarCount() {
        return ThreadLocalRandom.current().nextInt(1, 5);
    }

    int getRndVertexIndex(int[] exclude) {
        int result;
        do {
            result = ThreadLocalRandom.current().nextInt(vertexCount);
        } while (containsInt(exclude, result));
        return result;
    }

    int getRndVertexIndex() {
        return ThreadLocalRandom.current().nextInt(vertexCount);
    }

    double[] getRndMoney() {
        double[] money = new double[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            money[i] = rndDouble(0.0, vertexLimitMoney);
        }
        return money;
    }

    double[] getProcessingTime() {
        double[] time = new double[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            time[i] = rndDouble(0.0, maxProcessingTime);
        }
        return time;
    }

    double[][] getRndRoadLength() {
        return getRndEdges(maxRoadLength);
    }

    double[][] getRndCoef() {
        return getRndEdges(maxJamsCoefficient);
    }

    double[][] getRndEdges(double maxCoef) {
        double[][] m = new double[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                double edge = rndDouble(0.0, maxCoef);
                if (i == j) {
                    m[i][j] = 0;
                } else {
                    if (m[j][i] == 0) {
                        m[j][i] = edge;
                    }
                    if (m[i][j] == 0){
                        m[i][j] = edge;
                    }
                }
            }
        }
        return m;
    }

    double[][] getWeights(double[][] m, double[][] coef) {
        double[][] result = new double[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                result[i][j] = m[i][j] + (m[i][j] * coef[i][j]);
            }
        }
        return result;
    }

    private double rndDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    List<Car> buildCars(int count, double minLimit, double maxLimit) {
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cars.add(new Car(i + 1, rndDouble(minLimit, maxLimit)));
        }
        return cars;
    }

    List<Car> buildCars(int count, double limit) {
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cars.add(new Car(i + 1, limit));
        }
        return cars;
    }

    void viewEdges(double[][] edges) {
        for (int i = 0; i < edges[0].length; i++) {
            for (int j = 0; j < edges.length; j++) {
                System.out.printf("%.2f  ", edges[i][j]);
            }
            System.out.println();
        }
    }
}
