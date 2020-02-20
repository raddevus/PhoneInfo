package app.actionmobile.phoneinfo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import app.actionmobile.phoneinfo.ui.main.PlaceholderFragment

class TextReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //Toast.makeText(context!!,"Got a text", Toast.LENGTH_LONG).show()
        Log.d("MainActivity", "got a text")
        try {
            var allMessages: Array<SmsMessage> = getMessagesFromIntent(intent)

            var msg: String
            var from: String? = null
            for (m in allMessages) {
                msg = m.messageBody
                from = m.originatingAddress
                Log.d("MainActivity", "from: $from -- $msg")
                PlaceholderFragment.handleTextMessageArrival("from: $from? -- $msg")
            }
            return;
        }
        catch (ex: Exception){
            PlaceholderFragment.handleTextMessageArrival("Location 1: ${ex.message!!}")
            Log.d("MainActivity", ex.message)
        }
        try {
            var bundle = intent?.extras

            val mmsBundle = intent!!.extras
            var mmsData  = mmsBundle!!.getByteArray("data")
            Log.d("MainActivity","${mmsData}")
            PlaceholderFragment.handleTextMessageArrival("mmsData : ${mmsData!!.toString(Charsets.UTF_8)}")

        }
        catch (ex : Exception){
            PlaceholderFragment.handleTextMessageArrival("Location 2: ${ex.message!!}")
            Log.d("MainActivity", ex.message)
        }
    }

//    private fun GetMessage(intent : Intent) : Telephony.MmsSms{
//        var msg : Telephony.MmsSms;
//        var bundle : Bundle = intent.extras!!
//        bundle.
//        return msg;
//    }

}