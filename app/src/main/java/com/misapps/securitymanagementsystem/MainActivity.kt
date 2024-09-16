package com.misapps.securitymanagementsystem

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAuthenticate = findViewById<Button>(R.id.btnAuthenticate)
        val btnEncryptData = findViewById<Button>(R.id.btnEncryptData)
        val btnViewLogs = findViewById<Button>(R.id.btnViewLogs)
        tvResult = findViewById(R.id.tvResult)

        btnAuthenticate.setOnClickListener {
            authenticateUser()
        }

        btnEncryptData.setOnClickListener {
            encryptData("Texto muy sensible")
        }

        btnViewLogs.setOnClickListener {
            viewSecurityLogs()
        }
    }

    // 1. Función de autenticación de biométrica
    private fun authenticateUser() {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                tvResult.text = "Autenticación exitosa!"
                logSecurityEvent("Autenticación biométrica exitosa.")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                tvResult.text = "Autenticación fallida"
                logSecurityEvent("Intento fallido de autenticación biométrica.")
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setDescription("Usa tu huella digital para autenticarse")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // 2. Función de encriptar los datos
    private fun encryptData(data: String) {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "encrypted_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.putString("secret_data", data)
        editor.apply()

        tvResult.text = "Datos encriptados correctamente"
        logSecurityEvent("Datos encriptados con éxito.")
    }

    // 3. Ver los logs de seguridad
    private fun viewSecurityLogs() {
        // Aquí simulamos un log
        tvResult.text = "Logs de seguridad: \n- Autenticación correcta \n- Datos encriptados"
        logSecurityEvent("Se visualizaron los logs de seguridad.")
    }

    // 4. Función para registrar nuestros eventos de seguridad
    private fun logSecurityEvent(event: String) {
        Log.d("SecurityEvent", event)
    }
}
