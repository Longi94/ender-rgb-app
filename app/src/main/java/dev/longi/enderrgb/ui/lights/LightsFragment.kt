package dev.longi.enderrgb.ui.lights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ActionMode
import com.skydoves.colorpickerview.listeners.ColorListener
import dev.longi.enderrgb.R
import kotlinx.android.synthetic.main.fragment_lights.*


class LightsFragment : Fragment() {

    private lateinit var viewModel: LightsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lights, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LightsViewModel::class.java)
        viewModel.error.observe(viewLifecycleOwner, { error ->
            Snackbar.make(constraintLayout, "Failed set color: $error", Snackbar.LENGTH_LONG).show()
        })
        viewModel.effects.observe(viewLifecycleOwner, { effects ->
        })

        buttonWhite.setOnClickListener { viewModel.setColor(255, 255, 255) }
        buttonOff.setOnClickListener { viewModel.off() }
        colorPicker.actionMode = ActionMode.LAST
        colorPicker.setColorListener(ColorListener() { color: Int, fromUser: Boolean ->
            if (fromUser) {
                viewModel.setColor(color)
            }
        })
        colorPicker.attachBrightnessSlider(brightnessSlide)

        viewModel.getEffects()
    }

}