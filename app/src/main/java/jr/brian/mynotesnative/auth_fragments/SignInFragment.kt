package jr.brian.mynotesnative.auth_fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.R
import jr.brian.mynotesnative.constant.Constant
import jr.brian.mynotesnative.db.DatabaseHelper
import jr.brian.mynotesnative.notes.NotesGridActivity
import org.json.JSONObject

class SignInFragment : Fragment() {

    private lateinit var encryptedSharedPrefs: SharedPreferences
    private lateinit var databaseHelper: DatabaseHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper = DatabaseHelper(view.context)
        initEncryptedPrefs(view.context)
        initView(view)
    }

    private fun initEncryptedPrefs(context: Context) {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        encryptedSharedPrefs = EncryptedSharedPreferences.create(
            FILENAME,
            mainKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun initView(view: View) {
        view.findViewById<Button>(R.id.signInBTN).setOnClickListener {

            verifySignIn(view)
        }
    }

    private fun startHomeActivity(context: Context) {
        startActivity(Intent(context, NotesGridActivity::class.java))
    }

    private fun showSnackbar(str: String, view: View) {
        Snackbar.make(
            view.context,
            view.findViewById(R.id.sign_in_root),
            str,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun makeSignInApiRequest(view: View, data: JSONObject) {
        val cpb = view.findViewById<ProgressBar>(R.id.progress_bar_signIn)
        cpb.visibility = View.VISIBLE
        val requestQueue = Volley.newRequestQueue(view.context)
        val url = Constant.BASE_URL + Constant.SIGN_IN_END_POINT
        val request = JsonObjectRequest(Request.Method.POST, url, data, { response: JSONObject ->
            val msg = response.getString("message")
            Log.i("msg", msg)
            cpb.visibility = View.GONE
            startHomeActivity(view.context)
        }, { error: VolleyError ->
            error.printStackTrace()
            showSnackbar("Sign in failed. Try again.", view)
        })
        requestQueue.add(request)
    }

    private fun verifySignIn(view: View) {
        val emailEtSignIn = view.findViewById<EditText>(R.id.email_et_signIn)
        val passwordEtSignIn = view.findViewById<EditText>(R.id.password_et_signIn)
        if (emailEtSignIn.text.isNotEmpty() && passwordEtSignIn.text.isNotEmpty()) {
            val data = JSONObject().apply {
                put("email_id", emailEtSignIn.text.toString())
                put("password", passwordEtSignIn.text.toString())
            }
            makeSignInApiRequest(view, data)
//            if (encryptedSharedPrefs.contains(EMAIL) && encryptedSharedPrefs.contains(PASSWORD)) {

//            } else {
//                showSnackbar("Account not found. Please create an account", view)
//            }
        } else {
            showSnackbar("Please ensure both fields aren't empty", view)
        }
    }

    companion object {
        const val FILENAME = "login-details"
//        const val EMAIL = "email"
//        const val PASSWORD = "password"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }
}