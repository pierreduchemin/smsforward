package com.pierreduchemin.smsforward.ui.redirectlist

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.databinding.RedirectListFragmentBinding
import com.pierreduchemin.smsforward.ui.addredirect.AddRedirectActivity

class RedirectListFragment : Fragment() {

    companion object {
        fun newInstance() = RedirectListFragment()
    }

    enum class SwitchState {
        JUST_ENABLED,
        ENABLED,
        STOP
    }

    private object Flipper {
        const val EMPTY = 0
        const val CONTENT = 1
    }

    private lateinit var ui: RedirectListFragmentBinding
    private lateinit var viewModel: RedirectListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ui = RedirectListFragmentBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[RedirectListViewModel::class.java]
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.vfContent.rvForwards.layoutManager = LinearLayoutManager(requireContext())
        val fabAction: (v: View) -> Unit = {
            startActivity(Intent(requireActivity(), AddRedirectActivity::class.java))
        }
        ui.vfEmpty.fabAddRedirectEmpty.setOnClickListener(fabAction)
        ui.vfContent.fabAddRedirect.setOnClickListener(fabAction)
        ui.vfContent.swActivate.setOnClickListener {
            viewModel.onRedirectionToggled()
        }

        viewModel.ldButtonState.observe(requireActivity()) {
            if (it == SwitchState.JUST_ENABLED) {
                vibrate()
            }
            setSwitchState(it)
        }
        viewModel.ldForwardsList.observe(requireActivity()) {
            setList(it)
        }
    }

    private fun setSwitchState(switchState: SwitchState) {
        when (switchState) {
            SwitchState.JUST_ENABLED,
            SwitchState.ENABLED -> {
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
            SwitchState.STOP -> {
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

        // TODO update adapter instead of replacing it
        ui.vfContent.rvForwards.adapter = ForwardModelAdapter(forwardsList) { v ->
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
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val vibratorManager =
                    requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val systemService = requireContext().getSystemService(VIBRATOR_SERVICE) ?: return
                (systemService as Vibrator).vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
            else -> {
                val systemService = requireContext().getSystemService(VIBRATOR_SERVICE) ?: return
                (systemService as Vibrator).vibrate(50)
            }
        }
    }
}
