package br.com.organizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.organizer.camera.CameraFragment
import br.com.organizer.camera.CameraFragment2
import br.com.organizer.gallery.GalleryFragment
import br.com.organizer.program.ProgramFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPager()
    }

    fun setupPager(){
        val asList = arrayListOf(GalleryFragment.newInstance(),
                CameraFragment2.newInstance(),
                ProgramFragment.newInstance())


        val pager = main_pager
        val pagerAdapter = MainPagerAdapter(supportFragmentManager,asList)

        pager.adapter = pagerAdapter
        pager.currentItem = 1
    }
}
