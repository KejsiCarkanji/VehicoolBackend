package com.vehicool.vehicool.util.mappers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

public class ResponseMapper {
    private ResponseMapper(){}
    public static ResponseEntity<Object> map(String statusText, HttpStatus status,Object responseObject,String message){
        Map<String,Object> map = new HashMap<>();
        map.put("status",statusText);
        map.put("data",responseObject);
        map.put("message",message);
        return new ResponseEntity<>(map,status);
    }
}
