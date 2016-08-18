package utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.ui.activity.RPChangeActivity;
import com.yunzhanghu.redpacketui.ui.activity.RPRedPacketActivity;
import com.yunzhanghu.redpacketui.utils.RPOpenPacketUtil;

import org.json.JSONException;

public class RedPacketUtil implements Response.Listener<org.json.JSONObject>, Response.ErrorListener {

    public static final int REQUEST_CODE_SEND_MONEY = 15;

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

    /**
     * 进入发红包页面
     *
     * @param fragment
     * @param jsonObject
     * @param requestCode
     */
    public static void startRedPacketActivityForResult(Fragment fragment, JSONObject jsonObject, int requestCode) {
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromAvatarUrl = jsonObject.getString(RedPacketConstant.KEY_FROM_AVATAR_URL);
        redPacketInfo.fromNickName = jsonObject.getString(RedPacketConstant.KEY_FROM_NICK_NAME);
        //接收者Id或者接收的群Id
        int chatType = jsonObject.getInteger(RedPacketConstant.KEY_CHAT_TYPE);
        if (chatType == 1) {
            redPacketInfo.toUserId = jsonObject.getString(RedPacketConstant.KEY_USER_ID);
            redPacketInfo.chatType = 1;
        } else if (chatType == 2) {
            redPacketInfo.toGroupId = jsonObject.getString(RedPacketConstant.KEY_GROUP_ID);
            redPacketInfo.groupMemberCount = jsonObject.getInteger(RedPacketConstant.KEY_GROUP_MEMBERS_COUNT);
            redPacketInfo.chatType = 2;
        }
        Intent intent = new Intent(fragment.getActivity(), RPRedPacketActivity.class);
        intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
        String currentUserId = jsonObject.getString(RedPacketConstant.KEY_CURRENT_ID);
        TokenData tokenData = new TokenData();
        tokenData.appUserId = currentUserId;
        intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, tokenData);
        fragment.startActivityForResult(intent, requestCode);
    }


    /**
     * 拆红包的方法
     *
     * @param activity   FragmentActivity
     * @param jsonObject
     */
    public static void openRedPacket(final FragmentActivity activity, JSONObject jsonObject, final OpenRedPacketSuccess openRedPacketSuccess) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCanceledOnTouchOutside(false);
        String messageDirect;
        //接收者头像url 默认值为none
        String toAvatarUrl = jsonObject.getString(RedPacketConstant.KEY_TO_AVATAR_URL);
        //接收者昵称 默认值为当前用户ID
        final String toNickname = jsonObject.getString(RedPacketConstant.KEY_TO_NICK_NAME);
        String redPacketId = jsonObject.getString(RPConstant.EXTRA_RED_PACKET_ID);
        messageDirect = jsonObject.getString(RedPacketConstant.KEY_MESSAGE_DIRECT);
        final int chatType = jsonObject.getInteger(RedPacketConstant.KEY_CHAT_TYPE);
        String specialAvatarUrl = jsonObject.getString(RedPacketConstant.KEY_SPECIAL_AVATAR_URL);
        String specialNickname = jsonObject.getString(RedPacketConstant.KEY_SPECIAL_NICK_NAME);
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.redPacketId = redPacketId;
        redPacketInfo.toAvatarUrl = toAvatarUrl;
        redPacketInfo.toNickName = toNickname;
        redPacketInfo.moneyMsgDirect = messageDirect;
        redPacketInfo.chatType = chatType;
        String packetType = jsonObject.getString(RedPacketConstant.MESSAGE_ATTR_RED_PACKET_TYPE);
        if (!TextUtils.isEmpty(packetType) && packetType.equals(RedPacketConstant.GROUP_RED_PACKET_TYPE_EXCLUSIVE)) {
            redPacketInfo.specialAvatarUrl = specialAvatarUrl;
            redPacketInfo.specialNickname = specialNickname;
        }
        String currentUserId = jsonObject.getString(RedPacketConstant.KEY_CURRENT_ID);
        redPacketInfo.toUserId = currentUserId;
        TokenData tokenData = new TokenData();
        tokenData.appUserId = currentUserId;
        RPOpenPacketUtil.getInstance().openRedPacket(redPacketInfo, tokenData, activity, new RPOpenPacketUtil.RPOpenPacketCallBack() {
            @Override
            public void onSuccess(String senderId, String senderNickname) {
                openRedPacketSuccess.onSuccess(senderId, senderNickname);
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
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface OpenRedPacketSuccess {
        void onSuccess(String senderId, String senderNickname);
    }


    /**
     * 进入零钱页
     */

    public static void startChangeActivity(FragmentActivity fragmentActivity, String fromNickname, String fromAvatarUrl, String userId) {
        Intent intent = new Intent(fragmentActivity, RPChangeActivity.class);
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        redPacketInfo.fromNickName = fromNickname;
        redPacketInfo.fromAvatarUrl = fromAvatarUrl;
        intent.putExtra(RPConstant.EXTRA_RED_PACKET_INFO, redPacketInfo);
        TokenData tokenData = new TokenData();
        tokenData.appUserId = userId;
        intent.putExtra(RPConstant.EXTRA_TOKEN_DATA, tokenData);
        fragmentActivity.startActivity(intent);
    }

    public void requestSign(Context context, String userId,final RPValueCallback<TokenData> rpValueCallback) {
        mRPValueCallback = rpValueCallback;
        String mockUrl = "http://rpv2.yunzhanghu.com/api/sign?duid=" + userId;
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mockUrl, this, this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        mRPValueCallback.onError(volleyError.getMessage(), volleyError.toString());
    }

    @Override
    public void onResponse(org.json.JSONObject jsonObject) {
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
            } catch (JSONException e) {
                e.printStackTrace();
                mRPValueCallback.onError(e.getMessage(), e.getMessage());
            }

        } else {
            mRPValueCallback.onError("", "sign data is  null");
        }
    }

}
