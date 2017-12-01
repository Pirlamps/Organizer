package br.com.organizer.program

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.organizer.R

class ProgramFragment: Fragment() {

    companion object {

        fun newInstance(): ProgramFragment {
            val args = Bundle()
            val fragment = ProgramFragment()

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_program, container, false)

        return view
    }
}