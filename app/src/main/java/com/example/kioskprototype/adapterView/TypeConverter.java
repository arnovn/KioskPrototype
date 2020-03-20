package com.example.kioskprototype.adapterView;

import java.util.HashMap;
import java.util.Map;

//TODO: create map base on database bikeinfo types.

public class TypeConverter {

    Map<Integer, String> typeMapper;


    public TypeConverter(){
        typeMapper = new HashMap<Integer, String>();
        typeMapper.put(1, "Electric bike");
        typeMapper.put(2, "Normal bike");
    }

    public String getType(int id){
        return typeMapper.get(id).toString();
    }
}
