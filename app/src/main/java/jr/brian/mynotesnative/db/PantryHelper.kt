package jr.brian.mynotesnative.db

import android.view.View
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import jr.brian.mynotesnative.constant.Constant
import jr.brian.mynotesnative.data.Note
import org.json.JSONObject

class PantryHelper {
    private fun crudNote(
        note: Note,
        view: View,
        method: Int,
    ): JSONObject {
        val gson = Gson()
        val jsonString = gson.toJson(note)
        val requestQueue = Volley.newRequestQueue(view.context)
        val url = Constant.CRUD_BASKET_URL + "Note_${note.title}_${note.index}"
        var response = JSONObject()
        val request = JsonObjectRequest(
            method,
            url,
            JSONObject(jsonString), { r: JSONObject ->
                response = r
            },
            { error: VolleyError -> error.printStackTrace() })
        requestQueue.add(request)
        return response
    }

    fun saveNote(note: Note, view: View): JSONObject {
        return crudNote(
            note,
            view,
            Request.Method.POST,
        )
    }

    fun updateNote(note: Note, view: View): JSONObject {
        return crudNote(
            note,
            view,
            Request.Method.PUT,
        )
    }

    fun getNote(note: Note, view: View): JSONObject {
        return crudNote(
            note,
            view,
            Request.Method.GET,
        )
    }

    fun deleteNote(note: Note, view: View): JSONObject {
        return crudNote(
            note,
            view,
            Request.Method.DELETE,
        )
    }
}