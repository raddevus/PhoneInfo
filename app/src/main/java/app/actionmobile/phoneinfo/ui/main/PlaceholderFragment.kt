package app.actionmobile.phoneinfo.ui.main

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.actionmobile.phoneinfo.DNSWorker
import app.actionmobile.phoneinfo.Entry
import app.actionmobile.phoneinfo.EntryAdapter
import app.actionmobile.phoneinfo.R
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment(), SensorEventListener {

    private lateinit var pageViewModel: PageViewModel
    var temperaturelabel: TextView? = null
    var mSensorManager : SensorManager? = null
    var mTemperature : Sensor? = null

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
        var textView : TextView? = null
        var root : View? = null

        var NOT_SUPPORTED_MESSAGE = "Sorry, sensor not available for this device."

        when (arguments?.getInt(ARG_SECTION_NUMBER)) {
            1->{
                root = inflater.inflate(R.layout.fragment_phone, container, false)
                val b: Button = root?.findViewById(R.id.alertbutton) as Button
                b.text = "Load Phone Details"
                val EntryViewRecyclerView: RecyclerView =
                    root.findViewById(R.id.entryRecyclerView) as RecyclerView

                b.setOnClickListener {
                    //Toast.makeText(it.getContext(),"alert", Toast.LENGTH_SHORT).show()
                    var entryList = ArrayList<Entry>()
                    getPhoneDetails(entryList)
                    var entryAdapter = EntryAdapter(entryList)
                    EntryViewRecyclerView.adapter = entryAdapter;
                }

                val manager: RecyclerView.LayoutManager = LinearLayoutManager(root.getContext())
                EntryViewRecyclerView.setLayoutManager(manager)
                var entryList = ArrayList<Entry>()

                getPhoneDetails(entryList);

                var entryAdapter = EntryAdapter(entryList)
                EntryViewRecyclerView.adapter = entryAdapter;
            }
            2 ->{
                root = inflater.inflate(R.layout.fragment_dns, container, false)
                val b: Button = root?.findViewById(R.id.dnsButton) as Button
                val urlSpinner : Spinner = root?.findViewById(R.id.urlSpinner) as Spinner
                b.setOnClickListener {
                    TestUrls(root!!)
                }
                val spinnerAdapter: ArrayAdapter<String> =
                    ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1)
                // Specify the layout to use when the list of choices appears
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                urlSpinner.adapter = spinnerAdapter;
                spinnerAdapter.add(" - none - ")
                spinnerAdapter.notifyDataSetChanged()
                fillSpinnerWithValuesFromUserPrefs()
            }
            5 -> {

                root = inflater.inflate(R.layout.fragment_temperature, container, false)
                temperaturelabel = root?.findViewById(R.id.temperatureTextView)
                mSensorManager = context?.getSystemService(SENSOR_SERVICE) as SensorManager
                    //context, SENSOR_SERVICE) as SensorManager
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                    mTemperature= mSensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE); // requires API level 14.
                }
                if (mTemperature == null) {
                    temperaturelabel?.setText(NOT_SUPPORTED_MESSAGE);
                }
            }
            3 -> {
                root = inflater.inflate(R.layout.fragment_main, container, false)
                textView = root?.findViewById(R.id.section_label)
            }
            4 -> {
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

        }


//        pageViewModel.text.observe(this, Observer<String> {
//            textView?.text = it
//        })
        return root
    }

    fun fillSpinnerWithValuesFromUserPrefs(){

    }

    fun TestUrls(v : View){
        val entryViewRecyclerView: RecyclerView =
            v.findViewById(R.id.entryRecyclerView) as RecyclerView
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(v.getContext())
        entryViewRecyclerView.setLayoutManager(manager)
        var entryList = ArrayList<Entry>()
        var urlPrefs = context!!.getSharedPreferences("urls", MODE_PRIVATE);
        var urls = urlPrefs.getString("urls", "");

        var allUrls = urls?.split(",");
        //var allUrls : Array<String> = arrayOf("raddev.us","google.com", "microsoft.com","codeproject.com", "newlibre.com")
        if (allUrls != null) {
            for (u in allUrls) {
                Log.d("MainActivity", u)
                DNSWorker(entryList, entryViewRecyclerView).execute(u)
            }
        }
    }

    fun requestAllPermissions(){
        val permission = Manifest.permission.READ_PHONE_STATE
        val grant = ContextCompat.checkSelfPermission(context!!, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = permission
            ActivityCompat.requestPermissions(activity as Activity, permission_list, 1)
        }
    }
    @TargetApi(26)
    fun getPhoneDetails(allEntries : ArrayList<Entry>){
        val tMgr =
            context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager as TelephonyManager?
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestAllPermissions();
            return;
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                allEntries.add(Entry("voicemail # : ${tMgr?.voiceMailNumber}"))
                allEntries.add(Entry("SIM Serial #: ${tMgr?.simSerialNumber}"))
                allEntries.add(Entry("IMEI: ${tMgr?.imei}"))
                allEntries.add(Entry("MEID: ${tMgr?.meid}"))
            }
            catch(e : Exception){
                Log.d("MainActivity", e.message);
            }
        }
        allEntries.add(Entry("Network Operator Name: ${tMgr?.networkOperatorName}"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


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

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        var ambient_temperature = p0!!.values[0]
        temperaturelabel?.setText("Ambient Temperature: ${ambient_temperature}\n")
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);

    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this);
    }
}