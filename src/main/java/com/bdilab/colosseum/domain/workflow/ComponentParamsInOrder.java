package com.bdilab.colosseum.domain.workflow;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author liran
 * @Date 2021/4/2
 * 此类是为了包装如下形式的json
 * [{"select_all":{"sample_num":5}},{"random_sample":{}},{"random_tuning":{}}]
 */
public class ComponentParamsInOrder {
    private ArrayList<HashMap<String,HashMap<String,Object>>> componentParamsInOrder = new ArrayList<>();

    public ArrayList<HashMap<String, HashMap<String, Object>>> getComponentParamsInOrder() {
        return componentParamsInOrder;
    }
}




