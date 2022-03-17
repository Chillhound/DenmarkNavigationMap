package bfst20.logic.kdtree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bfst20.logic.misc.OSMType;
import bfst20.logic.entities.LinePath;
import javafx.geometry.Point2D;

public class KDTree implements Serializable {
    private float closetNodeDistance;
    private KDNode closetsNode;
    private KDNode root;


    public KDTree(List<LinePath> paths, Rect rect) {
        root = new KDNode();
        root.setLinePath(paths.get(0));
        root.setDirection(Direction.Latitudinal);
        root.setSplit(rect.getMinLat() + (rect.getMaxLat() - rect.getMinLat()) / 2);

        for (int i = 1; i < paths.size(); i++) {
            LinePath path = paths.get(i);

            insert(root, path);
        }

        System.gc();
    }

    public KDNode getRoot() {
        return root;
    }

    //With mouse to node distance calculation
    public Iterable<LinePath> getElementsInRect(Rect rect, double zoomLevel, Point2D point) {
        closetsNode = root;
        closetNodeDistance = Float.POSITIVE_INFINITY;

        List<LinePath> list = new ArrayList<>();
        range(root, rect, list, zoomLevel, point);
        return list;
    }

    private void range(KDNode node, Rect rect, List<LinePath> list, double zoomLevel, Point2D point) {
        if (node == null) return;

        if (rect.contains(node) && OSMType.getZoomLevel(node.getLinePath().getOSMType()) <= zoomLevel) {
            list.add(node.getLinePath());

            if(point != null){
                float[] coords = node.getLinePath().getCoords();

                for (int i = 2; i <= coords.length; i += 2) {

                    float distance = (float) Math.sqrt(Math.pow(point.getY() - coords[i - 1], 2) + Math.pow(point.getX() - coords[i - 2], 2));

                    if (distance < closetNodeDistance) {
                        closetNodeDistance = distance;
                        closetsNode = node;
                    }
                }
            }
        }

        if (rect.intersectsRight(node) && OSMType.getZoomLevel(node.getLinePath().getOSMType()) <= zoomLevel) {
            range(node.getRightNode(), rect, list, zoomLevel, point);
        }

        if (rect.intersectsLeft(node) && OSMType.getZoomLevel(node.getLinePath().getOSMType()) <= zoomLevel) {
            range(node.getLeftNode(), rect, list, zoomLevel, point);
        }
    }

    public LinePath getClosetsLinepathToMouse() {
        return closetsNode.getLinePath();
    }
    public double getClosetsLinePathToMouseDistance(){return closetNodeDistance;}

    private KDNode createNewKdNode(LinePath path, Direction direction) {
        KDNode node = new KDNode();
        node.setDirection(direction);
        if (direction == Direction.Latitudinal) {
            node.setSplit(path.getCenterLatitude());
        } else {
            node.setSplit(path.getCenterLongitude());
        }
        path.removeWay();
        node.setLinePath(path);

        return node;
    }

    private void insertNode(KDNode node, LinePath path) {
        //Both nodes are empty
        if (node.getDirection() == Direction.Latitudinal) {
            KDNode newNode = createNewKdNode(path, Direction.Longitudinal);
            if (node.getSplit() > path.getCenterLatitude()) {
                //Since it is less it would be to the left of the split line for the node.
                node.setLeftNode(newNode);
            } else {
                node.setRightNode(newNode);
            }
        } else {
            KDNode newNode = createNewKdNode(path, Direction.Latitudinal);
            if (node.getSplit() > path.getCenterLongitude()) {
                //Since it is less it would be to the left of the split line for the node.
                node.setRightNode(newNode);
            } else {
                node.setLeftNode(newNode);
            }
        }
    }

    private void insertNodeLeftExists(KDNode node, LinePath path) {
        //Left node is there but no right node
        if (node.getDirection() == Direction.Latitudinal) {
            KDNode newNode = createNewKdNode(path, Direction.Longitudinal);
            if (node.getSplit() > path.getCenterLatitude()) {
                //Since it is less it would be to the left of the split line for the node.
                //node.setLeftNode(newNode);
                insert(node.getLeftNode(), path); // Since left node exists
            } else {
                node.setRightNode(newNode);
            }
        } else {
            KDNode newNode = createNewKdNode(path, Direction.Latitudinal);

            if (node.getSplit() > path.getCenterLongitude()) {
                //Since it is less it would be to the left of the split line for the node.
                node.setRightNode(newNode);
            } else {
                //node.setLeftNode(newNode);
                insert(node.getLeftNode(), path); // Since left node exists
            }
        }
    }

    private void insertNodeRightExists(KDNode node, LinePath path) {
        //Right node is there but no left node.
        if (node.getDirection() == Direction.Latitudinal) {
            KDNode newNode = createNewKdNode(path, Direction.Longitudinal);
            if (node.getSplit() > path.getCenterLatitude()) {
                //Since it is less it would be to the left of the split line for the node.
                node.setLeftNode(newNode);
            } else {
                //node.setRightNode(newNode);
                insert(node.getRightNode(), path); // Since left node exists

            }
        } else {
            KDNode newNode = createNewKdNode(path, Direction.Latitudinal);
            if (node.getSplit() > path.getCenterLongitude()) {
                //Since it is less it would be to the left of the split line for the node.
                //node.setRightNode(newNode);
                insert(node.getRightNode(), path); // Since left node exists
            } else {
                node.setLeftNode(newNode);
            }
        }
    }

    //Going down the one of the children of the parent  node.
    private void insertNodeBothExists(KDNode node, LinePath path) {
        //Both nodes are there.
        if (node.getDirection() == Direction.Latitudinal) {
            KDNode newNode = createNewKdNode(path, Direction.Longitudinal);
            if (node.getSplit() > path.getCenterLatitude()) {
                //Since it is less it would be to the left of the split line for the node.
                //node.setLeftNode(newNode);
                insert(node.getLeftNode(), path); // Since left node exists
            } else {
                insert(node.getRightNode(), path); // Since left node exists
            }
        } else {
            KDNode newNode = createNewKdNode(path, Direction.Latitudinal);
            if (node.getSplit() > path.getCenterLongitude()) {
                //Since it is less it would be to the left of the split line for the node.
                //node.setRightNode(newNode);
                insert(node.getRightNode(), path); // Since left node exists
            } else {
                //node.setLeftNode(newNode);
                insert(node.getLeftNode(), path); // Since left node exists
            }
        }
    }

    //Determines what method to call.
    private void insert(KDNode node, LinePath path) {
        if (node.getLeftNode() == null && node.getRightNode() == null) {
            insertNode(node, path);
        } else if (node.getLeftNode() != null && node.getRightNode() == null) {
            insertNodeLeftExists(node, path);
        } else if (node.getRightNode() != null && node.getLeftNode() == null) {
            insertNodeRightExists(node, path);
        } else if (node.getLeftNode() != null && node.getRightNode() != null) {
            insertNodeBothExists(node, path);
        }
    }


}
