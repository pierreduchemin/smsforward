package com.pierreduchemin.smsforward.ui.redirectlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierreduchemin.smsforward.R

class RedirectListFragment : Fragment() {

    companion object {
        fun newInstance() = RedirectListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.redirect_list_fragment, container, false)
    }

}
