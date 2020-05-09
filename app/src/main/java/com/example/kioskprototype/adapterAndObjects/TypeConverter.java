package com.example.kioskprototype.adapterAndObjects;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

//TODO: create map base on database bikeinfo types.

/**
 * Convert int type to String type
 */
class TypeConverter {

    /**
     * Maps ints to Strings
     */
    private Map<Integer, String> typeMapper;


    /**
     * Constructor of TypeConverter
     */
    @SuppressLint("UseSparseArrays")
    TypeConverter(){
        typeMapper = new HashMap<>();
        typeMapper.put(1, "Electric bike");
        typeMapper.put(2, "Normal bike");
    }

    /**
     * Getter of type
     * @param id
     *              int type
     * @return
     *              String type
     */
    String getType(int id){
        return typeMapper.get(id);
    }
}
