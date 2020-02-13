package app.actionmobile.phoneinfo


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*


class EntryAdapter : RecyclerView.Adapter<EntryAdapter.ViewHolder?> {
    var allEntries: List<Entry> = ArrayList()

    constructor(entryList: List<Entry>) {
        allEntries = entryList

    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var entryTextView: TextView
        init {
            entryTextView =
                itemView.findViewById<View>(R.id.entryIdTextView) as TextView
            entryTextView.setOnLongClickListener { view ->
                Log.d("MainActivity", "entryTextView LongClick! : ${entryTextView.text}")
                Log.d("MainActivity", "got focus : ${view.title}")
                Toast.makeText(view.getContext(),"got focus : ${entryTextView.text}", Toast.LENGTH_SHORT).show()
//                Log.d(
//                    "MainActivity",
//                    "haptic is enabled : " + entryTextView.isHapticFeedbackEnabled
//                )
                entryTextView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                val initialText = entryTextView.text.toString()
                spannableStyle = SpannableString(initialText)
                textStyle = BackgroundColorSpan(Color.YELLOW)
                spannableStyle!!.setSpan(
                    textStyle,
                    0,
                    spannableStyle!!.length,
                    0
                )
                entryTextView.text = spannableStyle
//                Log.d("MainActivity", "copying to clipboard")
//                val clipboard =
//                    view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                val clip = ClipData.newPlainText(
//                    mConfig.getUserId(),
//                    mConfig.getUserId().toString() + ":" + entryTextView.tag
//                )
//                clipboard.setPrimaryClip(clip)
//                previousStyle = spannableStyle
//                val activity = entryTextView.context as MainActivity
//                activity.openTab(
//                    1,
//                    mConfig.getUserId().toString() + ":" + entryTextView.tag.toString()
//                )
                true
            }
            entryTextView.onFocusChangeListener =
                OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        previousText = entryTextView.text.toString()
                        Log.d(
                            "MainActivity",
                            "lost focus : " + v.id.toString() + " : " + previousText
                        )
                        previousStyle = SpannableString(previousText)
                        previousStyle!!.removeSpan(previousStyle)
                        entryTextView.text = previousStyle
                    }
                }
            //league = (TextView) itemView.findViewById(R.id.tvLeague);
//yearEstablished = (TextView) itemView.findViewById(R.id.tvYear);
        }
    }

    override
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return ViewHolder(view)
    }
    override
    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (allEntries!![position].title != null && !allEntries!![position].title.equals("")) {
            holder.entryTextView.setText(allEntries!![position].title)
        } else {
            holder.entryTextView.setText(allEntries!![position].title)
        }
        //holder.entryTextView.tag = allEntries!![position].get_id()
    }

    override
    fun  getItemCount() : Int {
        return allEntries!!.size
    }
//    val itemCount: Int
//        get() = allEntries!!.size

    companion object {
        var spannableStyle: SpannableString? = null
        var previousStyle: SpannableString? = null
        var previousText: String? = null
        var textStyle: BackgroundColorSpan? = null
    }
}
