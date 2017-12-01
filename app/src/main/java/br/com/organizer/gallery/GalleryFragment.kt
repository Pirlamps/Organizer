package br.com.organizer.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.organizer.R

class GalleryFragment : Fragment() {

    companion object {

        fun newInstance(): GalleryFragment {
            val args = Bundle()
            val fragment = GalleryFragment()

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_gallery, container, false)

        return view
    }
}