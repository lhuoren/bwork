package com.singun.openvpn.api.TAHGPSRail.entity;

/**
 * Created by access on 2017/6/21.
 */

public class TAHGPSRailGetEntity {
    /**
     * 序列化版本号（自动生成，方法请看上面）
     */
//    private static final long serialVersionUID = 88722423642445L;
    //定义的私有属性
    private String ID;
    private String sno;
    private String name;
    private String GPS;



    //无参数的构造器（如果不写系统会自动生成无参数的构造器，但不会显示出来）
    public TAHGPSRailGetEntity(){

    }
    //有参数的构造器
    public TAHGPSRailGetEntity(String id,String sno,String name, String GPS){
        this.ID = id;
        this.sno = name;
        this.name = name;
        this.GPS = GPS;
    }
    //创建的setter和getter方法
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGPS() {
        return GPS;
    }

    public void setGPS(String GPS) {
        this.GPS = GPS;
    }

}
