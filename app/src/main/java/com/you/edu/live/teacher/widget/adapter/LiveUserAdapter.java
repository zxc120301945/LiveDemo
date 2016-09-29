package com.you.edu.live.teacher.widget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.you.edu.live.teacher.R;
import com.you.edu.live.teacher.model.bean.User;
import com.you.edu.live.teacher.widget.GlideCircleTransform;

import java.util.List;

/**
 * 作者：XingRongJing on 2016/7/1.
 */
public class LiveUserAdapter extends RecyclerViewAdapter<User> {

    public LiveUserAdapter(List<User> list) {
        super(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_live_user_avator_item, parent, false);
        return new UserAvatorViewHolder(view);
    }

    @Override
    protected void onBindHeaderView(View headerView) {

    }

    @Override
    protected void onBindItemView(RecyclerView.ViewHolder holder, User item, int position) {
        UserAvatorViewHolder userAvatorViewHolder = (UserAvatorViewHolder) holder;
        Context ctx = userAvatorViewHolder.mIvUserAvator.getContext();
        Glide.with(ctx).load(item.getPhoto()).bitmapTransform(new GlideCircleTransform(ctx)).error(R.drawable.user_avator).placeholder(R.drawable.user_avator).into(userAvatorViewHolder.mIvUserAvator);
    }

    private static class UserAvatorViewHolder extends RecyclerView.ViewHolder {

        ImageView mIvUserAvator;

        public UserAvatorViewHolder(View itemView) {
            super(itemView);
            mIvUserAvator = (ImageView) itemView.findViewById(R.id.live_user_avator_item_avator);
        }
    }
}
