package com.yuntongxun.ecdemo.ui.chatting.model;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.ui.chatting.ChattingActivity;
import com.yuntongxun.ecdemo.ui.chatting.RedPackUtils.CheckRedPacketMessageUtil;
import com.yuntongxun.ecdemo.ui.chatting.holder.BaseHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.DescriptionViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.RedPacketViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECCallMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import utils.RedPacketConstant;

/**
 * Created by ustc on 2016/6/24.
 */
public class RedPacketRxRow extends BaseChattingRow {


    public RedPacketRxRow(int type) {
        super(type);
    }

    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_redpacket_from);
            //use the view holder pattern to save of already looked up subviews
            RedPacketViewHolder holder = new RedPacketViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, true));
        }
        return convertView;
    }

    @Override
    public void buildChattingData(final Context context, BaseHolder baseHolder,
                                  ECMessage detail, int position) {

        RedPacketViewHolder holder = (RedPacketViewHolder) baseHolder;
        ECMessage message = detail;
        if (message != null) {

            if (message.getType() == ECMessage.Type.TXT) {
                JSONObject jsonObject = CheckRedPacketMessageUtil.isRedPacketMessage(message);
                if (jsonObject != null) {
                    //清除文本框，和加载progressdialog
                    holder.getGreetingTv().setText(jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_GREETING));
                    holder.getSponsorNameTv().setText(jsonObject.getString(RedPacketConstant.EXTRA_SPONSOR_NAME));

                    ViewHolderTag holderTag = ViewHolderTag.createTag(message,
                            ViewHolderTag.TagType.TAG_IM_REDPACKET, position);
                    View.OnClickListener onClickListener = ((ChattingActivity) context).mChattingFragment.getChattingAdapter().getOnClickListener();
                    holder.getBubble().setTag(holderTag);
                    holder.getBubble().setOnClickListener(onClickListener);
                }

            }
        }


    }

    @Override
    public int getChatViewType() {

        return ChattingRowType.REDPACKET_ROW_RECEIVED.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, ECMessage detail) {

        return false;
    }
}
