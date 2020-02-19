package app.actionmobile.phoneinfo

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import app.actionmobile.phoneinfo.ui.main.PlaceholderFragment
import app.actionmobile.phoneinfo.ui.main.SectionsPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            val li = LayoutInflater.from(baseContext)
            val v: View = li.inflate(R.layout.config_values, null)
            val builder: AlertDialog.Builder = AlertDialog.Builder(v.getContext())

            builder.setMessage("Add new URL").setCancelable(false)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        val urls = applicationContext.getSharedPreferences(
                            "urls",
                            Context.MODE_PRIVATE
                        )
                        var outValues = urls.getString("urls", "")
                        Log.d("MainActivity", urls.getString("urls", ""))
                        val edit = urls.edit()
                        //edit.clear();
                        val input = v.findViewById(R.id.urlText) as EditText
                        val currentValue = input.text.toString()
                        if (currentValue !== "") {
                            outValues += if (outValues !== "") {
                                ",$currentValue"
                            } else {
                                currentValue
                            }
                        }
                        edit.putString("urls", outValues)
                        edit.commit()
                        PlaceholderFragment.areUserPrefsChanged = true
                        Log.d("MainActivity", "final outValues : $outValues")
                    })
                .setNegativeButton("CANCEL",
                    DialogInterface.OnClickListener { dialog, id -> })
            val alert: AlertDialog = builder.create()
            alert.setView(v)
            alert.show()
        }
    }
}