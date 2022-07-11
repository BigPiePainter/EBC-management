package com.pofa.ebcadmin.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.ToString;


@ToString
public class JsonResponse {
    public int code = 200;
    public Object data;
    public String msg = "success";

    public JsonResponse(int code){
        this.code = code;
    }

    public JsonResponse(Object data){
        this.data = data;
    }

    public JsonResponse(int code, Object data){
        this.code = code;
        this.data = data;
    }

}
