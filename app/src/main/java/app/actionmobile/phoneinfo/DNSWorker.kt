package app.actionmobile.phoneinfo

import android.os.AsyncTask
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import java.net.InetAddress

class DNSWorker : AsyncTask<String, Void, String> {

    var allEntries : ArrayList<Entry>
    var entryViewRecyclerView : RecyclerView

    constructor(e : ArrayList<Entry>, r : RecyclerView){
        this.allEntries = e
        entryViewRecyclerView = r
    }

    override fun doInBackground(vararg urls: String?): String {
        return try {
            Log.d("MainActivity", urls[0])
            var startTime = System.currentTimeMillis();
            var address = InetAddress.getByName(urls[0])
            var difference = System.currentTimeMillis() - startTime
            "${urls[0]} : ${address.hostAddress} : ${difference}ms"
        } catch (e: Exception) {
            "${urls[0]} : failed! ${e.message}"
        }
    }

    override fun onPostExecute(result: String?) {
        Log.d("MainActivity", result)
        var e = Entry(result!!)
        allEntries.add(e);
        var entryAdapter = EntryAdapter(allEntries)
        entryViewRecyclerView.adapter = entryAdapter;

    }
}