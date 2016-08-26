package com.yuntongxun.ecdemo.ui.chatting.redpacketutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.storage.ContactSqlManager;
import com.yuntongxun.ecdemo.ui.chatting.ChattingActivity;
import com.yuntongxun.ecdemo.ui.contact.ECContacts;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECGroupManager;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECGroupMember;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.bean.RPUserBean;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.callback.GroupMemberCallback;
import com.yunzhanghu.redpacketui.callback.NotifyGroupMemberCallback;
import com.yunzhanghu.redpacketui.ui.activity.RPChangeActivity;
import com.yunzhanghu.redpacketui.ui.activity.RPTransferDetailActivity;
import com.yunzhanghu.redpacketui.utils.RPGroupMemberUtil;
import com.yunzhanghu.redpacketui.utils.RPOpenPacketUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RedPacketUtil implements Response.Listener<JSONObject>, Response.ErrorListener {

    public static final int REQUEST_CODE_SEND_MONEY = 15;

    public static final int REQUEST_CODE_SEND_TRANSFER = 16;


    private TokenData mTokenData;

    private RPValueCallback<TokenData> mRPValueCallback;

    private static RedPacketUtil mRedPacketUtil;

    public static RedPacketUtil getInstance() {
        if (mRedPacketUtil == null) {
            synchronized (RedPacketUtil.class) {
                if (mRedPacketUtil == null) {
                    mRedPacketUtil = new RedPacketUtil();
                }

            }
        }
        return mRedPacketUtil;
    }

    public void setGroupMember(String groupId) {

        ECGroupManager groupManager = ECDevice.getECGroupManager();
        // 调用获取群组成员接口，设置结果回调
        groupManager.queryGroupMembers(groupId,
                new ECGroupManager.OnQueryGroupMembersListener() {
                    @Override
                    public void onQueryGroupMembersComplete(ECError error, final List members) {
                        if (error.errorCode == SdkErrorCode.REQUEST_SUCCESS
                                && members != null) {
                            // 获取群组成员成功
                            // 将群组成员信息更新到本地缓存中（sqlite） 通知UI更新
                            //如果不需要专属红包可以去掉
                            RPGroupMemberUtil.getInstance().setGroupMemberListener(new NotifyGroupMemberCallback() {
                                @Override
                                public void getGroupMember(final String groupID, final GroupMemberCallback mCallBack) {
                                    List<RPUserBean> userBeanList = new ArrayList<RPUserBean>();
                                    for (int i = 0; i < members.size(); i++) {
                                        RPUserBean userBean = new RPUserBean();
                                        ECGroupMember member = (ECGroupMember) members.get(i);
                                        userBean.userId = member.getVoipAccount();
                                        if (userBean.userId.equals(CCPAppManager.getUserId())) {
                                            continue;
                                        }
                                        userBean.userAvatar = "none";
                                        userBean.userNickname = TextUtils.isEmpty(member.getDisplayName()) ? member.getVoipAccount() : member.getDisplayName();
                                        userBeanList.add(userBean);
                                    }
                                    mCallBack.setGroupMember(userBeanList);
                                }
                            });
                            return;
                        }
                        // 群组成员获取失败
                        Log.e("ECSDK_Demo", "sync group detail fail " + ", errorCode=" + error.errorCode);

                    }

                }
        );
    }

    /**
     * 进入转账页面
     *
     * @param fragment
     * @param toChatUsername
     * @param requestCode
     */
    public static void startRedTransferActivityForResult(Fragment fragment, final String toChatUsername, int requestCode) {
        //发送者头像url
//        String fromAvatarUrl = "none";
//        //发送者昵称 设置了昵称就传昵称 否则传id
//        String fromNickname = EMChatManager.getInstance().getCurrentUser();
//        EaseUser easeUser = EaseUserUtils.getUserInfo(fromNickname);
//        if (easeUser != null) {
//            fromAvatarUrl = TextUtils.isEmpty(easeUser.getAvatar()) ? "none" : easeUser.getAvatar();
//            fromNickname = TextUtils.isEmpty(easeUser.getNick()) ? easeUser.getUsername() : easeUser.getNick();
//        }
//        String toAvatarUrl="none";
//        String toUserName="";
//        EaseUser easeToUser=EaseUserUtils.getUserInfo(toChatUsername);
//        if (easeToUser!=null){
//            toAvatarUrl = TextUtils.isEmpty(easeToUser.getAvatar()) ? "none" : easeToUser.getAvatar();
//            toUserName = TextUtils.isEmpty(easeToUser.getNick()) ? easeToUser.getUsername() : easeToUser.getNick();
//        }
//        RedPacketInfo redPacketInfo = new RedPacketInfo();
//        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
//        redPacketInfo.fromNickName = fromNickname;
//        //接收者Id或者接收的群Id
//        redPacketInfo.toUserId = toChatUsername;
//        redPacketInfo.toNickName=toUserName;
//        redPacketInfo.toAvatarUrl=toAvatarUrl;
//
//        Intent intent = new Intent(fragment.getContext(), RPRedTransferActivity.class);
//        intent.putExtra(RPConstant.EXTRA_TRANSFER_PACKET_INFO, redPacketInfo);
//        intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, getTokenData());
//        fragment.startActivityForResult(intent, requestCode);
    }


    /**
     * 打开红包
     *
     * @param mContext
     * @param ecMessage
     * @param clientUser
     */
    public void openRedPacket(final ChattingActivity mContext, ECMessage ecMessage, ClientUser clientUser) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setCanceledOnTouchOutside(false);
            RedPacketInfo redPacketInfo = new RedPacketInfo();
            JSONObject jsonRedPacket = RedPacketUtil.getInstance().isRedPacketMessage(ecMessage);
            String moneyID = jsonRedPacket.getString(RPConstant.EXTRA_RED_PACKET_ID);//红包id
            String packetType = jsonRedPacket.getString(RPConstant.MESSAGE_ATTR_RED_PACKET_TYPE);
            String specialReceiveId = jsonRedPacket.getString(RPConstant.MESSAGE_ATTR_SPECIAL_RECEIVER_ID);
            String toAvatarUrl = "none";//容联云没有网址图片
            String toNickName = clientUser.getUserName();
            toNickName = TextUtils.isEmpty(toNickName) ? clientUser.getUserId() : toNickName;
            redPacketInfo.toAvatarUrl = toAvatarUrl;//红包接受者头像
            redPacketInfo.toNickName = toNickName;//红包接受者昵称
            if (ecMessage.getDirection() == ECMessage.Direction.RECEIVE) {//接受者
                redPacketInfo.moneyMsgDirect = RPConstant.MESSAGE_DIRECT_RECEIVE;
            } else {//发送者
                redPacketInfo.moneyMsgDirect = RPConstant.MESSAGE_DIRECT_SEND;
            }
            if (mContext.mChattingFragment.isPeerChat()) {//群聊
                redPacketInfo.chatType = RPConstant.CHATTYPE_GROUP;
            } else {//单聊
                redPacketInfo.chatType = RPConstant.CHATTYPE_SINGLE;
            }
            redPacketInfo.redPacketId = moneyID;
            //定向红包
            if (!TextUtils.isEmpty(packetType) && packetType.equals(RPConstant.GROUP_RED_PACKET_TYPE_EXCLUSIVE)) {
                ECContacts contact = ContactSqlManager.getContact(specialReceiveId);
                if (contact != null) {
                    redPacketInfo.specialNickname = contact.getNickname();
                } else {
                    redPacketInfo.specialNickname = specialReceiveId;
                }
                redPacketInfo.specialAvatarUrl = "none";////开发者换成自己app的图像
                redPacketInfo.toUserId = CCPAppManager.getClientUser().getUserId();//接受者id
            }
            TokenData tokenData = new TokenData();
            tokenData.appUserId = clientUser.getUserId();
            RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, tokenData, mContext, new RPOpenPacketUtil.RPOpenPacketCallBack() {
                @Override
                public void onSuccess(String senderId, String senderNickname) {
                    mContext.mChattingFragment.sendRedPacketAckMessage(senderId, senderNickname);
                }

                @Override
                public void showLoading() {
                    progressDialog.show();
                }

                @Override
                public void hideLoading() {
                    progressDialog.dismiss();
                }

                @Override
                public void onError(String code, String message) {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打开转账
     *
     * @param mContext
     * @param ecMessage
     * @param clientUser
     */
    public void openTransfer(final ChattingActivity mContext, ECMessage ecMessage, ClientUser clientUser) {
        try {
            JSONObject jsonTransfer = RedPacketUtil.getInstance().isTransferMsg(ecMessage);
            String amount = jsonTransfer.getString(RPConstant.EXTRA_TRANSFER_AMOUNT);//转账金额
            String time = jsonTransfer.getString(RPConstant.EXTRA_TRANSFER_PACKET_TIME);//转账时间
            String fromAvatarUrl = "none";//开发者换成自己app的图像
            String fromNickName = clientUser.getUserName();
            fromNickName = TextUtils.isEmpty(fromNickName) ? clientUser.getUserId() : fromNickName;
            String messageDirect;
            if (ecMessage.getDirection() == ECMessage.Direction.RECEIVE) {//接受者
                messageDirect = RPConstant.MESSAGE_DIRECT_SEND;
            } else {//发送者
                messageDirect = RPConstant.MESSAGE_DIRECT_RECEIVE;
            }
            RedPacketInfo redPacketInfo = new RedPacketInfo();
            redPacketInfo.moneyMsgDirect = messageDirect;
            redPacketInfo.redPacketAmount = amount;
            redPacketInfo.fromNickName = fromNickName;
            redPacketInfo.fromAvatarUrl = fromAvatarUrl;
            redPacketInfo.transferTime = time;
            TokenData tokenData = new TokenData();
            tokenData.appUserId = clientUser.getUserId();
            Intent intent = new Intent(mContext, RPTransferDetailActivity.class);
            intent.putExtra(RPConstant.EXTRA_TRANSFER_PACKET_INFO, redPacketInfo);
            intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, tokenData);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 进入零钱页
     */

    public void startChangeActivity(FragmentActivity fragmentActivity, ClientUser clientUser) {
        String fromNickname = clientUser.getUserName();
        String fromAvatarUrl = "none";//容联云没有在线图片机制
        Intent intent = new Intent(fragmentActivity, RPChangeActivity.class);
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromNickName = fromNickname;
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
        TokenData tokenData = new TokenData();
        tokenData.appUserId = clientUser.getUserId();
        intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, tokenData);
        fragmentActivity.startActivity(intent);
    }

    /**
     * 是否红包消息
     *
     * @param message
     * @return
     */
    public JSONObject isRedPacketMessage(ECMessage message) {
        JSONObject rpJSON = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (!TextUtils.isEmpty(extraData)) {
                try {
                    JSONObject jsonObject = new JSONObject(extraData);
                    if (jsonObject.has(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE) && jsonObject.getBoolean(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE)) {
                        rpJSON = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return rpJSON;
    }

    /**
     * 是否回执消息
     *
     * @param message
     * @return
     */
    public JSONObject isRedPacketAckMessage(ECMessage message) {
        JSONObject jsonRedPacketAck = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (!TextUtils.isEmpty(extraData)) {
                try {
                    JSONObject jsonObject = new JSONObject(extraData);
                    if (jsonObject.has(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)
                            && jsonObject.getBoolean(RPConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE)) {
                        jsonRedPacketAck = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return jsonRedPacketAck;
    }

    /**
     * 是否转账消息
     *
     * @param message
     * @return
     */
    public JSONObject isTransferMsg(ECMessage message) {
        JSONObject jsonTransfer = null;
        if (message.getType() == ECMessage.Type.TXT) {
            // 设置内容
            String extraData = message.getUserData();
            if (!TextUtils.isEmpty(extraData)) {
                try {
                    JSONObject jsonObject = new JSONObject(extraData);
                    if (jsonObject.has(RPConstant.MESSAGE_ATTR_IS_TRANSFER_PACKET_MESSAGE)
                            && jsonObject.getBoolean(RPConstant.MESSAGE_ATTR_IS_TRANSFER_PACKET_MESSAGE)) {
                        jsonTransfer = jsonObject;
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        }
        return jsonTransfer;
    }

    /**
     * 是否是自己的回执消息消息
     *
     * @param message
     * @return
     */
    public boolean isMyAckMessage(ECMessage message, String currentUserId) {
        boolean isMyselfAckMsg = true;
        JSONObject jsonObject = isRedPacketAckMessage(message);
        if (jsonObject != null) {
            try {
                String receiverId = jsonObject.getString(RPConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
                String senderId = jsonObject.getString(RPConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
                //发送者和领取者都不是自己
                if (!currentUserId.equals(receiverId) && !currentUserId.equals(senderId)) {
                    isMyselfAckMsg = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isMyselfAckMsg;
    }

    /**
     * 刷新签名
     *
     * @param context
     * @param userId
     * @param rpValueCallback
     */

    public void requestSign(Context context, String userId, final RPValueCallback<TokenData> rpValueCallback) {
        mRPValueCallback = rpValueCallback;
        //String mockUrl = "http://rpv2.yunzhanghu.com/api/sign?duid=" + userId;
        String mockUrl = "http://10.10.1.10:32802/api/sign?duid=" + userId;
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, mockUrl, this, this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        mRPValueCallback.onError(volleyError.getMessage(), volleyError.toString());
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.length() > 0) {
            try {
                String partner = jsonObject.getString("partner");
                String userId = jsonObject.getString("user_id");
                String timestamp = jsonObject.getString("timestamp");
                String sign = jsonObject.getString("sign");
                //保存红包Token
                if (mTokenData == null) {
                    mTokenData = new TokenData();
                }
                mTokenData.authPartner = partner;
                mTokenData.appUserId = userId;
                mTokenData.timestamp = timestamp;
                mTokenData.authSign = sign;
                mRPValueCallback.onSuccess(mTokenData);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
                mRPValueCallback.onError(e.getMessage(), e.getMessage());
            }

        } else {
            mRPValueCallback.onError("", "sign data is  null");
        }
    }

}
