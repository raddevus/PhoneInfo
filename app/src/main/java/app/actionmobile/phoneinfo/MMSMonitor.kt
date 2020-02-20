package app.actionmobile.phoneinfo

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.actionmobile.phoneinfo.ui.main.PlaceholderFragment
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class MMSMonitor(mainActivity: Activity, mainContext: Context?) {
    private val mainActivity: Activity
    private var contentResolver: ContentResolver? = null
    private var mmshandler: Handler? = null
    private var mmsObserver: ContentObserver? = null
    var mmsNumber = ""
    var monitorStatus = false
    var mmsCount = 0
    var lastMMSTxId: String? = null
    var code: String? = null
    var context = mainContext

    fun startMMSMonitoring() {
        try {
            monitorStatus = false
            if (!monitorStatus) {

                val uriMMSURI: Uri = Uri.parse("content://mms")


                contentResolver!!.registerContentObserver(
                    Uri.parse("content://mms-sms"),
                    true,
                    mmsObserver!!
                )

                val mmsCur: Cursor? = mainActivity.getContentResolver()
                    .query(uriMMSURI, null, "msg_box = 4", null, "_id")
                if (mmsCur != null && mmsCur.getCount() > 0) {
                    mmsCount = mmsCur.getCount()
                    Toast.makeText(context,"MMSMonitor :: Init MMSCount ==$mmsCount", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context,"MMSMonitor :: startMMSMonitoring Exception== " + e.message,Toast.LENGTH_SHORT).show()
        }
    }

    fun stopMMSMonitoring() {
        try {
            monitorStatus = false
            if (!monitorStatus) {
                contentResolver?.unregisterContentObserver(mmsObserver!!)
            }
        } catch (e: Exception) {
            Toast.makeText(context,"MMSMonitor :: stopMMSMonitoring Exception == " + e.message,Toast.LENGTH_SHORT).show()
        }
    }
    internal inner class MMSHandler : Handler() {
        override fun handleMessage(msg: Message?) { //Log.d("MMS", "MMSMonitor :: Handler");
        }
    }

    internal inner class MMSObserver(mmshandle: Handler?) : ContentObserver(mmshandle) {
        private var mms_handle: Handler? = null
        override fun onChange(bSelfChange: Boolean) {
            super.onChange(bSelfChange)
            //Log.d("MMS", "MMSMonitor :: Onchange");
            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
                        monitorStatus = true
                        // Send message to Activity
                        val msg = Message()
                        mms_handle?.sendMessage(msg)
                        // Getting the mms count
                        val uriMMSURI: Uri = Uri.parse("content://mms/")
                        val mmsCur: Cursor? = mainActivity.getContentResolver()
                            .query(uriMMSURI, null, "msg_box = 4 or msg_box = 1", null, "_id")
                        var currMMSCount = 0
                        if (mmsCur != null && mmsCur.getCount() > 0) {
                            currMMSCount = mmsCur.getCount()
                        }
                        if (currMMSCount > mmsCount) {
                            mmsCount = currMMSCount
                            mmsCur?.moveToLast()
                            // get id , subject
//String subject = mmsCur.getString(6);
//int id = Integer.parseInt(mmsCur.getString(0));
                            val subject: String =
                                mmsCur!!.getString(mmsCur!!.getColumnIndex("sub"))
                            val id: Int = mmsCur.getString(mmsCur.getColumnIndex("_id")).toInt()
                            //",Toast.LENGTH_SHORT)
                           Toast.makeText(context,"MMSMonitor :: _id  == $id",Toast.LENGTH_SHORT).show()
                           Toast.makeText(context,"MMSMonitor :: Subject == $subject",Toast.LENGTH_SHORT).show()
                            var imgData: ByteArray? = null
                            var message = ""
                            var address = ""
                            var fileName = ""
                            var fileType: String? = ""
                            var direction = ""
                            // GET DIRECTION
                            var isIncoming = false
                            //int type = Integer.parseInt(mmsCur.getString(12));
                            val type: Int =
                                mmsCur.getString(mmsCur.getColumnIndex("m_type")).toInt()
                            if (type == 128) {
                                direction = "0"
                               Toast.makeText(context,"MMSMonitor :: Type == Outgoing MMS",Toast.LENGTH_SHORT).show()
                            } else {
                                isIncoming = true
                                direction = "1"
                               Toast.makeText(context,"MMSMonitor :: Type == Incoming MMS",Toast.LENGTH_SHORT).show()
                            }
                            // Get Parts
                            val uriMMSPart: Uri = Uri.parse("content://mms/part")
                            val curPart: Cursor? = mainActivity.getContentResolver()
                                .query(uriMMSPart, null, "mid = $id", null, "_id")
                           Toast.makeText(context,"MMSMonitor :: parts records length ==  ${curPart?.getCount()}",Toast.LENGTH_SHORT).show()
                            curPart?.moveToLast()
                            do { //String contentType = curPart.getString(3);
//String partId = curPart.getString(0);
                                val contentType: String =
                                    curPart!!.getString(curPart.getColumnIndex("ct"))
                                val partId: String =
                                    curPart.getString(curPart.getColumnIndex("_id"))
                               Toast.makeText(context,"MMSMonitor :: partId == $partId",Toast.LENGTH_SHORT).show()
                               Toast.makeText(context,"MMSMonitor :: part mime type == $contentType",Toast.LENGTH_SHORT).show()
                                // Get the message
                                if (contentType.equals("text/plain", ignoreCase = true)) {
                                   Toast.makeText(context,"MMSMonitor :: ==== Get the message start ====",Toast.LENGTH_SHORT).show()
                                    val messageData = readMMSPart(partId)
                                    if (messageData != null && messageData.size > 0) message =
                                        String(messageData)
                                    if (message === "") {
                                        val curPart1: Cursor? = mainActivity.getContentResolver()
                                            .query(
                                                uriMMSPart, null, "mid = " + id +
                                                        " and _id =" + partId, null, "_id"
                                            )
                                        for (i in 0 until curPart1!!.getColumnCount()) {
                                            Toast.makeText(context,"MMSMonitor :: Column Name : ${curPart1!!.getColumnName(i)}",Toast.LENGTH_SHORT).show()
                                        }
                                        curPart1?.moveToLast()
                                        message = curPart1!!.getString(13)
                                    }
                                   Toast.makeText(context,"MMSMonitor :: Txt Message == $message",Toast.LENGTH_SHORT).show()
                                } else if (isImageType(contentType) == true) {
                                   Toast.makeText(context,"MMSMonitor :: ==== Get the Image start ====",Toast.LENGTH_SHORT).show()
                                    fileName = "mms_$partId"
                                    fileType = contentType
                                    imgData = readMMSPart(partId)
                                   Toast.makeText(context,"MMSMonitor :: Iimage data length == ${imgData!!.size}",Toast.LENGTH_SHORT).show()
                                }
                            } while (curPart!!.moveToPrevious())
                            // Get Address
                            val uriAddrPart: Uri = Uri.parse("content://mms/$id/addr")
                            val addrCur: Cursor? = mainActivity.getContentResolver()
                                .query(uriAddrPart, null, "type=151", null, "_id")
                            if (addrCur != null) {
                                addrCur.moveToLast()
                                do {
                                    Toast.makeText(context,"MMSMonitor :: addrCur records length = " + addrCur.getCount(),Toast.LENGTH_SHORT).show()
                                    val addColIndx: Int = addrCur.getColumnIndex("address")
                                    val typeColIndx: Int = addrCur.getColumnIndex("type")
                                    address = addrCur.getString(addColIndx)

                                   Toast.makeText(context,"MMSMonitor :: address == $address",Toast.LENGTH_SHORT).show()
//                                    code =    getActivationcode()
                                   Toast.makeText(context,"MMSMonitor :: Activation Code ==$code",Toast.LENGTH_SHORT).show()
                                    val params = Hashtable<String,String>()
                                    params.put("verification_code", code)
                                    params.put("subject", subject)
                                    params.put("message", message)
                                    params.put("tel_number", address)
                                    params.put("direction", direction)
//                                    val url: String = AppData.URL_MMS_UPLOAD
//                                    val httpUp = HTTPMultipartUpload(
//                                        url, params, "uploadedfile", fileName, fileType, imgData
//                                    )
//                                    val response: ByteArray = httpUp.send()
                                   Toast.makeText(context,"MMSMonitor :: File Name ==$fileName",Toast.LENGTH_SHORT).show()
                                   Toast.makeText(context,"MMSMonitor :: Params ==$params",Toast.LENGTH_SHORT).show()
//                                   Toast.makeText(context,"MMSMonitor :: Upload response = " + String(response))
                                } while (addrCur.moveToPrevious())
                            }
                        }
                    } catch (e: Exception) {
                       Toast.makeText(context,"MMSMonitor Exception:: ${e.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            thread.start()
        }

        init {
            mms_handle = mmshandle
        }
    }

    private fun readMMSPart(partId: String): ByteArray? {
        var partData: ByteArray? = null
        val partURI: Uri = Uri.parse("content://mms/part/$partId")
        val baos = ByteArrayOutputStream()
        var `is`: InputStream? = null
        try {
           Toast.makeText(context,"MMSMonitor :: Entered into readMMSPart try..",Toast.LENGTH_SHORT).show()
            val mContentResolver: ContentResolver = mainActivity.getContentResolver()
            `is` = mContentResolver.openInputStream(partURI)
            val buffer = ByteArray(256)
            var len: Int = `is`!!.read(buffer)
            while (len >= 0) {
                baos.write(buffer, 0, len)
                len = `is`.read(buffer)
            }
            partData = baos.toByteArray()
            //Log.i("", "Text Msg  :: " + new String(partData));
        } catch (e: IOException) {
           Toast.makeText(context,"MMSMonitor :: Exception == Failed to load part data",Toast.LENGTH_SHORT).show()
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                   Toast.makeText(context,"Exception :: Failed to close stream",Toast.LENGTH_SHORT).show()
                }
            }
        }
        return partData
    }

    private fun isImageType(mime: String): Boolean {
        var result = false
        if (mime.equals("image/jpg", ignoreCase = true)
            || mime.equals("image/jpeg", ignoreCase = true)
            || mime.equals("image/png", ignoreCase = true)
            || mime.equals("image/gif", ignoreCase = true)
            || mime.equals("image/bmp", ignoreCase = true)
        ) {
            result = true
        }
        return result
    }

//    fun Log(tag: String?, message: String?) {
//        Logger.getInstance(Logger.DEBUG).log(
//            this.javaClass.simpleName,
//            tag, message
//        )
//    }

    companion object {
        var activationCode: String? = null
    }

    init {
        this.mainActivity = mainActivity
        contentResolver = mainActivity.getContentResolver()
        mmshandler = MMSHandler()
        mmsObserver = MMSObserver(mmshandler)
       Toast.makeText(context,"MMSMonitor :: ***** Start MMS Monitor *****",Toast.LENGTH_SHORT).show()
    }
}