package algorithm;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Result> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(start());
            System.out.println("\n   ==============================================   \n");
        }
        Result result = new Result(
                results.stream().mapToDouble(r -> r.allMoney).average().getAsDouble(),
                results.stream().mapToDouble(r -> r.money).average().getAsDouble(),
                results.stream().mapToDouble(r -> r.timeLost).average().getAsDouble(),
                results.stream().mapToDouble(r -> r.avgMoneyInCar).average().getAsDouble()
        );
        System.out.println(result);

    }

    private static Result start() {
        Generator generator = Generator.builder()
                .vertexCount(600)
                .maxProcessingTime(10.0)
                .vertexLimitMoney(50.0)
                .maxRoadLength(15.0)
                .maxJamsCoefficient(0.4)
                .build();

        int initVertexIndex = generator.getRndVertexIndex();
        int finishVertexIndex = generator.getRndVertexIndex(new int[]{initVertexIndex});

        List<Car> cars = generator.buildCars(4, 2000.0);

        return new Algorithm(generator, initVertexIndex, finishVertexIndex, cars, 480.0).run();
    }

}
