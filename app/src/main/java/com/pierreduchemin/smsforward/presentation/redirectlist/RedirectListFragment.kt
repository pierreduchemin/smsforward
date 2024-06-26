package com.pierreduchemin.smsforward.presentation.redirectlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.source.database.ForwardModel
import com.pierreduchemin.smsforward.databinding.RedirectListFragmentBinding
import com.pierreduchemin.smsforward.utils.SdkUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RedirectListFragment : Fragment() {

    sealed interface SwitchState {
        object JustEnabled: SwitchState
        object Enabled: SwitchState
        object Stopped: SwitchState
    }

    private object Flipper {
        //const val LOADING = 0
        const val EMPTY = 1
        const val CONTENT = 2
    }

    private lateinit var ui: RedirectListFragmentBinding
    private val viewModel: RedirectListViewModel by viewModels()
    private var lastSwitchState: SwitchState? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ui = RedirectListFragmentBinding.inflate(layoutInflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.vfContent.rvForwards.layoutManager = LinearLayoutManager(requireContext())
        ui.vfContent.rvForwards.adapter = ForwardModelAdapter { v ->
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.redirectlist_info_delete_title))
                .setMessage(getString(R.string.redirectlist_info_delete_content))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val forwardModel = v.tag as ForwardModel
                    viewModel.onDeleteConfirmed(forwardModel)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(R.drawable.ic_alert_24dp)
                .show()
        }
        ui.toolbar.ivHelp.isVisible = true

        val fabAction: (v: View) -> Unit = {
            startAddRedirect()
        }
        ui.vfEmpty.fabAddRedirectEmpty.setOnClickListener(fabAction)
        ui.vfContent.fabAddRedirect.setOnClickListener(fabAction)
        ui.vfContent.swActivate.setOnClickListener {
            viewModel.onRedirectionToggled()
        }
        viewModel.ldButtonState.observe(requireActivity()) {
            if (lastSwitchState != it) {
                lastSwitchState = it
                setSwitchState(it)
            }
        }
        viewModel.ldForwardsList.observe(requireActivity()) {
            setList(it)
        }
        ui.toolbar.ivHelp.setOnClickListener {
            startAbout()
        }
    }

    private fun startAddRedirect() {
        findNavController().navigate(R.id.action_redirectListFragment_to_addRedirectFragment)
    }

    private fun startAbout() {
        findNavController().navigate(R.id.action_redirectListFragment_to_aboutActivity)
    }

    private fun setSwitchState(switchState: SwitchState) {
        if (switchState == SwitchState.JustEnabled) {
            SdkUtils.vibrate(requireContext())
        }
        when (switchState) {
            SwitchState.JustEnabled,
            SwitchState.Enabled -> {
                ui.vfContent.swActivate.isEnabled = true
                ui.vfContent.swActivate.isChecked = true
                ui.vfContent.tvActivationMessage.text =
                    getString(R.string.redirectlist_redirection_activated)
                ui.vfContent.tvActivationMessage.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.activatedGreen
                    )
                )
            }

            SwitchState.Stopped -> {
                ui.vfContent.swActivate.isEnabled = true
                ui.vfContent.swActivate.isChecked = false
                ui.vfContent.tvActivationMessage.text =
                    getString(R.string.redirectlist_redirection_deactivated)
                ui.vfContent.tvActivationMessage.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.deactivatedRed
                    )
                )
            }
        }
    }

    private fun setList(forwardsList: List<ForwardModel>) {
        if (forwardsList.isEmpty()) {
            ui.viewFlipper.displayedChild = Flipper.EMPTY
            return
        }
        ui.viewFlipper.displayedChild = Flipper.CONTENT
        val adapter = ui.vfContent.rvForwards.adapter as ForwardModelAdapter
        adapter.setData(forwardsList)
    }
}
