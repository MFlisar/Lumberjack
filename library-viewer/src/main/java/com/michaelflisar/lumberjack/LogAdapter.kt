package com.michaelflisar.lumberjack

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.lumberjack.viewer.databinding.LogItemRowBinding

internal class LogAdapter(
    var items: List<Item>,
    var filter: String
) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

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
        if (viewType == 1) {
            binding.root.setBackgroundColor(Color.LTGRAY)
        }
        return ViewHolder(binding)
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
        private val binding: LogItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val highlightColor by lazy {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                getColorFromAttr(android.R.attr.colorPrimary)
            } else {
                Color.BLUE
            }
        }

        fun bind(item: Item, filter: String, pos: Int) {
            binding.tvNumber.text = "${item.row + 1}"
            binding.tvRow.text = getHighlightedText(item.text, filter, true)
        }

        fun unbind() {
            binding.tvNumber.text = null
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

        @ColorInt
        private fun getColorFromAttr(
            @AttrRes attrColor: Int,
            typedValue: TypedValue = TypedValue(),
            resolveRefs: Boolean = true
        ): Int {
            binding.root.context.theme.resolveAttribute(attrColor, typedValue, resolveRefs)
            return typedValue.data
        }
    }

    class Item(val row: Int, val text: String)
}
