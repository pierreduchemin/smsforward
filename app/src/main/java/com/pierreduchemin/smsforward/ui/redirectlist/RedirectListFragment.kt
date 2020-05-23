package com.pierreduchemin.smsforward.ui.redirectlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.ui.addredirect.AddRedirectActivity
import kotlinx.android.synthetic.main.redirect_list_fragment.*

class RedirectListFragment : Fragment() {

    companion object {
        private val TAG by lazy { RedirectListFragment::class.java.simpleName }
        fun newInstance() = RedirectListFragment()
    }

    enum class SwitchState {
        ENABLED,
        STOP
    }

    private lateinit var viewModel: RedirectListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(RedirectListViewModel::class.java)
        return inflater.inflate(R.layout.redirect_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvForwards.layoutManager = LinearLayoutManager(requireContext())
        fabAddRedirect.setOnClickListener {
            startActivity(Intent(requireActivity(), AddRedirectActivity::class.java))
        }
        swActivate.setOnClickListener {
            viewModel.onRedirectionToggled()
        }

        viewModel.buttonState.observe(requireActivity(), Observer {
            setSwitchState(it)
        })
        viewModel.forwardsList.observe(requireActivity(), Observer {
            setList(it)
        })
    }

    private fun setSwitchState(switchState: SwitchState) {
        when (switchState) {
            SwitchState.ENABLED -> {
                swActivate.isEnabled = true
                swActivate.isChecked = true
                tvActivationMessage.text = getString(R.string.redirectlist_redirection_activated)
                tvActivationMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.activatedGreen))
            }
            SwitchState.STOP -> {
                swActivate.isEnabled = true
                swActivate.isChecked = false
                tvActivationMessage.text = getString(R.string.redirectlist_redirection_deactivated)
                tvActivationMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.deactivatedRed))
            }
        }
    }

    private fun setList(forwardsList: List<ForwardModel>) {
        if (forwardsList.isEmpty()) {
            phEmpty.visibility = View.VISIBLE
            rvForwards.visibility = View.GONE
            llActivate.visibility = View.GONE
            return
        }

        phEmpty.visibility = View.GONE
        rvForwards.visibility = View.VISIBLE
        llActivate.visibility = View.VISIBLE

        // TODO update adapter instead of replacing it
        rvForwards.adapter = ForwardModelAdapter(forwardsList, View.OnClickListener { v ->
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.redirectlist_info_delete_title))
                .setMessage(getString(R.string.redirectlist_info_delete_content))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    val forwardModel = v.tag as ForwardModel
                    viewModel.onDeleteConfirmed(forwardModel)
                }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_alert)
                .show()
        })
    }
}
