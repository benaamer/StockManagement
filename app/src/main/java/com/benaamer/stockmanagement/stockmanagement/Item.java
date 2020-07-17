package com.benaamer.stockmanagement.stockmanagement;

import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Item {

    private String name;
    private String expiryDate;
    private String barcodeID;
    private boolean checked = false, xed = false;

    public Item(String name, String expiryDate, String barcodeID) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.barcodeID = barcodeID;
    }

    public Item(Item other) {
        this.name = other.getName();
        this.expiryDate = other.expiryDate;
        this.barcodeID = other.getBarcode();
    }


    public Item(String barcodeID) {
        this("NoName","Sell, Sell, Sell",barcodeID);

        String name = "Unnamed item";
        String expiryDate = "";

        try {
            URL url = new URL("http://api.upcdatabase.org/xml/0a4a07f05adbdb4d244054fdfa66aea5/" + barcodeID);
            URLConnection conn = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(conn.getInputStream());

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("output");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                //System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    //finds itemname in one of three places or defaults to assignments above
                    String itemname = "";
                    NodeList itemname1 = eElement.getElementsByTagName("itemname");
                    if (itemname1 != null && itemname1.getLength() > 0)
                        itemname = itemname1.item(0).getTextContent();
                    if (! itemname.equals("")) {
                        name = itemname;
                        assign(name,  expiryDate);
                        return;
                    } else {
                        itemname1 = eElement.getElementsByTagName("alias");
                        if (itemname1 != null && itemname1.getLength() > 0)
                            itemname = itemname1.item(0).getTextContent();
                        if (! itemname.equals("")) {
                            name = itemname;
                            assign(name, expiryDate);
                            return;
                        } else {
                            itemname1 = eElement.getElementsByTagName("description");
                            if (itemname1 != null && itemname1.getLength() > 0)
                                itemname = itemname1.item(0).getTextContent();
                            if (! itemname.equals("")) {
                                name = itemname;
                                assign(name, expiryDate);
                                return;
                            } else {
                                assign(name, expiryDate);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            assign(name, expiryDate);
            e.printStackTrace();
            return;
        }
    }

    private void assign(String name, String expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return this.name;
    }

    public String getExpiryDate() {
        return this.expiryDate;
    }

    public String getBarcode() {
        return barcodeID;
    }

    public Item setBarcode(String barcode) {
        this.barcodeID = barcode;
        return this;
    }

    public String toString() {
        return this.name + "\n" + this.expiryDate;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public boolean isXed() {
        return this.xed;
    }

}
