package com.nyx.ai.ui.setup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nyx.ai.R
import com.nyx.ai.databinding.ActivityApiSetupBinding
import com.nyx.ai.manager.ApiKeyManager
import com.nyx.ai.ui.chat.ChatActivity

class ApiSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApiSetupBinding
    private lateinit var apiKeyManager: ApiKeyManager
    
    private val keyFields = mutableListOf<TextInputEditText>()
    private var keyCount = 3 // Começa com 3 campos
    
    companion object {
        const val MIN_KEYS = 10
        const val MAX_KEYS = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiKeyManager = ApiKeyManager.getInstance(this)

        setupInitialFields()
        setupButtons()
    }

    private fun setupInitialFields() {
        // Adiciona os 3 campos iniciais
        for (i in 0 until 3) {
            addKeyField()
        }
    }

    private fun addKeyField() {
        if (keyCount >= MAX_KEYS) {
            Toast.makeText(this, "Máximo de $MAX_KEYS chaves atingido", Toast.LENGTH_SHORT).show()
            return
        }

        val editText = TextInputEditText(this).apply {
            hint = "Cole sua chave Groq aqui (gsk_...)"
            maxLines = 1
            setPadding(48, 36, 48, 36)
            setBackgroundResource(R.drawable.bg_edit_text)
            id = View.generateViewId()
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 24
        }

        editText.layoutParams = params
        binding.keysContainer.addView(editText)
        keyFields.add(editText)
        keyCount++
        
        // Rola para o novo campo
        binding.scrollView.fullScroll(View.FOCUS_DOWN)
    }

    private fun setupButtons() {
        binding.btnAddKey.setOnClickListener {
            addKeyField()
        }

        binding.btnSave.setOnClickListener {
            saveKeys()
        }

        binding.btnSkip.setOnClickListener {
            Toast.makeText(
                this,
                "Você pode configurar depois em Configurações",
                Toast.LENGTH_LONG
            ).show()
            proceedToChat()
        }
        
        // Link para tutorial
        binding.txtTutorial.setOnClickListener {
            openGroqTutorial()
        }
    }

    private fun saveKeys() {
        val keys = keyFields.mapNotNull { it.text?.toString()?.trim() }
            .filter { it.isNotBlank() }

        when {
            keys.isEmpty() -> {
                Toast.makeText(this, "Adicione pelo menos uma chave", Toast.LENGTH_SHORT).show()
            }
            keys.size < MIN_KEYS -> {
                Toast.makeText(
                    this,
                    "Mínimo de $MIN_KEYS chaves recomendado para evitar travamentos. Você adicionou ${keys.size}.",
                    Toast.LENGTH_LONG
                ).show()
                // Permite salvar mesmo assim, mas avisa
                // saveKeysInternal(keys)
            }
            else -> {
                saveKeysInternal(keys)
            }
        }
    }

    private fun saveKeysInternal(keys: List<String>) {
        val success = apiKeyManager.saveKeys(keys)
        
        if (success) {
            Toast.makeText(
                this,
                "${keys.size} chaves salvas com sucesso! Nyx AI está pronta.",
                Toast.LENGTH_LONG
            ).show()
            proceedToChat()
        } else {
            Toast.makeText(
                this,
                "Erro ao salvar chaves. Verifique se todas começam com 'gsk_'",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun proceedToChat() {
        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }

    private fun openGroqTutorial() {
        // Abre link do tutorial no navegador
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://console.groq.com/keys")
        }
        startActivity(intent)
    }
}
