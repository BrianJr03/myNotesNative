package jr.brian.mynotesnative

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import jr.brian.mynotesnative.auth_activities.SignInActivity
import jr.brian.mynotesnative.auth_activities.SignUpActivity
import jr.brian.mynotesnative.databinding.ActivityLandingBinding
import jr.brian.mynotesnative.notes.NotesGridActivity

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private lateinit var encryptedSharedPrefs: SharedPreferences
//    private lateinit var bundle : Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        supportActionBar?.hide()
        initEncryptedPrefs()
        initListeners()
        verifySignIn()
    }

    private fun initEncryptedPrefs() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        encryptedSharedPrefs = EncryptedSharedPreferences.create(
            SignInActivity.FILENAME,
            mainKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun initListeners() {
        binding.signInBtn.setOnClickListener {
            startActivity(
                Intent(this, SignInActivity::class.java)
            )
        }
        binding.signUpBtn.setOnClickListener {
            startActivity(
                Intent(this, SignUpActivity::class.java)
            )
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, NotesGridActivity::class.java))
    }

    private fun verifySignIn() {
        if (encryptedSharedPrefs.contains(SignInActivity.EMAIL) && encryptedSharedPrefs.contains(
                SignInActivity.PASSWORD
            )
        ) {
            startHomeActivity()
        }
    }
}