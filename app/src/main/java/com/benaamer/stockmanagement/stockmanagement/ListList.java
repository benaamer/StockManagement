package com.benaamer.stockmanagement.stockmanagement;

import java.util.ArrayList;
import java.util.List;


public class ListList {

    private List<ExpiryDatesList> expiryDatesLists;
    private int currentList;

    private List<Item> addedCodes;

    public ListList() {
        this.expiryDatesLists = new ArrayList<ExpiryDatesList>();
        this.currentList = -1;
        this.addedCodes = new ArrayList<Item>();
    }

    public void add(ExpiryDatesList list) {
        this.expiryDatesLists.add(list);
        currentList = this.expiryDatesLists.size() - 1;
    }

    public void remove(ExpiryDatesList list) {
        this.expiryDatesLists.remove(expiryDatesLists.indexOf(list));
    }

    public ExpiryDatesList get(int index) {
        return expiryDatesLists.get(index);
    }

    public ExpiryDatesList getCurrent() {
        return expiryDatesLists.get(currentList);
    }

    public int size() {
        return expiryDatesLists.size();
    }

    public List<ExpiryDatesList> getIterable() {
        return expiryDatesLists;
    }

    public String toString() {
        String res = "";
        for (ExpiryDatesList list: expiryDatesLists) {
            res += list.toString();
        }
        return res;
    }

    public int getCurrentList() {
        return currentList;
    }

    public void setCurrentList(int newList) {
        this.currentList = newList;
    }

    public List<Item> getAddedCodes() {
        return addedCodes;
    }

    public boolean noSelectedList() {
        System.out.println(this.currentList);
        return this.currentList == -1;
    }

    public void addNewCode(Item list) {
        addedCodes.add(list);
    }

    public Item checkBarcodeInCodes(String barcode) {
        for (Item item : addedCodes) {
            if (item.getBarcode().equals(barcode)) {
                return item;
            }
        }
        return null;
    }

}
