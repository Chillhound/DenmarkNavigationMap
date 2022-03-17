package bfst20.logic.misc;

import javafx.scene.paint.Color;

public enum OSMType {
    UNKNOWN,
    BEACH,
    BOUNDS,
    BUILDING,
    HIGHWAY,
    MOTORWAY,
    TERTIARY,
    ROUTING,
    RESIDENTIAL_HIGHWAY,
    UNCLASSIFIED_HIGHWAY,
    SECONDARY,
    PRIMARY,
    SERVICE,
    FOOTWAY,
    CYCLEWAY,
    PATH,
    TRACK,
    UNCLASSIFIED,
    COASTLINE,
    WATER,
    GREEN,
    HEATH,
    FOREST,
    PARKING,
    NATURAL,
    FARMLAND,
    TREE_ROW,
    MEADOW,
    LANDUSE,
    RESIDENTIAL,
    WOOD,
    MULTIPOLYGON,
    OCEAN,
    PLACE;

    public static double getLineWidth(OSMType OSMType, double lineWidth) {
        switch (OSMType) {
            case TERTIARY:
            case UNCLASSIFIED_HIGHWAY:
            case HIGHWAY:
            case MOTORWAY:
            case RESIDENTIAL_HIGHWAY:
            case SECONDARY:
            case PRIMARY:
            case SERVICE:
                return lineWidth * 2;
            default:
                return lineWidth;
        }
    }

    public static double getZoomLevel(OSMType OSMType) {
        switch (OSMType) {
            case COASTLINE:
            case MOTORWAY:
            case ROUTING:
            case PRIMARY:
                return 1;
            case HEATH:
            case WATER:
            case SECONDARY:
                return 4003111.816427079;
            case TERTIARY:
            case FARMLAND:
            case TREE_ROW:
            case BEACH:
            case GREEN:
            case MEADOW:
            case RESIDENTIAL:
                return 1.1191879061812976E7;
            case UNCLASSIFIED_HIGHWAY:
            case RESIDENTIAL_HIGHWAY:
            case MULTIPOLYGON:
            case FOREST:
            case NATURAL:
            case WOOD:
            case BUILDING:
            case HIGHWAY:
                return 1.3011416847239474E9;
            case FOOTWAY:
            case PATH:
            case TRACK:
            case CYCLEWAY:
            case SERVICE:
                return 3.2693626068786936E10;
            default:
                return Double.POSITIVE_INFINITY;
        }
    }

    public static boolean getFill(OSMType OSMType) {
        switch (OSMType) {
            case NATURAL:
            case FARMLAND:
            case FOREST:
            case ROUTING:
            case WATER:
            case COASTLINE:
            case BUILDING:
            case MULTIPOLYGON:
            case RESIDENTIAL:
            case WOOD:
            case HEATH:
            case GREEN:
            case BEACH:
            case MEADOW:
                return true;
            default:
                return false;
        }
    }


    public static Color getColor(OSMType OSMType, Boolean colorBlindMode) {
        if (!colorBlindMode) return getNormalColor(OSMType);
        else return getColorBlindColor(OSMType);
    }

    public static Color getNormalColor(OSMType OSMType) {
        switch (OSMType) {
            case OCEAN:
                return Color.LIGHTBLUE;
            case BEACH:
                return Color.rgb(255, 241, 186, 1);
            case MEADOW:
                return Color.rgb(205, 235, 176, 1);
            case COASTLINE:
                return Color.rgb(242, 239, 233, 1);
            case PLACE:
            case GREEN:
                return Color.LIGHTGREEN;
            case WATER:
                return Color.rgb(170, 211, 223, 1);
            case BUILDING:
                return Color.rgb(187, 170, 157, 1);
            case MULTIPOLYGON:
                return Color.BROWN;
            case RESIDENTIAL_HIGHWAY:
                return Color.rgb(92, 92, 92, 1);
            case MOTORWAY:
                return Color.BLACK;
            case HIGHWAY:
                return Color.rgb(54, 54, 54, 1);
            case UNCLASSIFIED_HIGHWAY:
                return Color.rgb(79, 79, 79, 0.5);
            case HEATH:
                return Color.rgb(214, 217, 159, 0.9);
            case RESIDENTIAL:
                return Color.rgb(218, 218, 218, 0.9);
            case FARMLAND:
                return Color.rgb(238, 240, 213, 0.9);
            case WOOD:
            case FOREST:
                return Color.rgb(157, 202, 138, 0.9);
            case TREE_ROW:
                return Color.rgb(0, 102, 0, 0.7);
            case PARKING:
                return Color.ROYALBLUE;
            case FOOTWAY:
            case PATH:
                return Color.rgb(183, 120, 90, 1);
            case TRACK:
                return Color.rgb(186, 153, 87, 1);
            case CYCLEWAY:
                return Color.rgb(15, 0, 158, 0.5);
            case SECONDARY:
                return Color.rgb(255, 255, 100, 0.5);
            case PRIMARY:
                return Color.rgb(253, 221, 179, 0.9);
            case SERVICE:
            case TERTIARY:
                return Color.rgb(158, 156, 153, 1);
            case NATURAL:
                return Color.BLUEVIOLET;
            case ROUTING:
                return Color.RED;
            default:
                return Color.TRANSPARENT;
        }

    }

    public static Color getColorBlindColor(OSMType OSMType) {
        return getColor(OSMType, false).invert();
    }

    //TODO: FIX SPEED
    public static int getMaxSpeed(OSMType OSMType) {
        switch (OSMType) {
            case RESIDENTIAL_HIGHWAY:
                return 50;
            case MOTORWAY:
                return 110;
            case TERTIARY:
                return 80;
            case UNCLASSIFIED_HIGHWAY:
                return 70;
            case TRACK:
                return 20;
            case PRIMARY:
                return 80;
            case SECONDARY:
                return 50;
            case SERVICE:
                return 30;
            default:
                return 80;
        }
    }

    public static OSMType[] highways() {
        OSMType[] types = {
                HIGHWAY,
                RESIDENTIAL_HIGHWAY,
                TERTIARY,
                UNCLASSIFIED_HIGHWAY,
                MOTORWAY,
                FOOTWAY,
                PATH,
                TRACK,
                CYCLEWAY,
                SECONDARY,
                PRIMARY,
                SERVICE
        };

        return types;
    }

    public static OSMType[] drawables() {
        OSMType[] types = {
                BEACH,
                FARMLAND,
                RESIDENTIAL,
                HEATH,
                WOOD,
                TREE_ROW,
                FOREST,
                WATER,
                BUILDING,
                MEADOW,
        };

        return types;
    }

    public static OSMType[] relations() {
        OSMType[] types = {
                HEATH,
                MEADOW,
                BUILDING,
                FOREST,
                FARMLAND,
                COASTLINE
        };

        return types;
    }

}