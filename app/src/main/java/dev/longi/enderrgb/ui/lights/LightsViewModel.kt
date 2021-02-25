package dev.longi.enderrgb.ui.lights

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.android.volley.Request
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

        try {
            val request = JsonObjectRequest(Request.Method.POST, url("light/rgb"), body,
                { response ->
                    Log.i(TAG, "setColor: Request successful: $response")
                },
                { error ->
                    _error.value = error.message
                    Log.e(TAG, "setColor: Failed to send request", error)
                }
            )

            VolleyQueue.getInstance(getApplication()).addToRequestQueue(request)
        } catch (e: Exception) {
            _error.value = e.message
            Log.e(TAG, "setColor: exception while making request", e)
        }
    }

    fun off() {
        setColor(0, 0, 0)
    }

    fun getEffects() {
        try {
            val request = JsonObjectRequest(Request.Method.GET, url("light/effect"), null,
                { response ->
                    Log.i(TAG, "getEffects: Request successful: $response")
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
                },
                { error ->
                    _error.value = error.message
                    Log.e(TAG, "getEffects: Failed to send request", error)
                }
            )

            VolleyQueue.getInstance(getApplication()).addToRequestQueue(request)
        } catch (e: Exception) {
            _error.value = e.message
            Log.e(TAG, "getEffects: exception while making request", e)
        }
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
        try {
            val request =
                JsonObjectRequest(Request.Method.POST, url("light/effect/$effectName"), null,
                    { response ->
                        Log.i(TAG, "setEffect: Request successful: $response")
                    },
                    { error ->
                        _error.value = error.message
                        Log.e(TAG, "setEffect: Failed to send request", error)
                    }
                )

            VolleyQueue.getInstance(getApplication()).addToRequestQueue(request)
        } catch (e: Exception) {
            _error.value = e.message
            Log.e(TAG, "setEffect: exception while making request", e)
        }
    }

    fun stopEffect() {
        try {
            val request =
                JsonObjectRequest(Request.Method.DELETE, url("light/effect"), null,
                    { response ->
                        Log.i(TAG, "stopEffect: Request successful: $response")
                    },
                    { error ->
                        _error.value = error.message
                        Log.e(TAG, "stopEffect: Failed to send request", error)
                    }
                )

            VolleyQueue.getInstance(getApplication()).addToRequestQueue(request)
        } catch (e: Exception) {
            _error.value = e.message
            Log.e(TAG, "stopEffect: exception while making request", e)
        }
    }
}