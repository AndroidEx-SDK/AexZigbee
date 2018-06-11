package com.androidex.comassistant.util;

import com.androidex.plugins.kkserial;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/6/11
 */
public class Control2530 {

    private final kkserial mSerial;
    private static Control2530 control2530;
    private final int mSerialFd;

    public Control2530(kkserial serial,int fd) {
        mSerial = serial;
        mSerialFd = fd;
    }

    public static Control2530 getInstance(kkserial serial,int fd){
        if (control2530==null){
            control2530 = new Control2530(serial,fd);
        }
        return control2530;
    }

    //重置、
    public void reset(){

    }
    //复位
    public void restoration(){

    }
    //上电
    public void electrify(){

    }
    //串口波特率、

    /**
     * 模块的节点类型
     */
    //协调器
    public void setCoordinator (){

    }
    //路由器
     public void setRouter(){

    }
    //终端
 public void setTerminal(){

    }

    /**
     *     透传发送方式
     */
    //广播
    public void setBroadcast(){

    }

    //点播
    public void setBunchPlanting(){

    }
    //组播
    public void setMulticast(){

    }

    // 目标短地址
    public void setTargetShortAddress(){

    }

    // 目标组播号
    public void setTargetMulticastNumber(){

    }

    // 本地组播号
    public   void setLocalMulticastNumber(){

    }

    // 入网状态
    public void setStateNet(){

    }

    //可以发送广播、
    public void sendBroadcast() {


    }

    //发送点播、
    public void sendBunchPlanting() {

    }

    //发送组播
    public void sendMulticast(){

     }

    //可以查看参数配置
    //节点类型
    public void catNodeTypes(){

    }

    public void catPAN_ID(){

    }
    public void catChannel(){

    }

    //波特率
    public void catBaudrate(){

    }

    //发射功率
    public void catTransmitterPower(){

    }
    //本地组播号
    public void catLocalMulticastNumber(){

    }
    //目标短地址
    public void catTargetShortAddress(){

    }
    //目标组播号
    public void catTargetMulticastNumber(){

    }
    //全透传发送方式
    public void catSendTypes(){

    }
    //MAC地址
    public void catMacAddress(){

    }
    //本地短地址
    public void catLocalShortAddress(){

    }
    //网络密匙
    public void catKeyNet(){

    }


}
