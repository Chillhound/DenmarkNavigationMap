package bfst20.data;

import bfst20.logic.entities.Address;
import bfst20.logic.ternary.TST;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddressData {

    private static String addressRegex = "[,. ]*(?<street>[\\D]+)[,. ]+(?<house>[\\d][\\w]*)[,. ]*(?<postcode>[\\w]*)[,.\\V]*";
    private static AddressData addressData;
    private TST tst;

    private AddressData() {
        tst = new TST();
    }

    public static AddressData getInstance() {
        if (addressData == null) {
            addressData = new AddressData();
        }

        return addressData;
    }

    public void saveAddress(long id, Address address) {
        if(address.getStreet() == null) return;
        tst.put(address.getStreet().replaceAll(" ", ""), address);
    }

    public TST getTST(){
        return tst;
    }
    public void clearData(){
        tst = new TST();
    }


    public String[] parseAddress(String input) {
        Matcher pattern = Pattern.compile(addressRegex).matcher(input);

        if (pattern.matches() && !input.equals("") && pattern.groupCount() == 3) {
            String street = pattern.group("street");
            String house = pattern.group("house");
            String postcode = pattern.group("postcode");

            String[] address = {street, house, postcode};

            return address;
        } else {

            String[] string = {input.trim().replaceAll(" ", "")};

            return string;
        }
    }

    public Queue<Address> searchSuggestions(String input){

        String[] addressStrings = parseAddress(input);

        if(addressStrings == null) return null;

        addressStrings[0] = addressStrings[0].replaceAll(" ", "");

        Queue<Address> addresses = getTST().keysWithPrefix(addressStrings[0]);
        Queue<Address> newAddresses = new LinkedList<>();

        for(Address address : addresses){

            if(addressStrings.length == 3 && !addressStrings[1].equals("")){
                if(!address.getHousenumber().startsWith(addressStrings[1])){
                    continue;
                }
            }

            if(addressStrings.length == 3 && !addressStrings[2].equals("")){
                if(!address.getPostcode().startsWith(addressStrings[2])){
                    continue;
                }
            }

            newAddresses.add(address);
        }

        return newAddresses;
    }

    public Address findAddress(String input) {
        String[] addressStrings = parseAddress(input);
        if (addressStrings.length == 0) return null;
        addressStrings[0] = addressStrings[0].replaceAll(" ", "");

        for (Address address : tst.keysWithPrefix(addressStrings[0])) {
            if (address.getStreet() == null) continue;

            if (
                    address.getStreet().trim().toLowerCase().replaceAll(" ", "").equals(addressStrings[0].trim().toLowerCase())
                            && address.getHousenumber().toLowerCase().trim().equals(addressStrings[1].trim().toLowerCase())
                            && (addressStrings[2].equals("") || (!addressStrings[2].equals("") && address.getPostcode().trim().equals(addressStrings[2].trim())))

            ) {

                return address;
            }
        }

        return null;
    }

	public void saveTST(TST tst) {
        this.tst = tst;
	}
}
