package com.inn.cafe.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CafeUtil {

    private CafeUtil() {

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}", httpStatus);
    }

    public static String getVVID(){
        Data data = new Data();
        long time = data.getTime();
        return "Bill-"+ time;
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException{
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String,Object> getMapFromJson(String data) {
        if (!String.isNullOrEmpty(data))
            return new Gson().fromJson(data,new TypeToken<Map<String,Object>>(){
            }.getType());
        return new HashMap<>();
    }

    public static Boolean isFileExist(String path){
        log.info("Inside isFileExist{}",path);
        try {
            File file = new File(path);
            return (file != null && file.exists()) ? Boolean.True :Boolean.FALSE;
        }catch (Exception ex) {
            ex.printStractTrace();
        }
        return false;
    }
}