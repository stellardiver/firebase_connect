package com.smh.fbconnect.utils.extensions

import android.content.Context
import android.graphics.drawable.ClipDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setBottomDivider(context: Context, drawableId: Int) {
    DividerItemDecoration(context, ClipDrawable.HORIZONTAL).also { decoration ->
        ContextCompat.getDrawable(
            context,
            drawableId
        )?.let { drawable ->
            decoration.setDrawable(drawable)
        }
        if (this.itemDecorationCount == 0) this.addItemDecoration(decoration)
    }
}