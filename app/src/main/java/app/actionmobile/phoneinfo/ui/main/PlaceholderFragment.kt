package app.actionmobile.phoneinfo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.actionmobile.phoneinfo.Entry
import app.actionmobile.phoneinfo.EntryAdapter
import app.actionmobile.phoneinfo.R
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root : View? = null
        var textView: TextView? = null
        when (arguments?.getInt(ARG_SECTION_NUMBER)) {

            2,3 -> {
                root = inflater.inflate(R.layout.fragment_main, container, false)
                textView = root?.findViewById(R.id.section_label)
            }
            1 -> {
                root = inflater.inflate(R.layout.fragment_phone, container, false)
                val b: Button = root?.findViewById(R.id.alertbutton) as Button
                b.setOnClickListener {
                    Toast.makeText(it.getContext(),"alert", Toast.LENGTH_SHORT).show()
                }
                val EntryViewRecyclerView: RecyclerView =
                    root.findViewById(R.id.entryRecyclerView) as RecyclerView
                val manager: RecyclerView.LayoutManager = LinearLayoutManager(root.getContext())
                EntryViewRecyclerView.setLayoutManager(manager)
                var entryList = ArrayList<Entry>()

                var adapter = EntryAdapter(entryList)

                //getAllEntries(EntryViewRecyclerView)
                var entry : Entry = Entry()
                entry.title = "first one"
                for (x in 1..30){
                    entry = Entry()
                    entry.title = "new item : ${x}"
                    entryList.add(entry)
                }

                adapter.allEntries = entryList
                EntryViewRecyclerView.adapter = adapter;
            }
        }


        pageViewModel.text.observe(this, Observer<String> {
            textView?.text = it
        })
        return root
    }

    fun getAllEntries(EntryViewRecyclerView : RecyclerView){
        // clear it first so when one is added the list isn't doubled.

    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}