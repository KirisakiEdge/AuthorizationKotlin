package com.example.authorizationkotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.register_window.view.*
import kotlinx.android.synthetic.main.sign_in_window.view.*
import kotlin.collections.Map


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth     //lateinit - late initialization
    private lateinit var db: FirebaseDatabase
    private lateinit var users: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        users = db!!.reference!!.child("Users")  // !!- не є null!!!

        val currentUser = auth.currentUser

        register.setOnClickListener {
            showRegisterWindow()
        }

        sign_in.setOnClickListener {
            showSignInWindow()
        }

    }

    private fun showSignInWindow() {
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enter")
        val signInWindow = layoutInflater.inflate(R.layout.sign_in_window, null)
        dialog.setView(signInWindow)

        val email = signInWindow.emailFieldS
        val password = signInWindow.passwordFieldS

        dialog.setNegativeButton("Close", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        dialog.setPositiveButton("Enter", DialogInterface.OnClickListener { dialog, which ->
            if (TextUtils.isEmpty(email.text.toString())) {
                Snackbar.make(root, "Enter Email", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (password.length() < 6) {
                Snackbar.make(root, " More than 6 symbols", Snackbar.LENGTH_SHORT).show()
                return@OnClickListener
            }
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful) {
                        startActivity(Intent(this, Map::class.java))
                        finish()
                    }else {
                        Snackbar.make(root, "Wrong Email or password", Snackbar.LENGTH_LONG ).show() }
                }
        })
        dialog.show()
    }

    private fun showRegisterWindow() {
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("Registration")
        val registerWindow = layoutInflater.inflate(R.layout.register_window, null)
        dialog.setView(registerWindow)

        val email = registerWindow.emailField
        val password = registerWindow.passwordField
        val name = registerWindow.nameFiled
        val surname = registerWindow.surnameField


        dialog.setNegativeButton("Close", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        dialog.setPositiveButton("Registration", DialogInterface.OnClickListener { dialog, which ->
            if (email.text.toString().isNotEmpty() && password.text.toString()
                    .isNotEmpty() && name.text.toString().isNotEmpty() && surname.text.toString()
                    .isNotEmpty()
            ) {
                /////////////////////////
                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this){task->
                            if (task.isSuccessful) {
                                val user = User(
                                    email.text.toString(),
                                    password.text.toString(),
                                    name.text.toString(),
                                    surname.text.toString()
                                )
//                            user.emailSet(email.text.toString())
//                            user.passwordSet(password.text.toString())
//                            user.nameSet(name.text.toString())
//                            user.surnameSet(surname.text.toString())

                                val useR = auth.currentUser!!.uid
                                val cUserDb = users.child(useR)
                                cUserDb.child("User").setValue(user)
                                Snackbar.make(
                                    root,
                                    "Successfully registered :)",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            } else {
                                Snackbar.make(
                                    root,
                                    "Error registering, try again later :(",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                    }
            }else {
                Snackbar.make(root,"Please fill up the Credentials :|", Snackbar.LENGTH_LONG).show()
            }
        })
        dialog.show()
    }
}
