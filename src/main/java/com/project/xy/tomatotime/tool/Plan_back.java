package com.project.xy.tomatotime.tool;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.project.xy.tomatotime.R;

public class Plan_back extends LinearLayout {
    public Plan_back(Context context, AttributeSet attrs) {
        super(context, (AttributeSet) attrs);
        LayoutInflater.from(context).inflate(R.layout.title_planback, this);

    }
}