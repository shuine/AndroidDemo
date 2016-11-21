package com.shine.demo.statusbar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yeshuxin on 16-11-21.
 */

public class StatusData {

    public String desc;
    public String isValid;
    public int processId;
    public String time;
    private final String VALID = "Y";

    public boolean isValid(){
        if(isValid.equals(VALID)){
            return true;
        }
        return false;
    }

    public static List<StatusData> getDemoData(int size){
        List<StatusData> data = new ArrayList<>();
        for (int i = 0;i<size ;i++){
            StatusData status = new StatusData();
            status.desc = "受理";
            status.isValid="Y";
            status.processId=i;
            data.add(status);
        }
        return data;
    }
}
