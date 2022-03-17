package bfst20.logic;

import bfst20.logic.entities.*;
import bfst20.logic.misc.OSMElement;
import bfst20.logic.misc.OSMType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static javax.xml.stream.XMLStreamConstants.*;

public class Parser {
    private List<Relation> tempOSMRelations;
    private static boolean isLoaded = false;
    private AppController appController;
    private static Parser parser;

    private Parser() {
        appController = new AppController();
        tempOSMRelations = new ArrayList<>();
    }

    public static Parser getInstance() {
        if (!isLoaded) {
            isLoaded = true;
            parser = new Parser();
        }
        return parser;
    }

    public void parseOSMFile(File file) throws IOException, XMLStreamException {
        parse(XMLInputFactory.newFactory().createXMLStreamReader(new FileReader(file, Charset.forName("UTF-8"))));
        tempOSMRelations = new ArrayList<>();
        System.gc();
    }

    public void parseString(String string) throws XMLStreamException {
        Reader stringReader = new StringReader(string);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(stringReader);
        parse(reader);
    }

    private void parse(XMLStreamReader reader) throws XMLStreamException {
        OSMElement lastElementParsed = null;
        HashMap<String, String> tags = null;
        String[] firstTag = new String[2];

        long lastNodeId = 0;
        float lat = 0;
        float lon = 0;

        while (reader.hasNext()) {
            reader.next();

            switch (reader.getEventType()) {
                case START_ELEMENT:
                    String tagName = reader.getLocalName().intern();

                    switch (tagName) {
                        case "bounds":
                            setBounds(reader);
                            break;
                        case "node":
                            lastNodeId = Long.parseLong(reader.getAttributeValue(null, "id"));
                            lon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            lat = -Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            addNodeToMap(reader);
                            tags = new HashMap<>();
                            break;
                        case "way":
                            lastElementParsed = addWayToList(reader);
                            tags = new HashMap<>();
                            break;
                        case "nd":
                            if (lastElementParsed instanceof Way) {
                                addSubElementToWay(reader, (Way) lastElementParsed);
                            }
                            break;
                        case "relation":
                            lastElementParsed = addRelationToList(reader);
                            tags = new HashMap<>();
                            break;
                        case "tag":
                            if (tags == null)
                                break;

                            String key = reader.getAttributeValue(null, "k");
                            String value = reader.getAttributeValue(null, "v");

                            if (firstTag[0] == null) {
                                firstTag[0] = key;
                                firstTag[1] = value;
                            }
                            tags.put(key.intern(), value.intern());
                            break;
                        case "member":
                            addMemberToRelation(reader);
                            break;
                    }
                    break;
                case END_ELEMENT:
                    tagName = reader.getLocalName();

                    switch (tagName) {
                        case "node":
                            parseAddressTags(lastNodeId, lon, lat, tags);
                            break;
                        case "relation":
                            Relation relation = (Relation) lastElementParsed;
                            appController.saveRelationData(relation);
                            parseTags(lastElementParsed, tags, firstTag);
                            break;
                        case "way":
                            parseTags(lastElementParsed, tags, firstTag);
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

    }

    private void setBounds(XMLStreamReader reader) {
        float minLat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
        float maxLon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
        float maxLat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
        float minLon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));

        appController.saveBoundsData(new Bounds(maxLat, minLat, maxLon, minLon));
    }


    private void addNodeToMap(XMLStreamReader reader) {
        long id = Long.parseLong(reader.getAttributeValue(null, "id"));
        float latitude = -Float.parseFloat(reader.getAttributeValue(null, "lat"));
        float longitude = Float.parseFloat(reader.getAttributeValue(null, "lon")) * 0.56f;

        Node node = new Node(id, latitude, longitude);
        appController.saveNodeData(node.getId(), node);

    }

    private Way addWayToList(XMLStreamReader reader) {
        long id = Long.parseLong(reader.getAttributeValue(null, "id"));
        Way way = new Way(id);

        appController.saveWayData(way);

        return way;
    }

    private void addSubElementToWay(XMLStreamReader reader, Way lastWay) {
        long id = Long.parseLong(reader.getAttributeValue(null, "ref"));

        lastWay.addNodeId(id);

    }

    // 1. Adding relation to temp
    // 2. Adding sub elements to the temp relation
    // 3. Adding final relation to the real realtions list.
    // Why: to have all sub elements in the final relations.
    private Relation addRelationToList(XMLStreamReader reader) {
        long id = Long.parseLong(reader.getAttributeValue(null, "id"));
        Relation relation = new Relation(id);
        tempOSMRelations.add(relation);

        return relation;
    }


    private void addMemberToRelation(XMLStreamReader reader) {
        Relation relation = tempOSMRelations.get(tempOSMRelations.size() - 1);
        long member = Long.parseLong(reader.getAttributeValue(null, "ref"));
        String type = reader.getAttributeValue(null, "type").intern();
        relation.addMember(member, type);
    }


    private void parseAddressTags(long lastNodeId, float lon, float lat, HashMap<String, String> tags) {
        if (tags.size() == 0) return;

        String city = tags.get("addr:city");
        String housenumber = tags.get("addr:housenumber");
        String postcode = tags.get("addr:postcode");
        String street = tags.get("addr:street");

        if (city == null) return;
        if (housenumber == null) return;
        if (postcode == null) return;
        if (street == null) return;

        Address address = new Address(
                city.intern(),
                housenumber.equals("") ? "" : housenumber.intern(),
                postcode.equals("") ? "" : postcode.intern(),
                street.equals("") ? "" : street.intern(),
                lat, lon, lastNodeId);
        appController.saveAddressData(lastNodeId, address);
    }

    private void parseTags(OSMElement lastElementParsed, HashMap<String, String> tags, String[] firstTag) {
        try {
            if(tags.containsKey("route")) return;

            if (tags.containsKey("name")) lastElementParsed.setName(tags.get("name").intern());

            if (tags.containsKey("type") && tags.get("type").equals("multipolygon")) {
                lastElementParsed.setMultipolygon(true);
            }

            if (tags.containsKey("landuse") || tags.containsKey("natural")) {
                if (tags.containsKey("natural")) {
                    lastElementParsed.setOSMType(OSMType.valueOf(tags.get("natural").toUpperCase()));
                } else {
                    OSMType type = OSMType.LANDUSE;

                    try {
                        type = OSMType.valueOf(tags.get("landuse").toUpperCase());
                    } catch (Exception e) {
                        // This exception is getting throwen a lot, because of all the missing Enum
                    }

                    lastElementParsed.setOSMType(type);
                }
            } else if (tags.containsKey("building")){
                lastElementParsed.setOSMType(OSMType.BUILDING);
            } else if (tags.containsKey("highway")) {
                parseHighway(lastElementParsed, tags);
            } else {
                lastElementParsed.setOSMType(OSMType.valueOf(firstTag[0].toUpperCase()));
            }

        } catch (Exception err) {
            // This exception is getting throwen a lot, because of all the missing Enum
            // Types.
        }
    }

    private void parseHighway(OSMElement lastElementParsed, HashMap<String, String> tags) {
        Way way = (Way) lastElementParsed;

        // Need source and target for graph edges
        for (long id : way.getNodeIds()) {
            Node node = appController.fetchNodeData(id);
            way.addNode(node);
        }

        if (tags.containsKey("maxspeed")) way.setMaxSpeed(Integer.parseInt(tags.get("maxspeed")));

        if (tags.containsKey("oneway")) {
            if (tags.get("oneway").equals("yes")) way.setOneWay(true);

            else way.setOneWay(false);
        }
        OSMType type = OSMType.HIGHWAY;

        type = setHighwayType(tags, type);
        lastElementParsed.setOSMType(type);
    }

    private OSMType setHighwayType(HashMap<String, String> tags, OSMType type) {
        try {
            type = OSMType.valueOf(tags.get("highway").toUpperCase());

            if (type == OSMType.RESIDENTIAL) type = OSMType.RESIDENTIAL_HIGHWAY;

            else if (type == OSMType.UNCLASSIFIED) type = OSMType.UNCLASSIFIED_HIGHWAY;

        } catch (Exception e) {
        }
        return type;
    }
}
