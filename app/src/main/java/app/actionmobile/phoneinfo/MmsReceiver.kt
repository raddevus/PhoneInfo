package app.actionmobile.phoneinfo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MmsReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context!!,"got an mms", Toast.LENGTH_LONG)
    }

}