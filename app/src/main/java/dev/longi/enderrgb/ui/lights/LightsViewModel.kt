package dev.longi.enderrgb.ui.lights

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import dev.longi.enderrgb.R
import dev.longi.enderrgb.network.VolleyQueue
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.IllegalStateException

class LightsViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val TAG = "LightsViewModel"
    }

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _effects = MutableLiveData<List<String>>()
    val effects: LiveData<List<String>> = _effects

    private fun jsonRequest(method: Int, path: String, listener: Response.Listener<JSONObject>? = null, jsonBody: JSONObject? = null) {
        try {
            val url = url(path)
            val request =
                JsonObjectRequest(method, url, jsonBody,
                    { response ->
                        Log.i(TAG, "stopEffect: Request successful: $response")
                        listener?.onResponse(response)
                    },
                    { error ->
                        _error.value = error.message
                        Log.e(TAG, "Failed to send request to $url", error)
                    }
                )

            VolleyQueue.getInstance(getApplication()).addToRequestQueue(request)
        } catch (e: Exception) {
            _error.value = e.message
            Log.e(TAG, "exception while making request to $path", e)
        }
    }

    fun setColor(color: Int) {
        val b = color and 255
        val g = (color shr 8) and 255
        val r = (color shr 16) and 255
        setColor(r, g, b)
    }

    fun setColor(r: Int, g: Int, b: Int) {
        val body = JSONObject()
        body.put("r", r)
        body.put("g", g)
        body.put("b", b)

        jsonRequest(Request.Method.POST, "light/rgb", null, body)
    }

    fun off() {
        setColor(0, 0, 0)
    }

    fun getEffects() {
        jsonRequest(Request.Method.GET, "light/effect", { response ->
            try {
                val effectsArray = response.getJSONArray("response")
                val effects = mutableListOf<String>()
                for (i in 0 until effectsArray.length()) {
                    effects.add(effectsArray.getString(i))
                }
                _effects.value = effects
            } catch (e: JSONException) {
                _effects.value = mutableListOf()
                Log.e(TAG, "getEffects: Malformed JSON, $response", e)
            }
        })
    }

    private fun url(path: String): String {
        val context: Context = getApplication()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val host = prefs.getString(context.getString(R.string.pref_octopi_host), "")
        val port = prefs.getString(context.getString(R.string.pref_enderrgb_port), "8090")?.toInt()

        if (host.isNullOrBlank()) {
            throw IllegalStateException("OctoPi host not set")
        }

        return "$host:$port/api/$path"
    }

    fun setEffect(effectName: String) {
        jsonRequest(Request.Method.POST, "light/effect/$effectName")
    }

    fun stopEffect() {
        jsonRequest(Request.Method.DELETE, "light/effect")
    }
}