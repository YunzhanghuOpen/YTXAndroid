/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yuntongxun.ecdemo.ui.chatting;


import com.yuntongxun.ecdemo.ui.chatting.RedPackUtils.CheckRedPacketMessageUtil;
import com.yuntongxun.ecdemo.ui.chatting.model.ChattingRowType;
import com.yuntongxun.ecsdk.ECMessage;

/**
 * @author Jorstin Chan@容联•云通讯
 * @version 4.0
 * @date 2014-12-11
 */
public class ChattingsRowUtils {

    /**
     * @param ecMessage
     * @return
     */
    public static int getChattingMessageType(ECMessage ecMessage) {
        ECMessage.Type type = ecMessage.getType();
        if (type == ECMessage.Type.TXT) {
            if (CheckRedPacketMessageUtil.isRedPacketMessage(ecMessage) != null) {

                return 7000;
            } else if (CheckRedPacketMessageUtil.isRedPacketAckMessage(ecMessage) != null) {

                return 8000;
            }

            return 2000;


        } else if (type == ECMessage.Type.VOICE) {
            return 60;
        } else if (type == ECMessage.Type.FILE) {
            return 1024;
        } else if (type == ECMessage.Type.IMAGE) {
            return 200;
        } else if (type == ECMessage.Type.VIDEO) {
            return 1024;
        } else if (type == ECMessage.Type.LOCATION) {
            return 2200;
        } else if (type == ECMessage.Type.CALL) {
            return 2400;
        } else if (type == ECMessage.Type.RICH_TEXT) {
            return 2600;
        }
        return 2000;
    }

    /**
     * @param iMessage
     * @return
     */
    public static Integer getMessageRowType(ECMessage iMessage) {
        ECMessage.Type type = iMessage.getType();
        ECMessage.Direction direction = iMessage.getDirection();
        if (type == ECMessage.Type.TXT) {

            if (direction == ECMessage.Direction.RECEIVE) {


                if (CheckRedPacketMessageUtil.isRedPacketMessage(iMessage) != null) {

                    return ChattingRowType.REDPACKET_ROW_RECEIVED.getId();
                } else if (CheckRedPacketMessageUtil.isRedPacketAckMessage(iMessage) != null) {

                    return ChattingRowType.REDPACKE_ROW_ACK_RECEIVED.getId();
                }


                return ChattingRowType.DESCRIPTION_ROW_RECEIVED.getId();
            } else {

                if (CheckRedPacketMessageUtil.isRedPacketMessage(iMessage) != null) {

                    return ChattingRowType.REDPACKE_ROW_TO.getId();
                } else if (CheckRedPacketMessageUtil.isRedPacketAckMessage(iMessage) != null) {

                    return ChattingRowType.REDPACKE_ROW_ACK_TO.getId();
                }


                return ChattingRowType.DESCRIPTION_ROW_TRANSMIT.getId();

            }


        } else if (type == ECMessage.Type.VOICE) {
            if (direction == ECMessage.Direction.RECEIVE) {
                return ChattingRowType.VOICE_ROW_RECEIVED.getId();
            }
            return ChattingRowType.VOICE_ROW_RECEIVED.getId();
        } else if (type == ECMessage.Type.FILE || type == ECMessage.Type.VIDEO) {
            if (direction == ECMessage.Direction.RECEIVE) {
                return ChattingRowType.FILE_ROW_RECEIVED.getId();
            }
            return ChattingRowType.FILE_ROW_RECEIVED.getId();
        } else if (type == ECMessage.Type.IMAGE) {
            if (direction == ECMessage.Direction.RECEIVE) {
                return ChattingRowType.IMAGE_ROW_RECEIVED.getId();
            }
            return ChattingRowType.IMAGE_ROW_RECEIVED.getId();
        } else if (type == ECMessage.Type.LOCATION) {
            if (direction == ECMessage.Direction.RECEIVE) {
                return ChattingRowType.LOCATION_ROW_RECEIVED.getId();
            }
            return ChattingRowType.LOCATION_ROW_TRANSMIT.getId();

        }
        return -1;
    }
}
