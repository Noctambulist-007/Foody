package com.noctambulist.foody.fragments.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.Direction
import app.rive.runtime.kotlin.core.Loop
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.noctambulist.foody.R
import com.noctambulist.foody.activities.FoodyActivity
import com.noctambulist.foody.databinding.FragmentLoginBinding
import com.noctambulist.foody.util.Resource
import com.noctambulist.foody.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private var isPasswordVisible = false
    private lateinit var mailLayout: TextInputLayout
    private lateinit var passLayout: TextInputLayout
    private lateinit var mailEditText: TextInputEditText
    private lateinit var passEditText: TextInputEditText
    private lateinit var loginButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            view,
                            "Reset link was sent to your email.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(view, "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {

                        Intent(requireContext(), FoodyActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_LONG)
                            .show()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupBottomSheetDialog(resetPasswordCallback: (String) -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.reset_password_dialog, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        val buttonSend = bottomSheetView.findViewById<Button>(R.id.buttonSendResetPassword)
        val editTextEmail = bottomSheetView.findViewById<EditText>(R.id.editResetPassword)

        buttonSend.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPasswordCallback(email)
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Email cannot be empty", Toast.LENGTH_LONG).show()
            }
        }

        val buttonCancel = bottomSheetView.findViewById<Button>(R.id.buttonCancelResetPassword)
        buttonCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        passEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.animate.play("hands_down", Loop.ONESHOT, Direction.BACKWARDS)
            } else {
                binding.animate.play("Hands_up", Loop.ONESHOT, Direction.BACKWARDS)
            }
        }

        passLayout.setEndIconOnClickListener {
            togglePassVisibility()
        }

        mailEditText.addTextChangedListener {
            binding.animate.play("Login Machine", Loop.AUTO, Direction.AUTO, true)
            binding.animate.setBooleanState("Login Machine", "isChecking", true)
            (it?.length?.times(1.5))?.toFloat()
                ?.let { it1 -> binding.animate.setNumberState("Login Machine", "numLook", it1) }
        }

        binding.animate.setOnTouchListener { view, _ ->
            hideKeyboard(view)
            clearFocus()
            true
        }

        passEditText.setOnEditorActionListener { tView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(tView.windowToken, 0)
                passEditText.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun togglePassVisibility() {
        if (isPasswordVisible) {
            val pass: String = passEditText.text.toString()
            passEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            passEditText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passEditText.setText(pass)
            passEditText.setSelection(pass.length)
            binding.animate.play("hands_down", Loop.ONESHOT, Direction.BACKWARDS)

        } else {
            val pass: String = passEditText.text.toString()
            passEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passEditText.inputType = InputType.TYPE_CLASS_TEXT
            passEditText.setText(pass)
            passEditText.setSelection(pass.length)
            binding.animate.play("Hands_up", Loop.ONESHOT, Direction.BACKWARDS)
        }
        isPasswordVisible = !isPasswordVisible
    }

    private fun initView() {
        mailLayout = binding.emailLayout
        passLayout = binding.passwordLayout
        mailEditText = binding.emailInputId
        passEditText = binding.passwordInputId
        loginButton = binding.loginBtn

        loginButton.setOnClickListener {
            login()
        }

        binding.container.setOnClickListener {
            onClickOutside()
        }

        binding.registerBtn.setOnClickListener {
            register()
        }

        binding.tvForgotPassword.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginBtn -> {
                login()
            }

            R.id.container -> {
                onClickOutside()
            }

            R.id.registerBtn -> {
                register()
            }
        }
    }

    private fun register() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun login() {
        val email = mailEditText.text.toString().trim()
        val password = passEditText.text.toString()

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(requireContext(), "Email and password are required", Toast.LENGTH_SHORT)
                .show()
            return
        } else if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_SHORT).show()
            return
        } else if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Password is required", Toast.LENGTH_SHORT).show()
            return
        }

        binding.animate.pause()
        if (passEditText.isFocused) {
            binding.animate.play("Hands_up", Loop.ONESHOT, Direction.BACKWARDS)
            passEditText.clearFocus()
        }

        viewModel.login(email, password)

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        val user = resource.data
                        binding.animate.play("success", Loop.AUTO, Direction.AUTO)

                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().popBackStack(R.id.loginFragment, true)
                            Intent(requireContext(), FoodyActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }, 1000)
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Wrong email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.animate.play("fail", Loop.ONESHOT, Direction.BACKWARDS)
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }
    }

    private fun onClickOutside() {
        hideKeyboard(binding.container)
        clearFocus()
    }

    private fun clearFocus() {
        mailEditText.clearFocus()
        passEditText.clearFocus()
        binding.animate.setBooleanState("Login Machine", "isChecking", false)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}