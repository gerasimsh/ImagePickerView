package ru.vvdev.imagepickerview


import android.content.res.Resources
import android.graphics.PorterDuff
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions

import java.util.ArrayList


class ImageAddAdapter(
    private val mClickListener: OnClickChooseImage,
    private var attr: ImageAttr,
    private val resources: Resources,
    private val openClick: OpenClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var imageList: MutableList<Image>? = ArrayList()


    fun setData(imageList: MutableList<Image>?) {
        if (imageList == null)
            return
        this.imageList = imageList
    }

    fun reload() {
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        imageList?.removeAt(position);
        notifyItemRemoved(position); }

    private fun reloadItem(position: Int) {
        notifyItemChanged(position)
    }


    fun getItem(i: Int): Image? {
        return if (imageList!!.size > i && i >= 0) {
            imageList!![i]
        } else {
            null
        }
    }


    override fun getItemId(i: Int): Long {
        return java.lang.Long.parseLong(getItem(i).toString())
    }

    fun changeItem(dialog: Image, position: Int) {
        if (position < imageList!!.size && position >= 0) {
            imageList!![position] = dialog
            reloadItem(position)
        }
    }

    override fun getItemCount(): Int {
        return if (imageList == null) 0 else imageList!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0 && attr.canAddPhoto) {
            AddHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_photo,
                    parent,
                    false
                )
            )
        } else {
            ViewHolderMy(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_photo_close,
                    parent,
                    false
                )
            )
        }


    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            when {
                position == 0 && attr.canAddPhoto -> (holder as AddHolder).bind(position, attr)
                else -> (holder as ViewHolderMy).bind(getItem(position), position, attr)

            }
        } catch (e: Exception) {

        }

    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    fun updateAttr(imageAttr: ImageAttr) {
        attr = imageAttr
    }

    interface OnClickChooseImage {
        fun onClickAdd(v: View, position: Int)
        fun onClickOpenImage(v: View, position: Int)
        fun onClickDeleteImage(v: View, position: Int)
        fun onLongClickImage(v: View, position: Int)
    }

    interface OpenClick {
        fun openClick(uri: Uri, position: Int)

    }


    inner class ViewHolderMy internal constructor(var view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {


        private var userAvatar: ImageView = view.findViewById<View>(R.id.image) as ImageView
        private var close: ImageView = view.findViewById<View>(R.id.close) as ImageView
        private var closeBack: ImageView = view.findViewById<View>(R.id.close_back) as ImageView
        internal var rootLay: ConstraintLayout =
            view.findViewById<View>(R.id.rootLayout) as ConstraintLayout
        private var imageCardView: CardView = view.findViewById<View>(R.id.imageCard) as CardView


        fun bind(
            dialog: Image?,
            position: Int,
            attr: ImageAttr
        ) {
            if (dialog == null)
                return
            Log.i("MyLog", "$dialog tyu")
            itemView.tag = position
            close.tag = position
            userAvatar.layoutParams.height = attr.viewHeight
            userAvatar.layoutParams.width = attr.viewWight
            imageCardView.radius = attr.cornerRadius

            if (attr.canDelete) {
                close.setOnClickListener(this)
                close.setColorFilter(attr.tintClose, PorterDuff.Mode.SRC_IN)
                close.visibility = View.VISIBLE
                closeBack.visibility = View.VISIBLE
            } else {
                close.visibility = View.GONE
                closeBack.visibility = View.INVISIBLE
            }
            closeBack.setColorFilter(attr.backClose, PorterDuff.Mode.MULTIPLY)
            Glide.with(view.context)
                .load(dialog.image)
                .apply(RequestOptions().transform(CenterCrop()))
                .into(userAvatar)
            imageCardView.setOnClickListener {
                mClickListener.onClickOpenImage(it, position)
                openClick.openClick(getItem(position)!!.image, position)
            }


        }

        override fun onClick(v: View) {
            val position = v.tag as Int
            mClickListener.onClickDeleteImage(v, position)
        }

        override fun onLongClick(v: View): Boolean {
            val position = v.tag as Int
            mClickListener.onLongClickImage(v, position)
            return true
        }
    }

    companion object {

        private val TYPE_HEADER = 2
        private val TYPE_ITEM = 1
    }

    inner class AddHolder internal constructor(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private var srcAdd: ImageView = view.findViewById<View>(R.id.camera) as ImageView
        internal var background: RelativeLayout =
            view.findViewById<View>(R.id.add) as RelativeLayout
        private var text: TextView = view.findViewById<View>(R.id.tv) as TextView
        private var imageCard: CardView = view.findViewById<View>(R.id.imageCard) as CardView
        private var rootLay: RelativeLayout =
            view.findViewById<View>(R.id.rootLayout) as RelativeLayout

        fun bind(position: Int, attr: ImageAttr) {
            rootLay.visibility = if (attr.canAddPhoto) View.VISIBLE else View.GONE

            imageCard.tag = position
            with(attr) {
                background.layoutParams.width = viewWight
                background.layoutParams.height = viewHeight
                background.setBackgroundColor(addAttr.imageBack)
                imageCard.radius = cornerRadius
                srcAdd.layoutParams.height = addAttr.addHeight
                srcAdd.layoutParams.width = addAttr.addWidth
                srcAdd.setImageDrawable(resources.getDrawable(addAttr.drawable))
                text.text = addAttr.text
                text.setTextColor(addAttr.textColor)
                text.textSize = addAttr.textSize
                text.setTypeface(null, addAttr.textStyle)
            }


            imageCard.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (attr.maxPhotos >= imageList!!.size || attr.maxPhotos == 0)
                mClickListener.onClickAdd(v, v.tag as Int)
            else
                Toast.makeText(v.context, attr.messageWhenMaxSize, Toast.LENGTH_LONG).show()
        }


    }
}
