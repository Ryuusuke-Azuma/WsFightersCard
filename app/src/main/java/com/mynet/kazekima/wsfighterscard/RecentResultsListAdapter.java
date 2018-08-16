/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mynet.kazekima.wsfighterscard.data.GameEntity;

public class RecentResultsListAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    class ViewHolder {
        TextView mgGameName;
        TextView mGameDate;
        TextView mBattleDeck;
        TextView mMemo;
    }

    public RecentResultsListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final View view = mInflater.inflate(R.layout.list_item, null);

        ViewHolder holder = new ViewHolder();
        holder.mgGameName = (TextView) view.findViewById(R.id.game_name);
        holder.mGameDate = (TextView) view.findViewById(R.id.game_date);
        holder.mBattleDeck = (TextView) view.findViewById(R.id.battle_deck);
        holder.mMemo = (TextView) view.findViewById(R.id.memo);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        GameEntity entity = GameEntity.toEntity(cursor);
        holder.mgGameName.setText(entity.getGameName());
        holder.mGameDate.setText(entity.getGameDate());
        holder.mBattleDeck.setText(entity.getBattleDeck());
        holder.mMemo.setText(entity.getMemo());
    }
}
