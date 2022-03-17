package bfst20.data;

import bfst20.logic.misc.OSMType;
import bfst20.logic.kdtree.KDTree;
import bfst20.logic.kdtree.Rect;

import java.util.HashMap;
import java.util.Map;

public class KDTreeData {

    private static boolean isLoaded = false;
    private static KDTreeData kdTreeData;
    private Map<OSMType, KDTree> kdTrees;
    private Rect rect;

    private KDTreeData() {
        kdTrees = new HashMap<>();
        rect = new Rect();
    }

    public static KDTreeData getInstance() {
        if (!isLoaded) {
            isLoaded = true;
            kdTreeData = new KDTreeData();
        }

        return kdTreeData;
    }

    public Rect getRect() {
        return this.rect;
    }

    public void saveRectValues(float minLat, float maxLat, float minLon, float maxLon) {
        rect.setMinLat(minLat);
        rect.setMaxLat(maxLat);
        rect.setMinLon(minLon);
        rect.setMaxLon(maxLon);
    }

    public void saveKDTree(OSMType OSMType, KDTree kdTree) {
        kdTrees.put(OSMType, kdTree);
    }

    public KDTree getKDTree(OSMType OSMType) {
        return kdTrees.get(OSMType);
    }

    public void clearData() {
        kdTrees = new HashMap<>();
        System.gc();
    }

    public Map<OSMType, KDTree> getAllLKDTrees() {
        return kdTrees;
    }

    public void saveAllKDTrees(Map<OSMType, KDTree> tree){
        this.kdTrees = tree;
    }

}
