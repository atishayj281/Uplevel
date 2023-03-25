package android.example.uptoskills.Adapters;

import android.content.Context;
import android.example.uptoskills.R;
import android.example.uptoskills.models.SliderData
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList

class SliderAdapter(private val context: Context) : PagerAdapter() {

    private lateinit var layoutInflater: LayoutInflater

    //-Arrays
    var slide_images = arrayListOf<Int>(
            R.drawable.intro_img1,
            R.drawable.intro_img2,
            R.drawable.intro_img2
    );

    var slide_headings = arrayListOf<String>(
            "Finding Job have never been easier",
            "Read Blogs",
            "Make Connections"
    );

    var slide_description = arrayListOf<String>(
            "Look for the jobs you love and you will never have to work a day in your life",
            "Read relevant blogs and upgrade yourself to the current market trends and relevant skills",
            "Connect with people in your college and keep yourself updated"
    );

    override fun getCount(): Int {
        return slide_description.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as? RelativeLayout
    }

    var pos = 0
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(this.context) as LayoutInflater
        pos = position

        var view = layoutInflater.inflate(R.layout.slide_layout, container, false)
        var slideImage = view.findViewById<ImageView>(R.id.imageView)
        var slideHeading = view.findViewById<TextView>(R.id.heading)
        var slideDescription = view.findViewById<TextView>(R.id.description)

        slideImage.setImageResource(slide_images[position])
        slideHeading.text = slide_headings[position]
        slideDescription.text = slide_description[position]

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as RelativeLayout)

    }

    fun getCurrentPage(): Int{
        return pos
    }

}
