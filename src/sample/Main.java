package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Clipping");
        primaryStage.setScene(new Scene(root, 700, 500));
        Canvas canvas = new Canvas(700, 500);
        root.setCenter(canvas);

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setLineWidth(3);

        List<Map.Entry<Double, Double>> screen = new ArrayList<>();
        screen.add(new AbstractMap.SimpleEntry<>(150.0, 150.0));
        screen.add(new AbstractMap.SimpleEntry<>(150.0, 350.0));
        screen.add(new AbstractMap.SimpleEntry<>(550.0, 350.0));
        screen.add(new AbstractMap.SimpleEntry<>(550.0, 150.0));

        for(int i = 0; i < screen.size(); i++){
            g.strokeLine(
                    screen.get(i).getKey(),
                    screen.get(i).getValue(),
                    screen.get((i+1)%screen.size()).getKey(),
                    screen.get((i+1)%screen.size()).getValue());
        }

        List<Map.Entry<Double, Double>> line = new ArrayList<>();
        line.add(new AbstractMap.SimpleEntry<>(70.0, 320.0));
        line.add(new AbstractMap.SimpleEntry<>(320.0, 70.0));

        g.strokeLine(line.get(0).getKey(), line.get(0).getValue(), line.get(1).getKey(), line.get(1).getValue());

        List<Map.Entry<Double, Double>> l = getIntersectionPoints(line.get(0), line.get(1), screen);
        if(!l.isEmpty()){
            g.setStroke(Color.RED);
            g.strokeLine(l.get(0).getKey(), l.get(0).getValue(), l.get(1).getKey(), l.get(1).getValue());
            g.setStroke(Color.BLACK);
        }

        List<Map.Entry<Double, Double>> line1 = new ArrayList<>();
        line1.add(new AbstractMap.SimpleEntry<>(30.0, 300.0));
        line1.add(new AbstractMap.SimpleEntry<>(100.0, 30.0));

        g.strokeLine(line1.get(0).getKey(), line1.get(0).getValue(), line1.get(1).getKey(), line1.get(1).getValue());

        List<Map.Entry<Double, Double>> l1 = getIntersectionPoints(line1.get(0), line1.get(1), screen);
        if(!l1.isEmpty()){
            g.setStroke(Color.RED);
            g.strokeLine(l1.get(0).getKey(), l1.get(0).getValue(), l1.get(1).getKey(), l1.get(1).getValue());
            g.setStroke(Color.BLACK);
        }

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    static boolean isEqual(double a, double b){
        return Math.abs(a - b) < 1e-5;
    }

    static Map.Entry<Double, Double> getIntersectionPoint(
            Map.Entry<Double, Double> l1p1, 
            Map.Entry<Double, Double> l1p2,
            Map.Entry<Double, Double> l2p1,
            Map.Entry<Double, Double> l2p2){
        double A1 = l1p2.getValue() - l1p1.getValue();
        double B1 = l1p1.getKey() - l1p2.getKey();
        double C1 = A1 * l1p1.getKey() + B1 * l1p1.getValue();

        double A2 = l2p2.getValue() - l2p1.getValue();
        double B2 = l2p1.getKey() - l2p2.getKey();
        double C2 = A2 * l2p1.getKey() + B2 * l2p1.getValue();

        //lines are parallel
        double det = A1 * B2 - A2 * B1;
        if(isEqual(det, 0)){
            return null; //parallel lines
        } else {
            double x = (B2 * C1 - B1 * C2) / det;
            double y = (A1 * C2 - A2 * C1) / det;
            boolean online1 = ((Math.min(l1p1.getKey(), l1p2.getKey()) < x || isEqual(Math.min(l1p1.getKey(), l1p2.getKey()), x))
                    && (Math.max(l1p1.getKey(), l1p2.getKey()) > x || isEqual(Math.max(l1p1.getKey(), l1p2.getKey()), x))
                    && (Math.min(l1p1.getValue(), l1p2.getValue()) < y || isEqual(Math.min(l1p1.getValue(), l1p2.getValue()), y))
                    && (Math.max(l1p1.getValue(), l1p2.getValue()) > y || isEqual(Math.max(l1p1.getValue(), l1p2.getValue()), y))
            );
            boolean online2 = ((Math.min(l2p1.getKey(), l2p2.getKey()) <= x || isEqual(Math.min(l2p1.getKey(), l2p2.getKey()), x))
                    && (Math.max(l2p1.getKey(), l2p2.getKey()) > x || isEqual(Math.max(l2p1.getKey(), l2p2.getKey()), x))
                    && (Math.min(l2p1.getValue(), l2p2.getValue()) < y || isEqual(Math.min(l2p1.getValue(), l2p2.getValue()), y))
                    && (Math.max(l2p1.getValue(), l2p2.getValue()) > y || isEqual(Math.max(l2p1.getValue(), l2p2.getValue()), y))
            );

            if(online1 && online2)
                return new AbstractMap.SimpleEntry<>(x, y);
        }
        return null; //intersection is at out of at least one segment.
    }

    static List<Map.Entry<Double, Double>> getIntersectionPoints(Map.Entry<Double, Double> p1, Map.Entry<Double, Double> p2, List<Map.Entry<Double, Double>> poly){
        //if line intersects polygon in corner point, function adds this point twice,
        //because two edges of polygon contain this point

        //Set is used to predict adding equal points
        List<Map.Entry<Double, Double>> intersectionPoints = new ArrayList<>();

        for(int i = 0; i < poly.size(); i++){

            int next = (i + 1 == poly.size()) ? 0 : i + 1;

            Map.Entry<Double, Double> ip = getIntersectionPoint(p1, p2, poly.get(i), poly.get(next));

            if(ip != null) intersectionPoints.add(ip);

        }

        return intersectionPoints;
    }
}
