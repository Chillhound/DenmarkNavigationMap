package bfst20.logic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bfst20.data.*;
import bfst20.logic.entities.*;
import bfst20.logic.kdtree.KDTree;
import bfst20.logic.kdtree.Rect;
import bfst20.logic.entities.LinePath;
import bfst20.logic.misc.OSMType;
import bfst20.logic.misc.Vehicle;
import bfst20.logic.routing.Edge;
import bfst20.logic.routing.Graph;
import bfst20.logic.routing.RoutingController;
import bfst20.logic.ternary.TST;
import bfst20.presentation.AlertHandler;
import bfst20.presentation.View;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javax.xml.stream.XMLStreamException;

public class    AppController {

    private RoutingController routingController;
    private LinePathGenerator linePathGenerator;
    private OSMElementData osmElementData;
    private LinePathData linePathData;
    private boolean isBinary = false;
    private RoutingData routingData;
    private AddressData addressData;
    private KDTreeData kdTreeData;
    private Parser parser;
    private View view;

    public AppController() {
        osmElementData = OSMElementData.getInstance();
        linePathData = LinePathData.getInstance();
        routingData = RoutingData.getInstance();
        addressData = AddressData.getInstance();
        kdTreeData = KDTreeData.getInstance();
        parser = Parser.getInstance();
    }

    public void initialize(View view, File file) {
        this.view = view;
        loadFile(file);
        routingController = new RoutingController(this);
        linePathGenerator = LinePathGenerator.getInstance(this);
        if (!isBinary) {
            linePathGenerator.convertWaysToLinePaths(fetchAllWays(), fetchAllNodes());
            linePathGenerator.convertRelationsToLinePaths(fetchRelations());
            linePathGenerator.clearData();
            clearNodeData();
            generateHighways();
            routingController.buildRoutingGraph();
        }
        view.initialize(isBinary);
        System.gc();
    }

    public void loadFile(File file) {
        linePathData.clearMotorways();
        linePathData.clearCoastlines();
        kdTreeData.clearData();
        clearLinePathData();
        addressData.clearData();
        InterestPointData interestPointData = InterestPointData.getInstance();
        interestPointData.clearData();
        try {
            if (file.getName().endsWith(".bin")){
                isBinary = true;
            }else{
                isBinary = false;
            }
            FileHandler.load(file, this);
        } catch (IOException ioException) {
            alertOK(Alert.AlertType.ERROR, "Invalid xml data, exiting.", true);
            System.exit(1);
        } catch (XMLStreamException xmlStreamException) {
            alertOK(Alert.AlertType.ERROR, "Invalid xml data, exiting.", true);
            System.exit(1);
        } catch (NullPointerException exception){
            alertOK(Alert.AlertType.ERROR, "Error finding file, exiting.", true);
            System.exit(1);
        }
    }

    public void generateHighways() {
        Map<OSMType, List<LinePath>> linePaths = linePathData.getLinePaths();
        List<LinePath> highWays = new ArrayList<>();

        if (linePathData.getMotorways() != null) highWays.addAll(linePathData.getMotorways());
        for (Map.Entry<OSMType, List<LinePath>> entry : linePaths.entrySet()) {
            highWays.addAll(entry.getValue());
        }

        linePathData.saveHighways(highWays);
    }

    public Map<OSMType, KDTree> fetchAllKDTrees() {
        return kdTreeData.getAllLKDTrees();
    }

    public void saveAllKDTrees(Map<OSMType, KDTree> tree) {
        kdTreeData.saveAllKDTrees(tree);
    }

    public List<LinePath> fetchHighways() {
        return linePathData.getHighways();
    }

    public double initializeRouting(String sourceQuery, String targetQuery, Vehicle vehicle) {
        routingController = new RoutingController(new AppController());

        Address source = addressData.findAddress(sourceQuery);
        Address target = addressData.findAddress(targetQuery);

        Graph graph = fetchGraphData();
        List<Edge> edges = graph.getEdges();

        return routingController.calculateShortestRoute(graph, edges, source, target, vehicle);
    }

    public Graph fetchGraphData() {
        return routingData.getGraph();
    }

    public void saveGraphData(Graph graph) {
        routingData.saveGraph(graph);
    }

    public List<Edge> fetchRouteData() {
        return routingData.getRoute();
    }

    public void saveRouteData(List<Edge> route) {
        routingData.saveRoute(route);
    }

    public void saveRouteDirections(Map<String, Double> routeInfo) {
        routingData.saveRouteDirections(routeInfo);
    }

    public Map<String, Double> fetchRouteDirections() {
        return routingData.getRouteDirections();
    }

    public void clearRouteInfoData() {
        routingData.clearData();
    }

    public void setSearchString(Address address) {
        view.setSearchAddress(address);
    }

    public void parseOSM(File file) throws IOException, XMLStreamException {
        parser.parseOSMFile(file);
    }

    public void parseString(String string) throws XMLStreamException {
        parser.parseString(string);
    }

    public void saveAddressData(long id, Address address) {
        addressData.saveAddress(id, address);
    }

    public void saveRelationData(Relation relation) {
        osmElementData.saveRelation(relation);
    }

    public List<Relation> fetchRelations() {
        return osmElementData.getRelations();
    }

    public void saveBoundsData(Bounds bounds) {
        osmElementData.saveBounds(bounds);
    }

    public Bounds fetchBoundsData() {
        return osmElementData.getBounds();
    }

    public void saveNodeData(long id, Node node) {
        osmElementData.addToNodeMap(id, node);
    }

    public void saveWayData(Way way) {
        osmElementData.saveWay(way);
    }

    public List<Way> fetchAllWays() {
        return osmElementData.getWays();
    }

    public Node fetchNodeData(long id) {
        return osmElementData.getNode(id);
    }

    public Map<Long, Node> fetchAllNodes() {
        return osmElementData.getNodes();
    }

    public List<LinePath> fetchCoastlines() {
        return linePathData.getCoastlines();
    }

    public void saveCoastlines(List<LinePath> paths) {
        linePathData.saveCoastlines(paths);
    }

    public void clearNodeData() {
        OSMElementData.getInstance().clearNodeData();
    }

    public Map<OSMType, List<LinePath>> fetchLinePathData() {
        return linePathData.getLinePaths();
    }

    public Way removeWayFromNodeTo(OSMType type, Node node) {
        return linePathData.removeWayFromNodeTo(type, node);
    }

    public void saveNodeToData(OSMType type, Node node, Way way) {
        linePathData.addNodeTo(type, node, way);
    }

    public Map<Node, Way> getNodeTo(OSMType type) {
        return linePathData.getNodeTo(type);
    }

    public void saveLinePathData(OSMType type, LinePath linePath) {
        if (type == OSMType.COASTLINE) linePathData.saveSingleCoastLine(linePath);

        else linePathData.saveLinePath(type, linePath);
    }

    public List<LinePath> fetchMotorways() {
        return linePathData.getMotorways();
    }

    public void clearLinePathData() {
        LinePathGenerator.getInstance(this).clearData();
        linePathData.clearData();
    }

    public void setupRect() {
        Bounds bounds = fetchBoundsData();

        kdTreeData.saveRectValues(bounds.getMinLat(), bounds.getMaxLat(), bounds.getMinLon(), bounds.getMaxLon());
    }

    public Rect fetchRectData() {
        return kdTreeData.getRect();
    }

    public void saveKDTree(OSMType type, List<LinePath> linePaths) {
        if (type == OSMType.COASTLINE) return;
        kdTreeData.saveKDTree(type, new KDTree(linePaths, fetchRectData()));
    }

    public KDTree fetchKDTree(OSMType OSMType) {
        return kdTreeData.getKDTree(OSMType);
    }


    public void alertOK(Alert.AlertType type, String text, boolean wait) {
        AlertHandler.alertOK(type, text, wait);
    }

    public void generateBinary() throws IOException {
        clearAllNonBinData();
        try {
            FileHandler.generateBinary();
        } catch (Exception e) {
            alertOK(Alert.AlertType.ERROR, "Error generating binary, please retry.", false);
        }
    }

    public void clearAllNonBinData() {
        clearNodeData();
        clearLinePathData();
        routingData.clearData();
    }

    public TST fetchTSTData() {
        return addressData.getTST();
    }

    public void saveTSTData(TST tst) {
        addressData.saveTST(tst);
    }

}