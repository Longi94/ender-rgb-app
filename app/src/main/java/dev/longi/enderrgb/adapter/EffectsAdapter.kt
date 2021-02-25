package dev.longi.enderrgb.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.longi.enderrgb.R

class EffectsAdapter : RecyclerView.Adapter<EffectsAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "EffectsAdapter"
    }

    private var effects = listOf<String>()

    var listener: EffectSelectedListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val effectName: TextView = view.findViewById(R.id.effectName)
    }

    interface EffectSelectedListener {
        fun onEffectSelected(effectName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_effect, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val effectName = effects[position]
        holder.effectName.text = effectName
        holder.itemView.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: $effectName clicked")
            listener?.let { it.onEffectSelected(effectName) }
        }
    }

    override fun getItemCount() = effects.size

    fun setEffects(effects: List<String>) {
        this.effects = effects
        notifyDataSetChanged()
    }
}