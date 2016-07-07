package com.yuntongxun.ecdemo.ui.chatting.RedPackUtils;

import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuntongxun.ecsdk.ECMessage;

import utils.RedPacketConstant;

/**
 * Created by ustc on 2016/6/20.
 */
public class CheckRedPacketMessageUtil {


    public static JSONObject isRedPacketMessage(ECMessage message){
        JSONObject rpJSON=null;

        if(message.getType()== ECMessage.Type.TXT){

            // 设置内容
            String extraData = message.getUserData() ;
            if(extraData!=null){

                try {
                    JSONObject jsonObject= JSONObject.parseObject(extraData);
                    if(jsonObject!=null&&jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)&&jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)){

                        rpJSON=jsonObject;
                    }
                }catch (JSONException e){

                    Log.e("JSONExceptionr",e.toString());
                }
            }
        }


        return rpJSON;
    }

    public static JSONObject isRedPacketAckedMessage(ECMessage message){
        JSONObject jsonRedPacketAcked=null;
        if(message.getType()== ECMessage.Type.TXT){

            // 设置内容
            String extraData =message.getUserData();
            if(extraData!=null){
                try {
                    JSONObject jsonObject= JSONObject.parseObject(extraData);
                    if(jsonObject!=null&&jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)&&jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)){

                        jsonRedPacketAcked=jsonObject;
                    }
                }catch (JSONException e){

                    Log.e("JSONExceptionr",e.toString());
                }
            }
        }


        return jsonRedPacketAcked;
    }

    public  static  boolean   isMyAckMessage(ECMessage message,String currentUserId) {
        boolean IS_MY_MESSAGE = true;
        JSONObject jsonObject = isRedPacketAckedMessage(message);
        if (jsonObject != null) {

            String recieveUserId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
            String recieveUserNick = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME);//红包接收者昵称
            String sendUserId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
            String sendUserNick = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME);//红包发送者昵称
            //发送者和领取者都不是是自己-
            if (!currentUserId.equals(recieveUserId) && !currentUserId.equals(sendUserId)) {
                IS_MY_MESSAGE=false;
            }

        }
        return IS_MY_MESSAGE;
    }



}
