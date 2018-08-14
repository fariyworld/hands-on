package com.mace.utils;

import javax.swing.*;
import java.util.*;

/**
 * description:
 * <br />
 * Created by mace on 16:29 2018/8/6.
 */
public class MapOps {

    public static <K,V> Map<K, V> sortByKey(Map<K, V> map, Comparator comparator){

        if(CheckOps.isEmpty(map))
            return null;

        Map<K, V> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(map);
        return sortedMap;
    }


    public static <K,V> Map<K, V> sortByValue(Map<K, V> map, Comparator comparator){

        if(CheckOps.isEmpty(map))
            return null;

        List<Map.Entry<K,V>> entryList = new ArrayList<>(map.entrySet());
        Collections.sort(entryList, comparator);
        Map<K,V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> kvEntry : entryList) {
            sortedMap.put(kvEntry.getKey(), kvEntry.getValue());
        }

        return sortedMap;
    }


    public static void main(String[] args) {


    }

}
