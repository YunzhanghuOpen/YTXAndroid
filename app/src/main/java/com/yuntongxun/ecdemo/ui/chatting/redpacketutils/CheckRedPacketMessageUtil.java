package com.yuntongxun.ecdemo.ui.chatting.redpacketutils;

import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuntongxun.ecsdk.ECMessage;

import utils.RedPacketConstant;

public class CheckRedPacketMessageUtil {


    public static JSONObject isRedPacketMessage(ECMessage message) {
        JSONObject rpJSON = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (extraData != null) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(extraData);
                    if (jsonObject != null && jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)
                            && jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)) {
                        rpJSON = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return rpJSON;
    }

    public static JSONObject isRedPacketAckMessage(ECMessage message) {
        JSONObject jsonRedPacketAck = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (extraData != null) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(extraData);
                    if (jsonObject != null && jsonObject.containsKey(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE) && jsonObject.getBoolean(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)) {

                        jsonRedPacketAck = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return jsonRedPacketAck;
    }

    public static boolean isMyAckMessage(ECMessage message, String currentUserId) {
        boolean isMyselfAckMsg = true;
        JSONObject jsonObject = isRedPacketAckMessage(message);
        if (jsonObject != null) {
            String receiverId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
            String senderId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
            //发送者和领取者都不是自己
            if (!currentUserId.equals(receiverId) && !currentUserId.equals(senderId)) {
                isMyselfAckMsg = false;
            }
        }
        return isMyselfAckMsg;
    }


}
