package app.actionmobile.phoneinfo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.telephony.SmsMessage
import android.util.Log
import app.actionmobile.phoneinfo.ui.main.PlaceholderFragment

class TextReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("MainActivity", "got a text")
        var bundle = intent?.extras
        var allMessages : Array<SmsMessage> = getMessagesFromIntent(intent)
        var msg : String
        var from : String? = null
        for (m in allMessages){
            msg = m.messageBody
            from = m.originatingAddress
            Log.d("MainActivity", "from: $from -- $msg")
            PlaceholderFragment.handleTextMessageArrival("from: $from? -- $msg")
        }
    }

//    private fun GetMessage(intent : Intent) : Telephony.MmsSms{
//        var msg : Telephony.MmsSms;
//        var bundle : Bundle = intent.extras!!
//        bundle.
//        return msg;
//    }

}