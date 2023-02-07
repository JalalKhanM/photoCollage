package com.example.collage_special.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collage_special.CollageActivity
import com.mobi.collage.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BackgroundAdapter(
    context: Context,
    bgClickListener: OnBGClickListener
) : RecyclerView.Adapter<BackgroundAdapter.BackgroundHolder>() {


    var mImages: Array<String>
    var mContext = context
    var bgListener: OnBGClickListener = bgClickListener
    var selectedindex = 0
    var lastSelected = 0

    init {
        mImages = mContext.assets.list("background") as Array<String>
    }

    interface OnBGClickListener {
        fun onBGClick(drawable: Drawable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_frame, parent, false)

        return BackgroundHolder(view)
    }

    override fun getItemCount(): Int {
        return mImages.size
    }

    override fun onBindViewHolder(
        holder: BackgroundHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {


        var drawable: Drawable? = null

        (mContext as (CollageActivity)).lifecycleScope.launch(Dispatchers.IO) {
            val inputStream = mContext.assets.open("background/" + mImages[position])
            drawable = Drawable.createFromStream(inputStream, null) as Drawable

            this.launch(Dispatchers.Main) {



                Glide.with(mContext).load(drawable).into(holder.img_frame)
            }
        }


//        holder.img_frame.setImageDrawable(drawable)

        if (selectedindex == position) {
            holder.ll_itemframe.setBackgroundColor(mContext.resources.getColor(R.color.main1))
        } else {
            holder.ll_itemframe.setBackgroundColor(mContext.resources.getColor(R.color.transparent))
        }

        holder.img_frame.setOnClickListener {
            selectedindex = position

            drawable?.let {
                bgListener.onBGClick(it)

                val bitmapDrawable = drawable as BitmapDrawable
                val myBitmap = bitmapDrawable.bitmap


                (mContext as CollageActivity).getScaledBitmap(myBitmap)
            }



            notifyItemChanged(lastSelected)
            notifyItemChanged(position)
            lastSelected = position
        }
    }

    class BackgroundHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_frame: ImageView = itemView.findViewById(R.id.img_frame)
        var ll_itemframe: LinearLayout = itemView.findViewById(R.id.ll_itemframe)
    }
}