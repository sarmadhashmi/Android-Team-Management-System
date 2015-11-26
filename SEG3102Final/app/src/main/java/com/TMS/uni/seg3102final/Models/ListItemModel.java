package com.TMS.uni.seg3102final.Models;

public class ListItemModel{
    String name;
    int value; /* 0 - checkbox disable, 1 - checkbox enable */

    public ListItemModel(String name, int value){
        this.name = name;
        this.value = value;
    }
    public String getName(){
        return this.name;
    }
    public int getValue(){
        return this.value;
    }

}