package app.actionmobile.phoneinfo

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class MMSMonitor(mainActivity: AppCompatActivity, mainContext: Context?) {
    private val mainActivity: AppCompatActivity
    private var contentResolver: ContentResolver? = null
    private var mmshandler: Handler? = null
    private var mmsObserver: ContentObserver? = null
    var mmsNumber = ""
    var monitorStatus = false
    var mmsCount = 0
    var lastMMSTxId: String? = null
    var code: String? = null
    fun startMMSMonitoring() {
        try {
            monitorStatus = false
            if (!monitorStatus) {
                contentResolver!!.registerContentObserver(
                    Uri.parse("content://mms-sms"),
                    true,
                    mmsObserver!!
                )
                val uriMMSURI: Uri = Uri.parse("content://mms")
                val mmsCur: Cursor? = mainActivity.getContentResolver()
                    .query(uriMMSURI, null, "msg_box = 4", null, "_id")
                if (mmsCur != null && mmsCur.getCount() > 0) {
                    mmsCount = mmsCur.getCount()
                    Log.d("MainActivity", "MMSMonitor :: Init MMSCount ==$mmsCount")
                }
            }
        } catch (e: Exception) {
            Log.d("MainActivity", "MMSMonitor :: startMMSMonitoring Exception== " + e.message)
        }
    }

    fun stopMMSMonitoring() {
        try {
            monitorStatus = false
            if (!monitorStatus) {
                contentResolver?.unregisterContentObserver(mmsObserver!!)
            }
        } catch (e: Exception) {
            Log.d("MainActivity", "MMSMonitor :: stopMMSMonitoring Exception == " + e.message)
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
                            Log.d("MainActivity", "MMSMonitor :: _id  == $id")
                            Log.d("MainActivity", "MMSMonitor :: Subject == $subject")
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
                                Log.d("MainActivity", "MMSMonitor :: Type == Outgoing MMS")
                            } else {
                                isIncoming = true
                                direction = "1"
                                Log.d("MainActivity", "MMSMonitor :: Type == Incoming MMS")
                            }
                            // Get Parts
                            val uriMMSPart: Uri = Uri.parse("content://mms/part")
                            val curPart: Cursor? = mainActivity.getContentResolver()
                                .query(uriMMSPart, null, "mid = $id", null, "_id")
                            Log.d("MainActivity", "MMSMonitor :: parts records length == " + curPart?.getCount())
                            curPart?.moveToLast()
                            do { //String contentType = curPart.getString(3);
//String partId = curPart.getString(0);
                                val contentType: String =
                                    curPart!!.getString(curPart.getColumnIndex("ct"))
                                val partId: String =
                                    curPart.getString(curPart.getColumnIndex("_id"))
                                Log.d("MainActivity", "MMSMonitor :: partId == $partId")
                                Log.d("MainActivity", "MMSMonitor :: part mime type == $contentType")
                                // Get the message
                                if (contentType.equals("text/plain", ignoreCase = true)) {
                                    Log.d("MainActivity", "MMSMonitor :: ==== Get the message start ====")
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
                                            Log.d(
                                                "MainActivity", "MMSMonitor :: Column Name : " +
                                                        curPart1!!.getColumnName(i)
                                            )
                                        }
                                        curPart1?.moveToLast()
                                        message = curPart1!!.getString(13)
                                    }
                                    Log.d("MainActivity", "MMSMonitor :: Txt Message == $message")
                                } else if (isImageType(contentType) == true) {
                                    Log.d("MainActivity", "MMSMonitor :: ==== Get the Image start ====")
                                    fileName = "mms_$partId"
                                    fileType = contentType
                                    imgData = readMMSPart(partId)
                                    Log.d("MainActivity", "MMSMonitor :: Iimage data length == " + imgData!!.size)
                                }
                            } while (curPart!!.moveToPrevious())
                            // Get Address
                            val uriAddrPart: Uri = Uri.parse("content://mms/$id/addr")
                            val addrCur: Cursor? = mainActivity.getContentResolver()
                                .query(uriAddrPart, null, "type=151", null, "_id")
                            if (addrCur != null) {
                                addrCur.moveToLast()
                                do {
                                    Log.d(
                                        "MainActivity",
                                        "MMSMonitor :: addrCur records length = " + addrCur.getCount()
                                    )
                                    val addColIndx: Int = addrCur.getColumnIndex("address")
                                    val typeColIndx: Int = addrCur.getColumnIndex("type")
                                    address = addrCur.getString(addColIndx)
                                    Log.d("MainActivity", "MMSMonitor :: address == $address")
//                                    code =    getActivationcode()
                                    Log.d("MainActivity", "MMSMonitor :: Activation Code ==$code")
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
                                    Log.d("MainActivity", "MMSMonitor :: File Name ==$fileName")
                                    Log.d("MainActivity", "MMSMonitor :: Params ==$params")
//                                    Log.d("MainActivity", "MMSMonitor :: Upload response = " + String(response))
                                } while (addrCur.moveToPrevious())
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("MainActivity", "MMSMonitor Exception:: " + e.message)
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
            Log.d("MainActivity", "MMSMonitor :: Entered into readMMSPart try..")
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
            Log.d("MainActivity", "MMSMonitor :: Exception == Failed to load part data")
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    Log.d("MainActivity", "Exception :: Failed to close stream")
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
        Log.d("MainActivity", "MMSMonitor :: ***** Start MMS Monitor *****")
    }
}