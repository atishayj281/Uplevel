package android.example.uptoskills.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class CourseViewPagerAdapter(fm: FragmentManager, behavior: Int):
    FragmentStatePagerAdapter(fm, behavior) {

    private var fragmentArrayList: ArrayList<Fragment> = ArrayList<Fragment>()
    private var fragmentTitle: ArrayList<String> = ArrayList<String>()



    override fun getCount(): Int {
        return fragmentArrayList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    fun addFragment(fragment: Fragment, title: String){
        fragmentArrayList.add(fragment)
        fragmentTitle.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }

    fun clearData(){
        fragmentArrayList.clear()
        fragmentTitle.clear()
    }
}