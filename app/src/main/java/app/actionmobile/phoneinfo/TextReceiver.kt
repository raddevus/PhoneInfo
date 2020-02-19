package app.actionmobile.phoneinfo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.util.Log

class TextReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("MainActivity", "got a text")
    }

//    private fun GetMessage(intent : Intent) : Telephony.MmsSms{
//        var msg : Telephony.MmsSms;
//        var bundle : Bundle = intent.extras!!
//        bundle.
//        return msg;
//    }

}