package android.example.uptoskills.Adapters;

import android.content.Context
import android.example.uptoskills.R
import android.example.uptoskills.models.SliderData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter


internal class SignInSlider(
    private val context: Context,
    val sliderDataArrayList: ArrayList<SliderData>
) :
    SliderViewAdapter<SignInSlider.SliderAdapterVH>() {


    private var mSliderItems: MutableList<SliderData> = ArrayList()
    get() = sliderDataArrayList
    fun renewItems(sliderItems: MutableList<SliderData>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderData) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.slider_layout, parent, false)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val (imageUrl) = mSliderItems[position]
        viewHolder.imageViewBackground.setImageDrawable(ContextCompat.getDrawable(context, imageUrl))
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    internal inner class SliderAdapterVH(itemView: View) :
        ViewHolder(itemView) {
        var imageViewBackground: ImageView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider)
        }
    }
}
