package com.example.collage_special.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

import com.example.collage_special.model.TemplateItem
import com.example.collage_special.utils.PhotoUtils
import com.mobi.collage.R

class FrameAdapter(
    context: Context,
    imageList: ArrayList<TemplateItem>,
    frameClickListener: OnFrameClickListener
) : RecyclerView.Adapter<FrameAdapter.FrameHolder>() {


    var mContext = context
    var mImages = imageList

    var frameListener: OnFrameClickListener = frameClickListener

    var lastSelected = 0

    interface OnFrameClickListener {
        fun onFrameClick(templateItem: TemplateItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_frame, parent, false)

        return FrameHolder(view)
    }

    override fun getItemCount(): Int {
        return mImages.size
    }

    override fun onBindViewHolder(holder: FrameHolder, position: Int) {

//        holder.img_frame.setImageResource(mImages[position])
        PhotoUtils.loadImageWithGlide(mContext, holder.img_frame, mImages[position].preview)

        if (mImages[position].isSelected) {
            holder.ll_itemframe.setBackgroundColor(mContext.resources.getColor(R.color.main1))
        } else {
            holder.ll_itemframe.setBackgroundColor(mContext.resources.getColor(R.color.transparent))
        }

        holder.img_frame.setOnClickListener {
            frameListener.onFrameClick(mImages[position])

            notifyItemChanged(position)
            notifyItemChanged(lastSelected)
            lastSelected = position

        }
    }

    class FrameHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_frame: ImageView = itemView.findViewById(R.id.img_frame)

        var ll_itemframe: LinearLayout = itemView.findViewById(R.id.ll_itemframe)
    }
}