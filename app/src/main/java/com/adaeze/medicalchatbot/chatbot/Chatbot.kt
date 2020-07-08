//package com.adaeze.medicalchatbot.chatbot//package com.adaeze.medicalchatbot.chatbot
//
//import android.app.Dialog
//import android.content.Intent
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.os.Handler
//import android.view.View
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.adaeze.medicalchatbot.R
//import com.adaeze.medicalchatbot.user.HomePage
//import com.adaeze.medicalchatbot.user.Meeting
//import com.github.bassaer.chatmessageview.model.ChatUser
//import com.github.bassaer.chatmessageview.model.Message
//import com.github.bassaer.chatmessageview.view.ChatView
//import com.github.kittinunf.fuel.Fuel
//import com.github.kittinunf.fuel.core.FuelManager
//import com.github.kittinunf.fuel.json.responseJson
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.database.DatabaseReference
//import java.util.*
//
//class Chatbot : AppCompatActivity() {
//    private var mChatView: ChatView? = null
//    private var agent: ChatUser? = null
//    private var human: ChatUser? = null
//    private val mAuth: FirebaseAuth? = null
//    private val mUsers: DatabaseReference? = null
//    private val cUsers: FirebaseUser? = null
//    private val uid: String? = null
//    private val email: String? = null
//    private var names: String? = null
//    private val phone: String? = null
//    private val age: String? = null
//    private var popDialog: Dialog? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chatbot)
//
//        FuelManager.instance.baseHeaders = mapOf( "Authorization" to "Bearer $ACCESS_TOKEN")
//        FuelManager.instance.basePath = "https://api.dialogflow.com/v2/"
//        FuelManager.instance.baseParams = listOf(
//                "v" to "20170712",                  // latest protocol
//                "sessionId" to UUID.randomUUID(),   // random ID
//                "lang" to "en"                      // English language
//        )
//
//        popDialog = Dialog(this@Chatbot)
//
//        try {
//            val intent = intent
//            val bundle = intent.extras
//            if (bundle != null) {
//                names = bundle.getString("names")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        human = ChatUser(
//                0,
//                "Me",
//                BitmapFactory.decodeResource(resources,
//                        R.drawable.medcare)
//        )
//
//        agent = ChatUser(
//                1,
//                "Medcare",
//                BitmapFactory.decodeResource(resources,
//                        R.drawable.medcare)
//        )
//        mChatView = findViewById<View>(R.id.chat_view) as ChatView
//
//        //Set UI parameters if you need
//        mChatView!!.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500))
//        mChatView!!.setLeftBubbleColor(Color.WHITE)
//        mChatView!!.setBackgroundColor(ContextCompat.getColor(this, R.color.blueGray500))
//        mChatView!!.setSendButtonColor(ContextCompat.getColor(this, R.color.cyan900))
//        mChatView!!.setSendIcon(R.drawable.ic_action_send)
//        mChatView!!.setRightMessageTextColor(Color.WHITE)
//        mChatView!!.setLeftMessageTextColor(Color.BLACK)
//        mChatView!!.setUsernameTextColor(Color.WHITE)
//        mChatView!!.setSendTimeTextColor(Color.WHITE)
//        mChatView!!.setDateSeparatorColor(Color.WHITE)
//        mChatView!!.setInputTextHint("new message...")
//        mChatView!!.setMessageMarginTop(5)
//        mChatView!!.setMessageMarginBottom(5)
//        botResponse("Hello $names Welcome! I am MedCare, Your online Assistant.")
//
//        //Click Send Button
//        mChatView!!.setOnClickSendButtonListener(View.OnClickListener { //new message
//            mChatView!!.send(Message.Builder()
//                    .setUser(human!!)
//                    .setRight(true)
//                    .hideIcon(true)
//                    .setUsernameVisibility(true)
//                    .setUserIconVisibility(false)
//                    .setText(mChatView!!.inputText)
//                    .build()
//            )
//            //Reset edit text
//            mChatView!!.inputText = ""
//            Fuel.get("/query", listOf("query" to mChatView!!.inputText))
//                    .responseJson { _, _, result ->
//                        val reply = result.get().obj()
//                                .getJSONObject("result")
//                                .getJSONObject("fulfillment")
//                                .getString("speech")
//
//                        mChatView!!.send(Message.Builder()
//                                .setRight(false)
//                                .hideIcon(false)
//                                .setUser(agent!!)
//                                .setText(reply)
//                                .setUsernameVisibility(true)
//                                .setUserIconVisibility(true)
//                                .build()
//                        )
//                        }
//                    })
//
//
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//    }
//
//    fun botResponse(text: String?) {
//        val receivedMessage = Message.Builder()
//                .setUser(agent!!)
//                .setRight(false)
//                .hideIcon(false)
//                .setUsernameVisibility(true)
//                .setUserIconVisibility(true)
//                .setText(text!!)
//                .build()
//        mChatView?.send(receivedMessage)
//
//    }
//
//    fun showPopup(view: Int) {
//        val txtclose: TextView
//        val display: Button
//        popDialog!!.setContentView(view)
//        txtclose = popDialog!!.findViewById(R.id.txtclose)
//        txtclose.text = "X"
//        display = popDialog!!.findViewById(R.id.meet_doctor)
//        display.setOnClickListener {
//            val toReg = Intent(this@Chatbot, Meeting::class.java)
//            toReg.putExtra("myName", names)
//            toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            toReg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(toReg)
//        }
//        txtclose.setOnClickListener { popDialog!!.dismiss() }
//        popDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        popDialog!!.show()
//    }
//
//    fun showPopup2(view: Int) {
//        val txtclose: TextView
//        val display: Button
//        popDialog!!.setContentView(view)
//        txtclose = popDialog!!.findViewById(R.id.txtclose2)
//        txtclose.text = "X"
//        display = popDialog!!.findViewById(R.id.close_section)
//        display.setOnClickListener {
//            val toReg = Intent(this@Chatbot, HomePage::class.java)
//            toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            toReg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(toReg)
//        }
//        txtclose.setOnClickListener { popDialog!!.dismiss() }
//        popDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        popDialog!!.show()
//    }
//
//    fun newDelay() {
//        val sendDelay = (Random().nextInt(3) + 1) * 1000
//        Handler().postDelayed({ }, sendDelay.toLong())
//    }
//
//    companion object {
//        private const val ACCESS_TOKEN = "4faeb3410c81b7ca8db6589fd471332857924988"
//    }
//}