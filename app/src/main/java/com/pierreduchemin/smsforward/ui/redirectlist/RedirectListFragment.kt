package com.pierreduchemin.smsforward.ui.redirectlist

import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
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
import com.pierreduchemin.smsforward.ui.addredirect.AddRedirectActivity
import kotlinx.android.synthetic.main.redirect_fragment_list_empty.*
import kotlinx.android.synthetic.main.redirect_list_fragment.*
import kotlinx.android.synthetic.main.redirect_list_fragment_content.*

class RedirectListFragment : Fragment() {

    companion object {
        fun newInstance() = RedirectListFragment()
    }

    enum class SwitchState {
        JUSTENABLED,
        ENABLED,
        STOP
    }

    private object Flipper {
        const val EMPTY = 0
        const val CONTENT = 1
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
        fabAddRedirectEmpty.setOnClickListener {
            startActivity(Intent(requireActivity(), AddRedirectActivity::class.java))
        }
        fabAddRedirect.setOnClickListener {
            startActivity(Intent(requireActivity(), AddRedirectActivity::class.java))
        }
        swActivate.setOnClickListener {
            viewModel.onRedirectionToggled()
        }

        viewModel.ldButtonState.observe(requireActivity(), {
            if (it == SwitchState.JUSTENABLED) {
                vibrate()
            }
            setSwitchState(it)
        })
        viewModel.ldForwardsList.observe(requireActivity(), {
            setList(it)
        })
    }

    private fun setSwitchState(switchState: SwitchState) {
        when (switchState) {
            SwitchState.JUSTENABLED,
            SwitchState.ENABLED -> {
                swActivate.isEnabled = true
                swActivate.isChecked = true
                tvActivationMessage.text = getString(R.string.redirectlist_redirection_activated)
                tvActivationMessage.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.activatedGreen
                    )
                )
            }
            SwitchState.STOP -> {
                swActivate.isEnabled = true
                swActivate.isChecked = false
                tvActivationMessage.text = getString(R.string.redirectlist_redirection_deactivated)
                tvActivationMessage.setTextColor(
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
            viewFlipper.displayedChild = Flipper.EMPTY
            return
        }
        viewFlipper.displayedChild = Flipper.CONTENT

        // TODO update adapter instead of replacing it
        rvForwards.adapter = ForwardModelAdapter(forwardsList) { v ->
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
        }
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            (requireContext().getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(
                VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            (requireContext().getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(150)
        }
    }
}
