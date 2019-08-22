package ru.vvdev.imagepickerview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout

import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources

import java.util.ArrayList


/**
 * Created by alexanderklimov on 6/2/18.
 */

class ImageChooseLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), ImageAddAdapter.OnClickListenerDetail, ImageAddAdapter.OnLongClickListenerDetail, View.OnClickListener {

    internal var imageList: MutableList<Image> = ArrayList()

    internal lateinit var imageAddAdapter: ImageAddAdapter

    internal lateinit var add: RelativeLayout

    internal lateinit var llRoot: HorizontalScrollView

    internal var background: Int = 0
    internal var close: Int = 0


    val items: List<Image>
        get() = imageList

    init {
        init(attrs)
    }


    private fun init(attrs: AttributeSet?) {

        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.view_image_choose_layout, this, true)
        add = v.findViewById(R.id.add)
        llRoot = v.findViewById(R.id.ll_root)
        add.setOnClickListener(this)
        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.orientation = LinearLayout.HORIZONTAL
        val imageRv = v.findViewById<RecyclerView>(R.id.imageRv)
        imageRv.layoutManager = mLayoutManager
        imageRv.itemAnimator = null
        imageRv.isNestedScrollingEnabled = false

        imageRv.setHasFixedSize(true)

        val arr = context.obtainStyledAttributes(attrs, R.styleable.imgPickr)
        close = arr.getColor(R.styleable.imgPickr_close_btn_color, resources.getColor(R.color.colorPrimary))
        background = arr.getColor(R.styleable.imgPickr_backgroundView, Color.parseColor("#ffffff"))
        llRoot.setBackgroundColor(background)

        imageAddAdapter = ImageAddAdapter(this, this, close, background)
        imageRv.adapter = imageAddAdapter
        imageAddAdapter.setData(imageList)
        imageAddAdapter.reload()

    }


    @SuppressLint("CheckResult")
    fun addImage(context: Context) {
        RxImagePicker.with(context).requestImage(Sources.GALLERY).subscribe { uri ->
            imageList.add(Image(uri))
            imageAddAdapter.setData(imageList)
            imageAddAdapter.reload()
        }

    }

    override fun onClickDetail(v: View, position: Int) {
        if (v.id == R.id.close) {
            imageList.removeAt(position)
            imageAddAdapter.notifyItemRemoved(position)
            imageAddAdapter.reload()
        }

    }

    override fun onLongClick(v: View, position: Int) {

    }

    override fun onClick(view: View) {
        addImage(context)
    }
}