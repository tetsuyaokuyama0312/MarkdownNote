package com.to.markdownnote

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * スワイプされた際に削除処理を実行する [ItemTouchHelper.SimpleCallback] の抽象実装。
 *
 * 実際の削除処理はサブクラスにて [onSwiped] をオーバーライドして実装する。
 *
 * @property context コンテキスト
 */
abstract class SwipeToDeleteCallback(
    private val context: Context
) :
    ItemTouchHelper.SimpleCallback(
        // 左スワイプのみ
        0,
        ItemTouchHelper.LEFT
    ) {

    companion object {
        private val BACKGROUND_COLOR = Color.parseColor("#f44336")
        private val CLEAR_PAINT =
            Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        // 途中でスワイプがキャンセルされた場合は元に戻す
        val cancelled = dX == 0f && !isCurrentlyActive
        if (cancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // 背景を描画
        drawBackground(c, itemView, dX)

        // アイコンを描画
        drawIcon(c, itemView)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, CLEAR_PAINT)
    }

    private fun drawBackground(
        c: Canvas,
        itemView: View,
        dX: Float
    ) {
        val background = ColorDrawable()
        background.color = BACKGROUND_COLOR
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)
    }

    private fun drawIcon(c: Canvas, itemView: View) {
        // 削除アイコンの描画位置を算出
        val deleteIcon = context.getDrawable(R.drawable.ic_delete)
            ?: throw IllegalStateException("deleteIcon not found")
        val itemHeight = itemView.bottom - itemView.top
        val deleteIconTop = itemView.top + (itemHeight - deleteIcon.intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - deleteIcon.intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - deleteIcon.intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + deleteIcon.intrinsicHeight

        // 描画
        deleteIcon.setBounds(
            deleteIconLeft,
            deleteIconTop,
            deleteIconRight,
            deleteIconBottom
        )
        deleteIcon.draw(c)
    }
}