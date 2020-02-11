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
import app.actionmobile.phoneinfo.R

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

            }

        }


        pageViewModel.text.observe(this, Observer<String> {
            textView?.text = it
        })
        return root
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