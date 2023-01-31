package com.example.collage_special.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.collage_special.R
import com.example.collage_special.adapter.GalleryAlbumAdapter
import com.example.collage_special.adapter.GalleryAlbumRecyclerAdapter
import com.example.collage_special.model.GalleryAlbum
import kotlinx.android.synthetic.main.fragment_gallery_album.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryAlbumFragment(context: Context) : Fragment() {

    lateinit var mAlbums: ArrayList<GalleryAlbum>
    var mContext: Context = context
    lateinit var mAdapter: GalleryAlbumRecyclerAdapter
    lateinit var fragment_view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragment_view =
            LayoutInflater.from(mContext).inflate(R.layout.fragment_gallery_album, container, false)




        lifecycleScope.launch(Dispatchers.IO) {
            this.launch(Dispatchers.Main){
                fragment_view.progressBar.visibility = View.VISIBLE
            }


            mAlbums = loadPhotoAlbums()

            lifecycleScope.launch(Dispatchers.Main) {
                fragment_view.progressBar.visibility = View.GONE

                mAdapter = GalleryAlbumRecyclerAdapter(
                    mContext,
                    mAlbums,
                    object : GalleryAlbumAdapter.OnGalleryAlbumClickListener {
                        override fun onGalleryAlbumClick(galleryAlbum: GalleryAlbum?) {

                            val bundle = Bundle()
                            bundle.putStringArrayList(
                                GalleryAlbumImageFragment.ALBUM_IMAGE_EXTRA,
                                galleryAlbum!!.mImageList as java.util.ArrayList<String>
                            )

                            bundle.putString(
                                GalleryAlbumImageFragment.ALBUM_NAME_EXTRA,
                                galleryAlbum.mAlbumName
                            )

                            val galleryalbumImageFragment = GalleryAlbumImageFragment()
                            galleryalbumImageFragment.arguments = bundle

                            val fragmentTransaction =
                                activity!!.supportFragmentManager.beginTransaction()
                            fragmentTransaction.replace(
                                R.id.frame_container,
                                galleryalbumImageFragment
                            )
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }
                    })

                mAdapter.notifyDataSetChanged()
                fragment_view.listView.layoutManager =
                    GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false)
                fragment_view.listView.adapter = mAdapter
            }
        }
        return fragment_view
    }

    @SuppressLint("Range")
    fun loadPhotoAlbums(): ArrayList<GalleryAlbum> {

        val r0 = LinkedHashMap<Long, GalleryAlbum>()
        val r4: Array<String> =
            arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN)

        val r3: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val r2: Cursor? = mContext.contentResolver.query(r3, null, null, null, "date_added DESC")

        val r2: Cursor? = mContext.contentResolver.query(r3, r4, null, null, null)

        val arrayList: ArrayList<GalleryAlbum> = java.util.ArrayList<GalleryAlbum>()
        if (r2 != null) {
            if (r2.moveToFirst()) {

                do {
                    val id =  r2.getString(r2.getColumnIndex(MediaStore.Images.Media._ID))
                    val r5: String = r2.getString(r2.getColumnIndex(MediaStore.Images.Media.DATA))
                    val r6: Long = r2.getLong(r2.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                    val r1: String? = r2.getString(r2.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val r3: Long = r2.getLong(r2.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))


                    Log.e("Path_Folders", r1.toString())

                    var r8: GalleryAlbum? = r0[r6]
                    if (r8 == null) {

                        if(r1 != null){
                            r8 = GalleryAlbum(r6, r1)
                            r8.mTakenDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(r3)
                            r8.mImageList.add(r5)
                            r0[r6] = r8
                        }

                    }else{
                        r8.mImageList.add(r5)
                    }

                } while (r2.moveToNext())
                arrayList.addAll(r0.values)
            }
        }
        r2!!.close()

        return arrayList
    }

}
