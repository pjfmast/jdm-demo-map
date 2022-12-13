package avd.jdm.demomaps

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

// For converting Json streams gson is better documented then moshi
// see also: https://stackoverflow.com/questions/73422455/mixed-mode-stream-oriented-json-parsing-with-moshi-without-special-type-adapters
// replace later with moshi?
class BlindWallsReader (private val context: Context) {
    // GSON object responsible for converting from JSON to a BlindWall object
    private val gson = Gson()

    // InputStream representing places.json
    private val inputStream: InputStream
        get() = context.resources.openRawResource(R.raw.blindwalls)

    /**
     * Reads the list of place JSON objects in the file places.json
     * and returns a list of Place objects
     */
    fun read(): List<BlindWall> {
        val itemType = object : TypeToken<List<BlindWallsResponse>>() {}.type
        val reader = InputStreamReader(inputStream)
        return gson.fromJson<List<BlindWallsResponse>>(reader, itemType).map {
            it.toBlindWall()
        }
    }
}