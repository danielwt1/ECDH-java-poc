package com.example.pocecdh.config;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ConcurrentMapSesion {
    private final ConcurrentMap<String, SesionDTO> mapSesionPrivateKey = new ConcurrentHashMap<>();


    public SesionDTO get(String key){
        return mapSesionPrivateKey.get(key);
    }
    public void put(String key,SesionDTO value){
        mapSesionPrivateKey.put(key,value);
    }
    public void remove(String key){
        mapSesionPrivateKey.remove(key);
    }
    public int size(){
        return mapSesionPrivateKey.size();
    }
}
