package app.actionmobile.phoneinfo.ui.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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



                //getAllEntries(EntryViewRecyclerView)
                var entry : Entry = Entry()
                entry.title = "first one"
                for (x in 1..30){
                    entry = Entry()
                    entry.title = "new item : ${x}"
                    entryList.add(entry)
                }
                var entryAdapter = EntryAdapter(entryList)

                EntryViewRecyclerView.adapter = entryAdapter;
            }
            4->{
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

                getPhoneDetails(entryList);

                var entryAdapter = EntryAdapter(entryList)
                EntryViewRecyclerView.adapter = entryAdapter;
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

    fun requestAllPermissions(){
        val permission = Manifest.permission.READ_PHONE_STATE
        val permission2 = Manifest.permission.READ_SMS
        val grant = ContextCompat.checkSelfPermission(context!!, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(2)
            permission_list[0] = permission
            permission_list[1] = permission2
            ActivityCompat.requestPermissions(activity as Activity, permission_list, 2)
        }
    }

    fun getPhoneDetails(allEntries : ArrayList<Entry>){


        val tMgr =
            context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager as TelephonyManager?
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) { // TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
            //allEntries.add( Entry("That isn't going to work."))
            requestAllPermissions();
            return;
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
        }
        allEntries.add( Entry(tMgr?.line1Number ?: "failed lineNumber"))
        allEntries.add(Entry("MMS User agent: ${tMgr?.mmsUserAgent ?: "failed user agent"}" ));
        allEntries.add(Entry("Device software ver. - ${tMgr?.deviceSoftwareVersion ?: "dev software version"}" ))

        when (tMgr?.phoneType) {
            PHONE_TYPE_CDMA -> {
                allEntries.add(Entry("Phone type: CDMA"))
            }
            PHONE_TYPE_GSM -> {
                allEntries.add(Entry("Phone type: GSM"))
            }
            PHONE_TYPE_NONE -> {
                allEntries.add(Entry("Phone type: NONE"))
            }
            PHONE_TYPE_SIP -> {
                allEntries.add(Entry("Phone type: SIP"))
            }
        }

        when (tMgr?.simState) {
            SIM_STATE_UNKNOWN -> {
                allEntries.add(Entry("SIM State - UNKNOWN"))
            }
            SIM_STATE_ABSENT -> {
                allEntries.add(Entry("SIM State - ABSENT"))
            }
            SIM_STATE_PIN_REQUIRED -> {
                allEntries.add(Entry("SIM State - PIN REQUIRED"))
            }
            SIM_STATE_PUK_REQUIRED -> {
                allEntries.add(Entry("SIM State - PUK REQUIRED"))
            }
            SIM_STATE_NETWORK_LOCKED -> {
                allEntries.add(Entry("SIM State - NETWORK LOCKED"))
            }
            SIM_STATE_READY -> {
                allEntries.add(Entry("SIM State - READY"))
            }
            SIM_STATE_NOT_READY -> {
                allEntries.add(Entry("SIM State - NOT READY"))
            }
            SIM_STATE_PERM_DISABLED -> {
                allEntries.add(Entry("SIM State - PERM DISABLED"))
            }
            SIM_STATE_CARD_IO_ERROR -> {
                allEntries.add(Entry("SIM State - CARD IO ERROR"))
            }
            SIM_STATE_CARD_RESTRICTED -> {
                allEntries.add(Entry("SIM State - CARD RESTRICTED"))
            }
        }
        allEntries.add(Entry("voicemail # : ${tMgr?.voiceMailNumber }"))
        allEntries.add(Entry("SIM Serial #: ${tMgr?.simSerialNumber}"))
        allEntries.add(Entry("Network Operator Name: ${tMgr?.networkOperatorName}"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            allEntries.add(Entry("IMEI: ${tMgr?.imei}"))
            allEntries.add(Entry("MEID: ${tMgr?.meid}"))
            allEntries.add(Entry("Visual VMail Pkg Name: ${tMgr?.visualVoicemailPackageName}"))
        }

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