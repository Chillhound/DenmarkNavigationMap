package bfst20.presentation;

import bfst20.data.InterestPointData;
import bfst20.logic.AppController;
import bfst20.logic.misc.OSMType;
import bfst20.logic.entities.*;
import bfst20.logic.kdtree.Rect;
import bfst20.logic.misc.Vehicle;
import bfst20.logic.routing.Edge;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;


import java.util.*;
import java.util.List;

import javafx.scene.transform.NonInvertibleTransformException;


public class View {
    private Map<OSMType, List<LinePath>> linePaths;
    private boolean isColorBlindMode = false;
    private Affine trans = new Affine();
    private AppController appController;
    private List<LinePath> coastlines;
    private List<LinePath> motorways;
    private Address searchAddress;
    private GraphicsContext gc;
    private Point2D mousePos;
    private Canvas canvas;
    List<Edge> route = null;

    Label mouseLocationLabel;

    double zoomLevel = 1.0;
    double timesZoomed = 0.0;
    double sliderValue = 0;

    public View(Canvas canvas) {
        mousePos = new Point2D(0, 0);
        appController = new AppController();
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public void initialize(boolean isBinary) {
        trans = new Affine();
        linePaths = appController.fetchLinePathData();
        coastlines = appController.fetchCoastlines();

        if (!isBinary) {
            appController.clearLinePathData();
            createKDTrees();
        }

        System.gc();

        Bounds bounds = appController.fetchBoundsData();

        float minLon = bounds.getMinLon();
        float maxLon = bounds.getMaxLon();
        float minLat = bounds.getMinLat();
        float maxLat = bounds.getMaxLat();

        pan(-minLon, -minLat);
        zoom(canvas.getHeight() / (maxLon - minLon), (minLat - maxLat) / 2, 0, 1);

        repaint();
    }

    public void setMousePos(Point2D mousePos) {
        this.mousePos = mousePos;
    }

    private void createKDTrees() {
        appController.setupRect();

        for (Map.Entry<OSMType, List<LinePath>> entry : linePaths.entrySet()){

            if (entry.getKey() != OSMType.COASTLINE) {
                if (entry.getValue().size() != 0) {
                    appController.saveKDTree(entry.getKey(), entry.getValue());
                }
            }
        }

        //List<LinePath> highways = appController.getHighwaysFromModel();

        //appController.addKDTreeToModel(OSMType.HIGHWAY, highways);

        motorways = appController.fetchMotorways();

        if(motorways.size() > 0){
            appController.saveKDTree(OSMType.MOTORWAY, motorways);
        }

        appController.saveKDTree(OSMType.COASTLINE, linePaths.get(OSMType.COASTLINE));

        linePaths = null;

        repaint();
    }

    private long lastTime = 0;

    private boolean fps() {
        Date date = new Date();

        if (lastTime == 0) {
            lastTime = date.getTime();
            return false;
        } else {
            if ((date.getTime() - 30) < lastTime) {
                return true;
            }
        }

        lastTime = date.getTime();

        return false;
    }

    double pixelwidth;

    public void repaint() {

        if (fps()) return;


        gc.setTransform(new Affine());
        gc.setFill(OSMType.getColor(OSMType.OCEAN, isColorBlindMode));

        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setTransform(trans);

        pixelwidth = 1 / Math.sqrt(Math.abs(trans.determinant()));

        int boxSize = (int) canvas.getWidth() + 50;

        Rect rect = createRect(boxSize);

        Point2D mouse = convertCoordinates(
                mousePos.getX(),
                mousePos.getY());


        //drawTypeKdTree(OSMType.COASTLINE, rect, pixelwidth);

        for (LinePath path : coastlines) {
            drawLinePath(path, pixelwidth);
        }

        drawAllKDTreeTypes(rect, mouse);

        setClosetLinePathToMouse();

        Point2D mc1 = convertCoordinates((canvas.getWidth() / 2) - boxSize, (canvas.getHeight() / 2) - boxSize);
        Point2D mc2 = convertCoordinates((canvas.getWidth() / 2) + boxSize, (canvas.getHeight() / 2) + boxSize);

        //gc.setStroke(Color.PURPLE);
        //gc.strokeRect(mouse.getX(), mouse.getY(), 0.001, 0.001);
        gc.beginPath();
        gc.setStroke(Color.BLUE);
        gc.strokeRect(mc1.getX(), mc1.getY(), mc2.getX() - mc1.getX(), mc2.getY() - mc1.getY());
        gc.stroke();

        drawSearchLocation(searchAddress, pixelwidth);
        drawInterestPoints(pixelwidth);


        if (route != null) {

            drawPointer(pixelwidth, 30, route.get(0).getTarget().getLongitude(), route.get(0).getTarget().getLatitude(), "1");
            drawPointer(pixelwidth, 30, route.get(route.size()-1).getSource().getLongitude(), route.get(route.size()-1).getSource().getLatitude(), "2");


            for (Edge edge : route) {
                drawRoute(edge, pixelwidth);
            }
        }

        //System.gc();
    }



    private void drawKDTree(OSMType type, Rect rect, double lineWidth, Point2D point) {
        if(appController.fetchKDTree(type) != null){
            for (LinePath linePath : appController.fetchKDTree(type).getElementsInRect(rect, trans.determinant(), point)) {

                drawLinePath(linePath, lineWidth);
                gc.fill();
            }
        }
    }


    private Rect createRect(int boxSize) {
        Point2D mc1 = convertCoordinates((canvas.getWidth() / 2) - boxSize, (canvas.getHeight() / 2) - boxSize);
        Point2D mc2 = convertCoordinates((canvas.getWidth() / 2) + boxSize, (canvas.getHeight() / 2) + boxSize);
        return new Rect((float) mc1.getY(), (float) mc2.getY(), (float) mc1.getX(), (float) mc2.getX());
    }

    private void drawInterestPoints(double lineWidth) {
        InterestPointData interestPointData = InterestPointData.getInstance();

        int i = 0;

        for (InterestPoint interestPoint : interestPointData.getAllInterestPoints()) {
            int bubbleSize = 30;

            drawPointer(lineWidth, bubbleSize, interestPoint.getLongitude(), interestPoint.getLatitude(), String.valueOf(i));
            i++;
        }
    }

    public void shortestPath(String sourceQuery, String targetQuery, Vehicle vehicle) {
        double distance = appController.initializeRouting(sourceQuery, targetQuery, vehicle);

        route = appController.fetchRouteData();

        repaint();
    }

    public void setSearchAddress(Address address) {
        this.searchAddress = address;

        repaint();
    }

    public void drawSearchLocation(Address address, double lineWidth) {
        if (address == null) return;

        int bubbleSize = 30;

        drawPointer(lineWidth, bubbleSize, address.getLon(), address.getLat(), "1");

    }

    private void drawPointer(double lineWidth, int bubbleSize, float lon, float lat, String id) {
        gc.beginPath();
        gc.setStroke(Color.RED);
        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", lineWidth * 20));
        gc.fillText(id, lon - (lineWidth * bubbleSize / 2) + (lineWidth * bubbleSize / 3), lat - (lineWidth * bubbleSize * 1.4) + (lineWidth * bubbleSize / 1.5));
        gc.fill();
        gc.strokeOval(lon - (lineWidth * bubbleSize / 2), lat - (lineWidth * bubbleSize * 1.4), lineWidth * bubbleSize, lineWidth * bubbleSize);
        gc.moveTo(lon - (lineWidth * bubbleSize / 2), lat - (lineWidth * bubbleSize));
        gc.lineTo(lon, lat);
        gc.moveTo(lon + (lineWidth * bubbleSize / 2), lat - (lineWidth * bubbleSize));
        gc.lineTo(lon, lat);
        gc.stroke();

    }

    private void drawRoute(Edge edge, double lineWidth) {
        gc.setLineWidth(lineWidth);
        gc.beginPath();
        gc.setStroke(OSMType.getColor(OSMType.ROUTING, false));


        traceEdge(edge, gc);
        gc.stroke();
    }

    private void traceEdge(Edge edge, GraphicsContext gc) {
        Node sourceNode = edge.getSource();
        Node targetNode = edge.getTarget();

        float[] coords = new float[]{sourceNode.getLongitude(), sourceNode.getLatitude(), targetNode.getLongitude(), targetNode.getLatitude()};
        gc.setStroke(OSMType.getColor(OSMType.ROUTING, false));
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2; i <= coords.length; i += 2) {
            gc.lineTo(coords[i - 2], coords[i - 1]);
        }
    }


    private void drawLinePath(LinePath linePath, double lineWidth) {

        OSMType OSMType = linePath.getOSMType();
        gc.setStroke(OSMType.getColor(OSMType, isColorBlindMode));
        gc.setLineWidth(OSMType.getLineWidth(OSMType, lineWidth));
        gc.setFill(linePath.getFill() ? OSMType.getColor(OSMType, isColorBlindMode) : Color.TRANSPARENT);


        if (linePath.isMultipolygon()) {
            traceMultipolygon(linePath, gc);
        } else {
            trace(linePath, gc);
        }
    }

    private void traceMultipolygon(LinePath linePath, GraphicsContext gc) {
        gc.beginPath();
        gc.setFillRule(FillRule.EVEN_ODD);
        float[] coords = linePath.getCoords();
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2; i <= coords.length; i += 2) {
            gc.lineTo(coords[i - 2], coords[i - 1]);
        }
        gc.stroke();

        if (OSMType.getFill(linePath.getOSMType())) {
            gc.fill();
        }
    }


    private void trace(LinePath linePath, GraphicsContext gc) {
        gc.beginPath();
        draw(linePath, gc);

        gc.stroke();

        if (OSMType.getFill(linePath.getOSMType())) {
            gc.fill();
        }
    }

    private void draw(LinePath linePath, GraphicsContext gc) {
        float[] coords = linePath.getCoords();
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2; i <= coords.length; i += 2) {
            gc.lineTo(coords[i - 2], coords[i - 1]);
        }

    }

    //Converts raw coordinates to canvas coordinates.
    public Point2D convertCoordinates(double x, double y) {
        try {
            return trans.inverseTransform(x, y);
        } catch (NonInvertibleTransformException e) {
            //It is not possible for java to throw this exeption, but the try/catch is needed anyways.
            // Troels siger at det her ikke kan ske
            return null;
        }
    }

    public void zoom(double factor, double x, double y, double deltaY) {

        scale(factor, x, y, deltaY);
        reduceZoomLevel();
        reduceTimesZoomed();
    }


    private void scale(double factor, double x, double y, double deltaY) {
        trans.prependScale(factor, factor, x, y);
        timesZoomed += deltaY / 40;
        repaint();
    }

    private void reduceZoomLevel() {
        if (zoomLevel > 2500) {
            zoomLevel = zoomLevel / 2517.0648374271736;
        }
    }

    private void reduceTimesZoomed() {
        if (timesZoomed > 126) {
            timesZoomed = 126;
        }
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void displayError(Alert.AlertType type, String text, boolean wait) {
        Alert alert = new Alert(type, text, ButtonType.OK);

        if(wait){
            alert.showAndWait();
        }else{
            alert.show();
        }
    }

    public void changeToColorBlindMode(boolean isColorBlindMode) {
        this.isColorBlindMode = isColorBlindMode;
    }

    public void setMouseLocationView(Label mouseLocationLabel) {
        this.mouseLocationLabel = mouseLocationLabel;
    }


    public double getTimesZoomed() {
        return timesZoomed;
    }

    public void setSliderValue(double value) {
        sliderValue = value;
    }

    public double getSliderValue() {
        return sliderValue;
    }

    //Order of KDTree drawing is important.
    private void drawAllKDTreeTypes(Rect rect, Point2D mouse){

        OSMType[] drawableTypes = OSMType.drawables();

        for(OSMType type : drawableTypes){
            drawKDTree(type, rect, pixelwidth, null);
        }

        OSMType[] highwayTypes = OSMType.highways();

        for(OSMType type : highwayTypes){
            drawKDTree(type, rect, pixelwidth, mouse);
        }

    }

    private void setClosetLinePathToMouse() {
        try {

            OSMType[] types = OSMType.highways();

            Map<OSMType, Double> dist = new HashMap<>();

            for(OSMType type : types){
                if(appController.fetchAllKDTrees().get(type) != null){
                    dist.put(type, appController.fetchKDTree(type).getClosetsLinePathToMouseDistance());
                }
            }

            double shortestDistance = Double.POSITIVE_INFINITY;
            OSMType shortestType = null;

            for(Map.Entry<OSMType, Double> entry : dist.entrySet()){
                if(entry.getValue() < shortestDistance){
                    shortestDistance = entry.getValue();
                    shortestType = entry.getKey();
                }
            }

            String name = appController.fetchKDTree(shortestType).getClosetsLinepathToMouse().getName();
            mouseLocationLabel.setText(name == null ? "Unknown way" : name);
        } catch (Exception e) {
        }
    }
}