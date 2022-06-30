package jr.brian.mynotesnative

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import jr.brian.mynotesnative.databinding.ActivityMainBinding
import jr.brian.mynotesnative.notes.NotesGridActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var encryptedSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initEncryptedPrefs()
        initView()
        verifySignIn()
    }

    private fun initEncryptedPrefs() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        encryptedSharedPrefs = EncryptedSharedPreferences.create(
            FILENAME,
            mainKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        editor = encryptedSharedPrefs.edit()
    }

    private fun initView() {
        binding.apply {
            binding.loginBtn.setOnClickListener {
                signIn(emailEtSecure.text.toString(), passwordEtSecure.text.toString())
            }
        }
    }

    private fun signIn(email: String, password: String) {
        editor.apply {
            putString(EMAIL, email)
            putString(PASSWORD, password)
            if (commit()) {
                startHomeActivity()
            }
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this@MainActivity, NotesGridActivity::class.java))
    }

    private fun verifySignIn() {
        if (encryptedSharedPrefs.contains(EMAIL)) {
            startHomeActivity()
        }
    }

    companion object {
        const val FILENAME = "login-details"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }
}