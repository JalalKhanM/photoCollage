package com.example.collage_special.utils

import android.graphics.PointF

import com.example.collage_special.model.TemplateItem
import com.example.collage_special.template.PhotoItem

/**
 * Created by admin on 7/4/2016.
 */
object SixteenFrameImage {

    internal fun collage_16_1(): TemplateItem {
        val item = FrameImageUtils.collage("collage_16_1.png")
        //first frame
        var photoItem = PhotoItem()
        photoItem.index = 0
        photoItem.bound.set(0f, 0f, 1f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //second frame
        photoItem = PhotoItem()
        photoItem.index = 1
        photoItem.bound.set(0.25f, 0f, 0.75f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //third frame
        photoItem = PhotoItem()
        photoItem.index = 2
        photoItem.bound.set(0.5f, 0f, 0.5f, 0.3333f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //fourth frame
        photoItem = PhotoItem()
        photoItem.index = 3
        photoItem.bound.set(0f, 0.3333f, 0.25f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //fifth frame
        photoItem = PhotoItem()
        photoItem.index = 4
        photoItem.bound.set(0.25f, 0.3333f, 0.75f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //sixth frame
        photoItem = PhotoItem()
        photoItem.index = 5
        photoItem.bound.set(0.5f, 0.3333f, 0.5f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //seventh frame
        photoItem = PhotoItem()
        photoItem.index = 6
        photoItem.bound.set(0.75f, 0.3333f, 0.25f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //eighth frame
        photoItem = PhotoItem()
        photoItem.index = 7
        photoItem.bound.set(1f, 0.6666f, 0f, 0.6666f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //ninth frame
        photoItem = PhotoItem()
        photoItem.index = 8
        photoItem.bound.set(0f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //tenth frame
        photoItem = PhotoItem()
        photoItem.index = 9
        photoItem.bound.set(0.25f, 0.6666f, 0.75f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)

        //eleventh frame
        photoItem = PhotoItem()
        photoItem.index = 10
        photoItem.bound.set(0.5f, 0.6666f, 0.5f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)

        //twelve frame
        photoItem = PhotoItem()
        photoItem.index = 11
        photoItem.bound.set(0.75f, 0.6666f, 0.25f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)

        //thirteen frame
        photoItem = PhotoItem()
        photoItem.index = 12
        photoItem.bound.set(1f, 0.6666f, 0f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //fourteen frame
        photoItem = PhotoItem()
        photoItem.index = 13
        photoItem.bound.set(0.75f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //fifteen frame
        photoItem = PhotoItem()
        photoItem.index = 14
        photoItem.bound.set(0.75f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)
        //sixteen frame
        photoItem = PhotoItem()
        photoItem.index = 15
        photoItem.bound.set(0.75f, 0.6666f, 1f, 1f)
        photoItem.pointList.add(PointF(0f, 0f))
        photoItem.pointList.add(PointF(1f, 0f))
        photoItem.pointList.add(PointF(1f, 1f))
        photoItem.pointList.add(PointF(0f, 1f))
        item.photoItemList.add(photoItem)

        return item
    }


}
