package app.actionmobile.phoneinfo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.telephony.SmsMessage
import android.util.Log
import app.actionmobile.phoneinfo.ui.main.PlaceholderFragment

class TextReceiver : BroadcastReceiver() {

    private val ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED"
    private val MMS_DATA_TYPE = "application/vnd.wap.mms-message"

    override fun onReceive(context: Context?, intent: Intent?) {
        //Toast.makeText(context!!,"Got a text", Toast.LENGTH_LONG).show()
        Log.d("MainActivity", "got a text")
        val action = intent!!.action
        val type = intent.type
        if (action == ACTION_MMS_RECEIVED && type == MMS_DATA_TYPE) {
            var bundle = intent?.extras

            val mmsBundle = intent!!.extras
            var mmsData = mmsBundle!!.getByteArray("data")
            Log.d("MainActivity", "${mmsData}")
            PlaceholderFragment.handleTextMessageArrival("mmsData : ${mmsData!!.toString(Charsets.UTF_8)}")

            var msgs: Array<SmsMessage>? = null
            var str: String = "";
            var contactId = -1;
            var address: String = ""

            if (bundle != null) {

                var buffer = bundle.getByteArray("data");
                Log.d("MainActivity", "buffer " + buffer);
                PlaceholderFragment.handleTextMessageArrival("buffer $buffer")
                var incomingNumber = String(buffer!!)
                var indx = incomingNumber.indexOf("/TYPE")
                if (indx > 0 && (indx - 15) > 0) {
                    var newIndx = indx - 15
                    incomingNumber = incomingNumber.substring(newIndx, indx)
                    indx = incomingNumber.indexOf("+")
                    if (indx > 0) {
                        incomingNumber = incomingNumber.substring(indx)
                        Log.d("MainActivity", "Mobile Number: $incomingNumber")
                    }
                }
            }
            var transactionId = bundle?.getInt("transactionId")
            Log.d("MainActivity", "transactionId $transactionId")
            PlaceholderFragment.handleTextMessageArrival("transactionId $transactionId")
            var pduType = bundle?.getInt("pduType")
            Log.d("MainActivity", "pduType $pduType")
            PlaceholderFragment.handleTextMessageArrival("pduType $pduType")
            var buffer2 = bundle?.getByteArray("header")
            var header = String(buffer2!!)
            Log.d("MainActivity", "header $header")
            PlaceholderFragment.handleTextMessageArrival("header $header")
            if (contactId != -1) {
                PlaceholderFragment.handleTextMessageArrival("contactID : $contactId -- $str")
            }
        }
        else {
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
            } catch (ex: Exception) {
                PlaceholderFragment.handleTextMessageArrival("Location 1: ${ex.message!!}")
                Log.d("MainActivity", ex.message)
            }
        }
    }
}

//    private fun GetMessage(intent : Intent) : Telephony.MmsSms{
//        var msg : Telephony.MmsSms;
//        var bundle : Bundle = intent.extras!!
//        bundle.
//        return msg;
//    }

