package com.study.mastersdegree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.lifecycle.lifecycleScope

class GoogleLoginActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    private val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getWritePermission(DistanceRecord::class)
    )

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login)

        // Inicjalizacja Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Rejestracja wyników żądania zezwoleń
        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                Log.d("HealthConnect", "Wszystkie wymagane uprawnienia przyznane.")
                signIn()
            } else {
                Toast.makeText(this, "Brak wymaganych zezwoleń", Toast.LENGTH_SHORT).show()
            }
        }

        // Obsługa kliknięcia przycisku logowania
        findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            checkPermissionsAndRun()
        }
    }

    private fun checkPermissionsAndRun() {
        val healthConnectClient = HealthConnectClient.getOrCreate(this)
        val permissionController = healthConnectClient.permissionController

        lifecycleScope.launchWhenStarted {
            val grantedPermissions = permissionController.getGrantedPermissions()
            if (grantedPermissions.containsAll(PERMISSIONS)) {
                Log.d("HealthConnect", "Uprawnienia już przyznane.")
                signIn()
            } else {
                requestPermissionsLauncher.launch(PERMISSIONS.toTypedArray()) // Konwersja na Array<String>
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleLogin", "Zalogowano jako: ${account.email}")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: ApiException) {
            Log.w("GoogleLogin", "Błąd logowania: ${e.statusCode}")
        }
    }
}
