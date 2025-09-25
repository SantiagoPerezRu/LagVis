package com.example.lagvis_v1.ui.xml

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lagvis_v1.R
// 👇 Usa los modelos del paquete XML, no los de compose
import com.example.lagvis_v1.ui.xml.FeatureId
import com.example.lagvis_v1.ui.xml.FeatureItem
// Si Palette está en este mismo paquete, este import es opcional
// import com.example.lagvis_v1.ui.xml.Palette

private const val TYPE_HEADER = 0
private const val TYPE_ITEM = 1

class HomeAdapter(
    private val userName: String,
    private val items: List<FeatureItem>,
    private val onFeatureClick: (FeatureId) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = 1 + items.size
    override fun getItemViewType(position: Int): Int = if (position == 0) TYPE_HEADER else TYPE_ITEM
    fun isHeader(position: Int) = position == 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderVH(inflater.inflate(R.layout.item_home_header, parent, false))
        } else {
            ItemVH(inflater.inflate(R.layout.item_feature_card, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            (holder as HeaderVH).bind(userName)
        } else {
            val item = items[position - 1]
            (holder as ItemVH).bind(item, onFeatureClick)
        }
    }

    class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUser: TextView = view.findViewById(R.id.tvUserName)
        fun bind(userName: String) { tvUser.text = userName }
    }

    class ItemVH(view: View) : RecyclerView.ViewHolder(view) {
        private val card: View = view.findViewById(R.id.cardFeature)
        private val strip: View = view.findViewById(R.id.strip)
        private val iconHolder: FrameLayout = view.findViewById(R.id.iconHolder)
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = view.findViewById(R.id.tvDesc)

        fun bind(model: FeatureItem, onClick: (FeatureId) -> Unit) {
            tvTitle.text = model.title
            tvDesc.text = model.description
            ivIcon.setImageResource(model.iconRes) // <-- requiere FeatureItem (XML) con Int

            when (model.palette) {
                Palette.PRIMARY -> {
                    strip.setBackgroundResource(R.drawable.bg_strip_primary)
                    iconHolder.setBackgroundResource(R.drawable.bg_icon_gradient_primary)
                }
                Palette.SECONDARY -> {
                    strip.setBackgroundResource(R.drawable.bg_strip_secondary)
                    iconHolder.setBackgroundResource(R.drawable.bg_icon_gradient_secondary)
                }
                Palette.TERTIARY -> {
                    strip.setBackgroundResource(R.drawable.bg_strip_tertiary)
                    iconHolder.setBackgroundResource(R.drawable.bg_icon_gradient_tertiary)
                }
                Palette.MIXED -> {
                    strip.setBackgroundResource(R.drawable.bg_strip_mixed)
                    iconHolder.setBackgroundResource(R.drawable.bg_icon_gradient_mixed)
                }
            }

            card.setOnClickListener { onClick(model.id) }
        }
    }
}
