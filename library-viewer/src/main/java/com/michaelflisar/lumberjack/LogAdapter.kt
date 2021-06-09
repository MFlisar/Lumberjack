package com.michaelflisar.lumberjack

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.lumberjack.viewer.databinding.LogItemRowBinding

internal class LogAdapter(
    context: Context,
    var items: List<Item>,
    var filter: String
) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    private val bgColor1: Int = if (context.isCurrentThemeDark()) Color.BLACK else Color.WHITE
    private val bgColor2: Int = if (context.isCurrentThemeDark()) Color.DKGRAY else Color.LTGRAY
    private val highlightColor: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) context.getColorFromAttr(android.R.attr.colorPrimary) else Color.BLUE
    private val textColor: Int = TextView(context).textColors.defaultColor

    fun update(items: List<Item>, filter: String) {
        this.items = items
        this.filter = filter
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LogItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setBackgroundColor(if (viewType == 0) bgColor1 else bgColor2)
        return ViewHolder(binding, highlightColor, textColor)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, filter, position)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.unbind()
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: LogItemRowBinding,
        private val highlightColor: Int,
        private val textColor: Int
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Item, filter: String, pos: Int) {
            binding.tvNumber.text = "${item.row + 1}"
            binding.tvType.setTextColor(item.level.getTitleColor(textColor))
            binding.tvType.text = item.level.name
            binding.tvDate.text = item.date
            binding.tvRow.setTextColor(item.level.getTextColor(textColor))
            binding.tvRow.text = getHighlightedText(item.text, filter, true)
        }

        fun unbind() {
            binding.tvNumber.text = null
            binding.tvType.text = null
            binding.tvDate.text = null
            binding.tvRow.text = null
        }

        private fun getHighlightedText(
            text: String,
            search: String,
            ignoreCase: Boolean
        ): Spannable {
            if (text.isEmpty() || search.isEmpty())
                return text.toSpannable()

            val wordToSpan: Spannable = SpannableString(text)
            var ofe: Int = text.indexOf(search, 0, ignoreCase)
            var offset = 0
            while (offset < text.length && ofe != -1) {
                ofe = text.indexOf(search, offset, ignoreCase)
                if (ofe == -1) {
                    break
                }
                wordToSpan.setSpan(
                    ForegroundColorSpan(highlightColor),
                    ofe,
                    ofe + search.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                wordToSpan.setSpan(
                    StyleSpan(Typeface.BOLD),
                    ofe,
                    ofe + search.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                offset = ofe + search.length
            }
            return wordToSpan
        }


    }

    class Item(val row: Int, val text: String, val level: Level, val date: String?) {

        enum class Level(val level: Int, val useDefaultTextColor: Boolean, private val color: Int) {
            TRACE(0, true, -1),
            DEBUG(1, true, -1),
            INFO(2, true, -1),
            WARN(3, false, Color.parseColor("#FFA500") /* orange */),
            ERROR(4, false, Color.RED),
            UNKNOWN(-1, false, android.R.color.transparent)
            ;

            private var c: Int? = null
            private var c2: Int? = null

            fun getTitleColor(textColor: Int): Int {
                return if (useDefaultTextColor) textColor else color
            }

            fun getTextColor(textColor: Int): Int {
                val c = getTitleColor(textColor)
                return if (!useDefaultTextColor && c == android.R.color.transparent)
                    textColor
                else c
            }
        }
    }
}
