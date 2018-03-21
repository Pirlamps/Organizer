package br.com.organizer

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class MainPagerAdapter(fm: FragmentManager,val fragmentList: List<Fragment>) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int{
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }


}