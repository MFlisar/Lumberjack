package com.michaelflisar.lumberjack.view

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
import com.michaelflisar.lumberjack.getColorFromAttr
import com.michaelflisar.lumberjack.core.Level
import com.michaelflisar.lumberjack.interfaces.IDataExtractor
import com.michaelflisar.lumberjack.isCurrentThemeDark
import com.michaelflisar.lumberjack.viewer.databinding.LogItemRowBinding
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller

internal class LogAdapter(
    context: Context,
    var items: List<IDataExtractor.Data>,
    var filter: String
) : RecyclerView.Adapter<LogAdapter.ViewHolder>(), RecyclerViewFastScroller.OnPopupTextUpdate {

    private val bgColor1: Int = if (context.isCurrentThemeDark()) Color.BLACK else Color.WHITE
    private val bgColor2: Int = if (context.isCurrentThemeDark()) Color.DKGRAY else Color.LTGRAY
    private val highlightColor: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) context.getColorFromAttr(android.R.attr.colorPrimary) else Color.BLUE
    private val textColor: Int = TextView(context).textColors.defaultColor

    fun clear() {
        update(emptyList(), "")
    }

    fun update(items: List<IDataExtractor.Data>, filter: String) {
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


        fun bind(item: IDataExtractor.Data, filter: String, pos: Int) {
            binding.tvNumber.text = "${item.line + 1}"
            binding.tvType.setTextColor(item.level.getTitleColor(textColor))
            binding.tvType.text = item.level.name
            binding.tvDate.text = item.date
            binding.tvRow.setTextColor(item.level.getTextColor(textColor))
            binding.tvRow.text = getHighlightedText(item.log, filter, true)
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

    // ---------------
    // FastScroller
    // ---------------

    override fun onChange(position: Int): CharSequence {
        return (position + 1).toString()
    }
}
