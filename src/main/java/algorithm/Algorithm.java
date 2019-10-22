package algorithm;

import com.google.common.primitives.Doubles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


class Algorithm {

    private Generator generator;
    private int initVertexIndex;
    private int finishVertexIndex;
    private List<Car> cars;
    private double minutesLeft;

    private final double[][] roadsLength;
    private final double[] operationTime;
    private final double[] vertexMoney;
    private List<Vertex> currentVertices;
    private Vertex currentVertex;
    private double completionContinuedRatio = 0.4;

    Algorithm(Generator generator, int initVertexIndex, int finishVertexIndex, List<Car> cars, double minutesLeft) {
        this.generator = generator;
        this.initVertexIndex = initVertexIndex;
        this.finishVertexIndex = finishVertexIndex;
        this.cars = cars;
        this.minutesLeft = minutesLeft;
        roadsLength = generator.getRndRoadLength();
        operationTime = generator.getProcessingTime();
        vertexMoney = generator.getRndMoney();
        currentVertices = getVertices(operationTime, vertexMoney);
        currentVertex = getVertexByIndex(initVertexIndex); // начальлая (актуальная) вершина
    }

    Result run() {

        double allMoney = Doubles.asList(vertexMoney).stream().mapToDouble(Double::doubleValue).sum();
        System.out.println("Всего денег: " + allMoney);

        // setWeightUp
        currentVertices = reassemblyVertices();

        // начало пути для всех машин
        cars.forEach(c -> {
            c.setCurrentVertex(currentVertex);
            vertexOperations(c);
        });

        boolean done = false;

        while (true) {
            // самый короткий путь
            double shortestWayTime = cars.stream().mapToDouble(Car::getRoadMinutesLeft).min().getAsDouble();
            for (Car car : cars) {
                // если машина не доехала до вершины
                if (car.getRoadMinutesLeft() > shortestWayTime) {
                    car.setMinusTime(shortestWayTime);
                    continue;
                }
                // если машина доехала до вершины
                if (car.getRoadMinutesLeft() == shortestWayTime) {
                    currentVertices = reassemblyVertices();
                    if (vertexOperations(car)) {
                        done = true;
                        break;
                    }
                }
                // если машина доехала до вершины и осталось еще время
                if (car.getRoadMinutesLeft() < shortestWayTime) {
                    double shortestSubWayTime = shortestWayTime - car.getRoadMinutesLeft();
                    currentVertices = reassemblyVertices();
                    if (vertexOperations(car)) {
                        done = true;
                        break;
                    }
                    car.setMinusTime(shortestSubWayTime);
                }
            }
            if (done) break;
            minutesLeft -= shortestWayTime;
        }
        return new Result(allMoney, cars.stream().mapToDouble(Car::getMoney).sum(), minutesLeft, cars.stream().mapToDouble(Car::getMoney).average().getAsDouble());
    }

    // перерасчет currentVertices по новым сгенерированным коэффициентам
    private List<Vertex> reassemblyVertices() {
        double[][] currentCoef = generator.getRndCoef();
        double[][] weights = generator.getWeights(roadsLength, currentCoef);
        double[] currentWeight = weights[currentVertex.getIndex()];
        return updateRoadUp(currentWeight, currentVertices);
    }

    //производимые с машиной операции в вершине
    private boolean vertexOperations(Car car) {
        // сбор денег из вершины
        double moneyCollected = car.collectCurrentVertex();
        // определение новой вершиы как цели
        Vertex bestWay = getBestWay(car);
        // задается время достижения новой вершины (учитывая время обслуживания)
        car.setRoadMinutesLeft(bestWay.weightUp + bestWay.operationTime);
        // если все машины находятся в конечной вершине - заканчваем
        if (car.getCurrentVertex().index == finishVertexIndex && cars.stream().allMatch(Car::isFilled)) {
            System.out.println("Все харвестеры прибыли на завод переработки спайса");
            double allMoney = cars.stream().mapToDouble(Car::getMoney).sum();
            System.out.println("Всего собрано спайса: " + allMoney);
            System.out.println("Осталось минут: " + minutesLeft);
            return true;
        } else {
            // если находимся в начальной вершине
            if (car.getCurrentVertex().index == initVertexIndex) {
                System.out.println("Харвестер №" + car.index + " приступил к сбору спайса");
            } else {
                // в проццесе сбора бабла
                System.out.println("Харвестер №" + car.index + " собрал спайса: " + moneyCollected + " в пустыне №" + car.getCurrentVertex().index);
            }
            // если напрявляемся в конечную вершину
            if (bestWay.getIndex() == finishVertexIndex) {
                car.setFilled(true);
                System.out.println("Харвестер №" + car.index + " направляется на завод переработки спайса");
            }
            // обнуляем деньги в вершине
            resetMoneyByIndex(car.getCurrentVertex().index);
        }
        // устанавливаем цель - следующую вершину
        car.setCurrentVertex(bestWay);
        return false;
    }

    private void resetMoneyByIndex(int index) {
        currentVertices.stream()
                .filter(v -> v.getIndex() == index)
                .forEach(v -> v.setMoney(0.0));
    }

    private List<Vertex> getVertices(double[] operationTime, double[] vertexMoney) {
        List<Vertex> vertexes = new ArrayList<>();
        for (int i = 0; i < operationTime.length; i++) {
            if (i == finishVertexIndex || i == initVertexIndex)
                vertexes.add(new Vertex(i, operationTime[i], 0.0));
            else
                vertexes.add(new Vertex(i, operationTime[i], vertexMoney[i]));
        }
        return vertexes;
    }

    private List<Vertex> updateRoadUp(double[] currentWeight, List<Vertex> vertices) {
        return vertices.stream().peek(v -> v.setWeightUp(currentWeight[v.getIndex()])).collect(toList());
    }

    private Vertex getVertexByIndex(int index) {
        return currentVertices.stream().filter(v -> v.getIndex() == index).findFirst().get();
    }

    private Vertex getBestWay(Car car) {
        Vertex finishVertex = getVertexByIndex(finishVertexIndex);
        double returnHomeTime = car.getCurrentVertex().operationTime + finishVertex.operationTime;
        if (minutesLeft * completionContinuedRatio < returnHomeTime) {
            return finishVertex;
        } else {
            // тут главная формула!!! )))
            List<Vertex> sortedV = currentVertices.stream()
                    .sorted(Comparator.comparing(v -> v.money / (v.operationTime + v.weightUp)))
                    .collect(Collectors.toList());
            Collections.reverse(sortedV);
            for (Vertex v : sortedV) {
                if (v.money == 0.0 || cars.stream().filter(c -> c.getCurrentVertex() != null).anyMatch(c -> c.getCurrentVertex().index == v.index)) {
                    continue;
                }
                if (minutesLeft * completionContinuedRatio > car.getCurrentVertex().weightUp + v.operationTime
                        && v.money <= car.getLimit() - car.getMoney()) {
                    return v;
                }
            }
            return finishVertex;
        }
    }

}
