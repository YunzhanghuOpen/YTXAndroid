package com.yuntongxun.ecdemo.ui.chatting.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;

/**
 * Created by ustc on 2016/6/24.
 */
public class RedPacketAckViewHolder  extends BaseHolder{

    public View chattingContent;
    public TextView tv_money_msg;
    /**
     * TextView that display IMessage description.
     */
    /**
     * @param type
     */
    public RedPacketAckViewHolder(int type) {
        super(type);

    }

    public BaseHolder initBaseHolder(View baseView , boolean receive) {
        super.initBaseHolder(baseView);

        chattingTime = (TextView) baseView.findViewById(R.id.chatting_time_tv);
        chattingUser = (TextView) baseView.findViewById(R.id.chatting_user_tv);
        checkBox = (CheckBox) baseView.findViewById(R.id.chatting_checkbox);
        chattingMaskView = baseView.findViewById(R.id.chatting_maskview);
        chattingContent = baseView.findViewById(R.id.chatting_content_area);
        tv_money_msg= (TextView) baseView.findViewById(R.id.tv_money_msg);

        if(receive) {
            type = 18;
            return this;
        }

//        uploadState = (ImageView) baseView.findViewById(R.id.chatting_state_iv);
//        progressBar = (ProgressBar) baseView.findViewById(R.id.uploading_pb);
        type = 19;
        return this;
    }

    /**
     *
     * @return
     */
    public TextView  getMoneyMsgTv() {
        if(tv_money_msg == null) {
            tv_money_msg = (TextView) getBaseView().findViewById(R.id.tv_money_greeting);
        }
        return tv_money_msg;
    }

    /**
     *
     * @return
     */
    public ProgressBar getUploadProgressBar() {
        if(progressBar == null) {
            progressBar = (ProgressBar) getBaseView().findViewById(R.id.uploading_pb);
        }
        return progressBar;
    }

}

