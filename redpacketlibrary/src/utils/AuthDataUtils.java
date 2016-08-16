/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;
import com.yunzhanghu.redpacketsdk.bean.TokenData;

public class AuthDataUtils {
    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "AuthData";
    private static SharedPreferences mSharedPreferences;
    private static AuthDataUtils mPreferenceManager;
    private static SharedPreferences.Editor editor;

    private AuthDataUtils(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        if (mPreferenceManager == null) {
            mPreferenceManager = new AuthDataUtils(cxt);
        }
    }

    /**
     * 单例模式，获取instance实例
     *
     * @param
     * @return
     */
    public synchronized static AuthDataUtils getInstance() {
        if (mPreferenceManager == null) {
            throw new RuntimeException("please init first!");
        }
        return mPreferenceManager;
    }


    public void setAuthData(JSONObject authData, String userId) {
        editor.putString(userId, authData.toJSONString());
        editor.commit();
    }

    public TokenData getTokenData(String userId) {
        JSONObject jsonObject;
        TokenData authData = new TokenData();
        String jsonStr = mSharedPreferences.getString(userId, null);
        if (jsonStr != null) {
            jsonObject = JSONObject.parseObject(jsonStr);
            if (jsonObject != null) {
                String partner = jsonObject.getString("partner");
                String user_id = jsonObject.getString("user_id");
                String timestamp = jsonObject.getString("timestamp");
                String sign = jsonObject.getString("sign");
                authData.appUserId = user_id;
                authData.timestamp = timestamp;
                authData.authPartner = partner;
                authData.authSign = sign;
            }
        }
        return authData;

    }


    public void removeCurrentUserInfo(String userId) {
        editor.remove(userId);
        editor.commit();
    }
}
