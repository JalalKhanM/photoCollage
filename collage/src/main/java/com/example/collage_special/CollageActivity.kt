package com.example.collage_special


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.scale
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collage_special.adapter.BackgroundAdapter
import com.example.collage_special.adapter.FilterNameAdapter
import com.example.collage_special.adapter.FrameAdapter
import com.example.collage_special.frame.FramePhotoLayout
import com.example.collage_special.model.FilterData
import com.example.collage_special.model.TemplateItem
import com.example.collage_special.multitouch.PhotoView
import com.example.collage_special.utils.FrameImageUtils
import com.example.collage_special.utils.ImageUtils
import com.jaeger.library.StatusBarUtil
import com.mobi.collage.R
import kotlinx.android.synthetic.main.activity_collage.*
import kotlinx.android.synthetic.main.activity_collage.view.*
import kotlinx.android.synthetic.main.activity_filter_collage.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.Locale.filter


class CollageActivity : AppCompatActivity(), View.OnClickListener,
    FrameAdapter.OnFrameClickListener, BackgroundAdapter.OnBGClickListener {

    private var isPhotoMode = true
    private val photoId = View.generateViewId()
    private val photoFramId = View.generateViewId()

    var mFramePhotoLayout: FramePhotoLayout? = null
    var DEFAULT_SPACE: Float = 0.0f
    var MAX_SPACE: Float = 0.0f
    var MAX_CORNER: Float = 0.0f

    protected val RATIO_SQUARE = 0
    protected val RATIO_GOLDEN = 2

    private var mSpace = DEFAULT_SPACE
    private var mCorner = 0f
    val MAX_SPACE_PROGRESS = 300.0f
    val MAX_CORNER_PROGRESS = 200.0f
    private var mBackgroundColor = Color.WHITE
    private var mBackgroundImage: Bitmap? = null
    private var mBackgroundUri: Uri? = null
    private var mSavedInstanceState: Bundle? = null
    protected var mLayoutRatio = RATIO_SQUARE
    protected lateinit var mPhotoView: PhotoView
    protected var mOutputScale = 1f
    protected var mSelectedTemplateItem: TemplateItem? = null
    private var mImageInTemplateCount = 0
    protected var mTemplateItemList: ArrayList<TemplateItem>? = ArrayList()
    protected var mSelectedPhotoPaths: MutableList<String> = ArrayList()

    lateinit var frameAdapter: FrameAdapter
    lateinit var img_background: ImageView
    lateinit var collagePic: ImageView

    private var mLastClickTime: Long = 0
    fun checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    override fun onBGClick(drawable: Drawable) {

        val bmp = mFramePhotoLayout!!.createImage()
        val bitmap = (drawable as BitmapDrawable).bitmap
        mBackgroundImage = AndroidUtils.resizeImageToNewSize(bitmap, bmp.width, bmp.height)

//        img_background.background = BitmapDrawable(resources, mBackgroundImage)
//        img_background.setImageBitmap(mBackgroundImage)

    }

    override fun onFrameClick(templateItem: TemplateItem) {

        mSelectedTemplateItem!!.isSelected = false

        for (idx in 0 until mSelectedTemplateItem!!.photoItemList.size) {
            val photoItem = mSelectedTemplateItem!!.photoItemList[idx]
            if (photoItem.imagePath != null && photoItem.imagePath!!.length > 0) {
                if (idx < mSelectedPhotoPaths.size) {
                    mSelectedPhotoPaths.add(idx, photoItem.imagePath!!)
                } else {
                    mSelectedPhotoPaths.add(photoItem.imagePath!!)
                }
            }
        }

        val size = Math.min(mSelectedPhotoPaths.size, templateItem.photoItemList.size)
        for (idx in 0 until size) {
            val photoItem = templateItem.photoItemList.get(idx)
            if (photoItem.imagePath == null || photoItem.imagePath!!.length < 1) {
                photoItem.imagePath = mSelectedPhotoPaths[idx]
            }
        }

        mSelectedTemplateItem = templateItem
        mSelectedTemplateItem!!.isSelected = true
        buildLayout(templateItem)
    }

    inner class space_listener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            mSpace = MAX_SPACE * seekBar!!.getProgress() / MAX_SPACE_PROGRESS
            if (mFramePhotoLayout != null)
                mFramePhotoLayout!!.setSpace(mSpace, mCorner)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }

    inner class corner_listener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            mCorner = MAX_CORNER * seekBar!!.getProgress() / MAX_CORNER_PROGRESS
            if (mFramePhotoLayout != null)
                mFramePhotoLayout!!.setSpace(mSpace, mCorner)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.tab_layout -> {
                img_background.visibility = View.VISIBLE
                collagePic.visibility = View.GONE

                val list = rl_container.children
                var isExist = true

                run breaking@{
                    list.forEach {
                        if (it.id == photoFramId) {
                            isExist = true
                            return@breaking
                        } else {
                            isExist = false
                        }
                    }
                }
                isPhotoMode = true

                if (!isExist)
                    rl_container.addView(mFramePhotoLayout)


                tab_layout.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_header, null)
                tab_border.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                tab_bg.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                filter.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)

                tab_layoutText.setTextColor(Color.WHITE)
                tab_borderText.setTextColor(Color.GRAY)
                tab_bgText.setTextColor(Color.GRAY)
                filterText.setTextColor(Color.GRAY)

                ll_frame.visibility = View.VISIBLE
                ll_border.visibility = View.GONE
                ll_bg.visibility = View.GONE
                filterContainer.visibility = View.GONE
            }

            R.id.tab_border -> {
                img_background.visibility = View.VISIBLE
                collagePic.visibility = View.GONE

                val list = rl_container.children
                var isExist = true

                run breaking@{
                    list.forEach {
                        if (it.id == photoFramId) {
                            isExist = true
                            return@breaking
                        } else {
                            isExist = false
                        }
                    }
                }
                isPhotoMode = true

                if (!isExist)
                    rl_container.addView(mFramePhotoLayout)


                tab_layout.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                tab_border.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_header, null)
                tab_bg.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                filter.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)

                tab_layoutText.setTextColor(Color.GRAY)
                tab_borderText.setTextColor(Color.WHITE)
                tab_bgText.setTextColor(Color.GRAY)
                filterText.setTextColor(Color.GRAY)

                ll_frame.visibility = View.GONE
                ll_border.visibility = View.VISIBLE
                ll_bg.visibility = View.GONE
                filterContainer.visibility = View.GONE
            }
            R.id.tab_bg -> {

                img_background.visibility = View.VISIBLE
                collagePic.visibility = View.GONE

                val list = rl_container.children
                var isExist = true

                run breaking@{
                    list.forEach {
                        if (it.id == photoFramId) {
                            isExist = true
                            return@breaking
                        } else {
                            isExist = false
                        }
                    }
                }
                isPhotoMode = true

                if (!isExist)
                    rl_container.addView(mFramePhotoLayout)



                tab_layout.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                tab_border.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                tab_bg.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_header, null)
                filter.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)

                tab_layoutText.setTextColor(Color.GRAY)
                tab_borderText.setTextColor(Color.GRAY)
                tab_bgText.setTextColor(Color.WHITE)
                filterText.setTextColor(Color.GRAY)

                ll_frame.visibility = View.GONE
                ll_border.visibility = View.GONE
                ll_bg.visibility = View.VISIBLE
                filterContainer.visibility = View.GONE

            }
            R.id.btn_next -> {

//                img_background.visibility = View.VISIBLE
//                collagePic.visibility = View.GONE
//                mPhotoView.visibility = View.VISIBLE
//                isPhotoMode = true
//
//                checkClick()
//
//                var outStream: FileOutputStream? = null
//
//                try {
//                    val collageBitmap = createOutputImage()
//                    saveBitmap(collageBitmap)
//                    Toast.makeText(this, "Image Saved, Successfully", Toast.LENGTH_LONG).show()
//                    finish()
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }

                var outStream: FileOutputStream? = null
                try {
                    val collageBitmap = createOutputImage()
                    outStream = FileOutputStream(File(cacheDir, "tempBMP"))
                    collageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                    outStream.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val intent = Intent(this, FilterCollageActivity::class.java)
                startActivity(intent)
            }
            else -> {
               /* bmp = createOutputImage()

                img_background.visibility = View.GONE
                collagePic.visibility = View.GONE

                isPhotoMode = false

                rl_container.removeView(mFramePhotoLayout)

                collagePic.setImageBitmap(bmp)

                tab_layout.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                tab_border.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                tab_bg.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.disable_bg, null)
                filter.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_header, null)

                tab_layoutText.setTextColor(Color.GRAY)
                tab_borderText.setTextColor(Color.GRAY)
                tab_bgText.setTextColor(Color.GRAY)
                filterText.setTextColor(Color.WHITE)

                ll_frame.visibility = View.GONE
                ll_border.visibility = View.GONE
                ll_bg.visibility = View.GONE
                filterContainer.visibility = View.VISIBLE*/

                      var outStream: FileOutputStream? = null
                      try {
                          val collageBitmap = createOutputImage()
                          outStream = FileOutputStream(File(cacheDir, "tempBMP"))
                          collageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                          outStream.close()
                      } catch (e: FileNotFoundException) {
                          e.printStackTrace()
                      } catch (e: IOException) {
                          e.printStackTrace()
                      }
                      val intent = Intent(this, FilterCollageActivity::class.java)
                      startActivity(intent)

            }
        }
    }

    fun saveBitmap(bitmap: Bitmap) {
        val mainDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "RosyEditor"
        )
        if (!mainDir.exists()) {
            if (mainDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : $mainDir")
        }
        val now = Date()
        val fileName = (now.time / 1000).toString() + ".png"

        val file = File(mainDir.absolutePath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()

            /*  savedImageUri = Uri.parse(file.path)*/

            MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtil.setTranslucent(this)
        setContentView(R.layout.activity_collage)

        DEFAULT_SPACE = ImageUtils.pxFromDp(this, 2F)
        MAX_SPACE = ImageUtils.pxFromDp(this, 30F)
        MAX_CORNER = ImageUtils.pxFromDp(this, 60F)

        mSpace = DEFAULT_SPACE

        if (savedInstanceState != null) {
            mSpace = savedInstanceState.getFloat("mSpace")
            mCorner = savedInstanceState.getFloat("mCorner")
            mSavedInstanceState = savedInstanceState
        }

        mImageInTemplateCount = intent.getIntExtra("imagesinTemplate", 0)
        val extraImagePaths = intent.getStringArrayListExtra("selectedImages")

        list_bg.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list_bg.adapter = BackgroundAdapter(this, this)

        tab_layout.setOnClickListener(this)
        tab_border.setOnClickListener(this)
        tab_bg.setOnClickListener(this)

        seekbar_space.setOnSeekBarChangeListener(space_listener())
        seekbar_corner.setOnSeekBarChangeListener(corner_listener())

        mPhotoView = PhotoView(this)
        mPhotoView.id = photoId

        rl_container.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    /* mOutputScale = ImageUtils.calculateOutputScaleFactor(
                         rl_container.width,
                         rl_container.height
                     )*/

                    Log.e("isPhotoMode "," value $isPhotoMode")

                    if (isPhotoMode)
                        buildLayout(mSelectedTemplateItem!!)
                    // remove listener
                    rl_container.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

        img_background = findViewById(R.id.img_background)
        collagePic = findViewById(R.id.collagePic)

        loadFrameImages()

        list_framess.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        frameAdapter = FrameAdapter(this, mTemplateItemList!!, this)
        list_framess.adapter = frameAdapter


        mSelectedTemplateItem = mTemplateItemList!![0]
        mSelectedTemplateItem!!.isSelected = true

        if (extraImagePaths != null) {
            val size =
                Math.min(extraImagePaths.size, mSelectedTemplateItem!!.photoItemList.size)
            for (i in 0 until size)
                mSelectedTemplateItem!!.photoItemList[i].imagePath = extraImagePaths[i]
        }


       /* list_filterstypes.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        var filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr1)
        list_filterstypes.adapter = filter_typeAdapter

        filter_namess.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val filter_nameAdapter = FilterNameAdapter(this, resources.getStringArray(R.array.filters))

        filter_nameAdapter.setOnFilterNameClick(object : FilterNameAdapter.FilterNameClickListener {
            override fun onItemClick(view: View, position: Int) {

                if (position == 0) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr1)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 1) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr2)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 2) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_duo)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 3) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_pink)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 4) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_fresh)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 5) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_euro)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 6) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_dark)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 7) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_ins)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 8) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_elegant)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 9) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_golden)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 10) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_tint)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 11) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_film)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 12) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_lomo)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 13) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_movie)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 14) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_retro)
                    list_filterstype.adapter = filter_typeAdapter
                } else if (position == 15) {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_bw)
                    list_filterstype.adapter = filter_typeAdapter
                } else {
                    filter_typeAdapter = FilterDetailAdapter(AndroidUtils.filter_clr1)
                    list_filterstype.adapter = filter_typeAdapter
                }
                filter_nameAdapter.notifyDataSetChanged()
                filter_typeAdapter.notifyDataSetChanged()
            }
        })
        filter_namess.adapter = filter_nameAdapter*/

        btn_next.setOnClickListener(this)
        filter.setOnClickListener(this)
    }

    private fun loadFrameImages() {
        val mAllTemplateItemList = ArrayList<TemplateItem>()

        mAllTemplateItemList.addAll(FrameImageUtils.loadFrameImages(this))

        mTemplateItemList = ArrayList<TemplateItem>()
        if (mImageInTemplateCount > 0) {
            for (item in mAllTemplateItemList)
                if (item.photoItemList.size === mImageInTemplateCount) {
                    mTemplateItemList!!.add(item)
                }
        } else {
            mTemplateItemList!!.addAll(mAllTemplateItemList)
        }
    }


    fun getScaledBitmap(bitmap: Bitmap) {
        val viewWidth = rl_container.width
        val viewHeight = rl_container.height

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels


        try {
            val bitmap2 = bitmap.scale(width, viewHeight, false)
            val bg = BitmapDrawable(this.resources, bitmap2)
            img_background.background = bg

        } catch (e: Exception) {
            Log.e("Bitmap Error", " error = ${e.message}")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putFloat("mSpace", mSpace)
        outState.putFloat("mCornerBar", mCorner)
        if (mFramePhotoLayout != null) {
            mFramePhotoLayout!!.saveInstanceState(outState)
        }

    }

    fun buildLayout(item: TemplateItem) {
        mFramePhotoLayout = FramePhotoLayout(this, item.photoItemList)
        mFramePhotoLayout!!.id = photoFramId

//        if (mBackgroundImage != null && !mBackgroundImage!!.isRecycled()) {
//            if (Build.VERSION.SDK_INT >= 16)
//                rl_container.setBackground(BitmapDrawable(resources, mBackgroundImage))
//            else
//                rl_container.setBackgroundDrawable(BitmapDrawable(resources, mBackgroundImage))
//        } else {
//            rl_container.setBackgroundColor(mBackgroundColor)
//        }

        var viewWidth = rl_container.width
        var viewHeight = rl_container.height

        if (mLayoutRatio === RATIO_SQUARE) {
            if (viewWidth > viewHeight) {
                viewWidth = viewHeight
            } else {
                viewHeight = viewWidth
            }
        } else if (mLayoutRatio === RATIO_GOLDEN) {
            val goldenRatio = 1.61803398875
            if (viewWidth <= viewHeight) {
                if (viewWidth * goldenRatio >= viewHeight) {
                    viewWidth = (viewHeight / goldenRatio).toInt()
                } else {
                    viewHeight = (viewWidth * goldenRatio).toInt()
                }
            } else if (viewHeight <= viewWidth) {
                if (viewHeight * goldenRatio >= viewWidth) {
                    viewHeight = (viewWidth / goldenRatio).toInt()
                } else {
                    viewWidth = (viewHeight * goldenRatio).toInt()
                }
            }
        }

        mOutputScale = ImageUtils.calculateOutputScaleFactor(viewWidth, viewHeight)


        mFramePhotoLayout!!.build(viewWidth, viewHeight, mOutputScale, mSpace, mCorner)
        if (mSavedInstanceState != null) {
            mFramePhotoLayout!!.restoreInstanceState(mSavedInstanceState!!)
            mSavedInstanceState = null
        }
        val params = RelativeLayout.LayoutParams(viewWidth, viewHeight)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        rl_container.removeAllViews()

        rl_container.removeView(img_background)
        rl_container.addView(img_background, params)

        rl_container.addView(mFramePhotoLayout, params)
        //add sticker view
        rl_container.removeView(mPhotoView)
        rl_container.addView(mPhotoView, params)
        //reset space and corner seek bars

        seekbar_space.progress = (MAX_SPACE_PROGRESS * mSpace / MAX_SPACE).toInt()
        seekbar_corner.progress = (MAX_CORNER_PROGRESS * mCorner / MAX_CORNER).toInt()
    }

    @Throws(OutOfMemoryError::class)
    fun createOutputImage(): Bitmap {
        try {
            var template = mFramePhotoLayout!!.createImage()
            val result =
                Bitmap.createBitmap(template!!.width, template.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            if (mBackgroundImage != null && !mBackgroundImage!!.isRecycled()) {
                canvas.drawBitmap(
                    mBackgroundImage!!,
                    Rect(0, 0, mBackgroundImage!!.getWidth(), mBackgroundImage!!.getHeight()),
                    Rect(0, 0, result.width, result.height),
                    paint
                )
            } else {
                canvas.drawColor(mBackgroundColor)
            }

            canvas.drawBitmap(template, 0f, 0f, paint)
            template.recycle()
            var stickers = mPhotoView.getImage(mOutputScale)
            canvas.drawBitmap(stickers!!, 0f, 0f, paint)
            stickers.recycle()
            stickers = null
            System.gc()
            return result
        } catch (error: OutOfMemoryError) {
            throw error
        }
    }


    val screenShot: Bitmap
        get() {
            val findViewById = findViewById<View>(R.id.img_collage)
            findViewById.background = null
            findViewById.destroyDrawingCache()
            findViewById.isDrawingCacheEnabled = true
            val createBitmap = Bitmap.createBitmap(findViewById.drawingCache)
            findViewById.isDrawingCacheEnabled = false
            val createBitmap2 = Bitmap.createBitmap(
                createBitmap.width,
                createBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            findViewById.draw(Canvas(createBitmap2))
            return createBitmap2
        }

    private var savedImageUri: Uri? = null
    lateinit var bmp: Bitmap

    companion object {
        var red: Float = 0F
        var green: Float = 0F
        var blue: Float = 0F
        var saturation: Float = 0F
    }


    inner class FilterDetailAdapter(filters: Array<FilterData>) :
        RecyclerView.Adapter<FilterDetailAdapter.FilterDetailHolder>() {
        var filterType = filters
        var selectedindex = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterDetailHolder {
            var view = LayoutInflater.from(this@CollageActivity)
                .inflate(R.layout.item_filter, parent, false)
            return FilterDetailHolder(view)
        }

        override fun getItemCount(): Int {
            return filterType.size
        }

        override fun onBindViewHolder(
            holder: FilterDetailHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {

            if (selectedindex == position) {
                holder.rl_filteritem.setBackgroundColor(resources.getColor(R.color.colorAAccent))
            } else {
                holder.rl_filteritem.setBackgroundColor(resources.getColor(R.color.transparent))
            }

            holder.thumbnail_filter.setImageResource(R.drawable.thumb_filter)

            FilterCollageActivity.red = filterType[position].red
            FilterCollageActivity.green = filterType[position].green
            FilterCollageActivity.blue = filterType[position].blue
            FilterCollageActivity.saturation = filterType[position].saturation

            var bitmap = Bitmap.createBitmap(
                bmp.getWidth(),
                bmp.getHeight(),
                Bitmap.Config.ARGB_8888
            )
            var canvas = Canvas(bitmap)

            var paint = Paint()
            var colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(FilterCollageActivity.saturation)

            var colorScale = ColorMatrix()
            colorScale.setScale(
                FilterCollageActivity.red,
                FilterCollageActivity.green,
                FilterCollageActivity.blue, 1F
            )
            colorMatrix.postConcat(colorScale)

            paint.setColorFilter(ColorMatrixColorFilter(colorMatrix))
            canvas.drawBitmap(bmp, 0F, 0F, paint)

            holder.thumbnail_filter.setImageBitmap(bitmap)

            holder.filterName.setText(filterType[position].text)

            holder.rl_filteritem.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    selectedindex = position

                    FilterCollageActivity.red = filterType[position].red
                    FilterCollageActivity.green = filterType[position].green
                    FilterCollageActivity.blue = filterType[position].blue
                    FilterCollageActivity.saturation = filterType[position].saturation

                    Async_Filter(
                        bmp,
                        collagePic
                    ).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        FilterCollageActivity.red,
                        FilterCollageActivity.green,
                        FilterCollageActivity.blue
                    )
                    notifyDataSetChanged()
                }
            })
        }

        inner class FilterDetailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var thumbnail_filter: ImageView
            var filterName: TextView
            var rl_filteritem: RelativeLayout

            init {
                thumbnail_filter = itemView.findViewById(R.id.thumbnail_filter)
                filterName = itemView.findViewById(R.id.filterName)
                rl_filteritem = itemView.findViewById(R.id.rl_filteritem)
            }
        }
    }

    class Async_Filter() : AsyncTask<Float, Void, Bitmap>() {

        lateinit var originalBitmap: Bitmap
        lateinit var imgMain: ImageView

        constructor(originalBitmap: Bitmap, imgMain: ImageView) : this() {
            this.originalBitmap = originalBitmap
            this.imgMain = imgMain
        }

        override fun doInBackground(vararg params: Float?): Bitmap {
            var r = params[0]
            var g = params[1]
            var b = params[2]

            var bitmap = Bitmap.createBitmap(
                this.originalBitmap.getWidth(),
                this.originalBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
            )
            var canvas = Canvas(bitmap)

            var paint = Paint()
            var colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(FilterCollageActivity.saturation)

            var colorScale = ColorMatrix()
            colorScale.setScale(r!!, g!!, b!!, 1F)
            colorMatrix.postConcat(colorScale)

            paint.setColorFilter(ColorMatrixColorFilter(colorMatrix))
            canvas.drawBitmap(this.originalBitmap, 0F, 0F, paint)

            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            this.imgMain.setImageBitmap(result)

        }
    }
}