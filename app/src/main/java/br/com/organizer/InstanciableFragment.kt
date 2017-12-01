package br.com.organizer

import android.support.v4.app.Fragment

interface InstanciableFragment {

    fun newInstance(): Fragment
}