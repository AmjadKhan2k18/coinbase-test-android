package com.example.coinbasetest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.coinbase.android.nativesdk.CoinbaseWalletSDK
import com.coinbase.android.nativesdk.message.request.Account
import com.coinbase.android.nativesdk.message.request.Web3JsonRPC
import com.coinbase.android.nativesdk.message.response.ActionResult
import com.example.coinbasetest.ui.theme.CoinbaseTestTheme

class MainActivity : ComponentActivity() {

    private lateinit var client : CoinbaseWalletSDK

    private fun initCoinbaseSdk() {

        Log.d("msg","==== init coinbase")

        client = CoinbaseWalletSDK(
            appContext = applicationContext,
            domain = Uri.parse("https://www.coinbase.com"),
            openIntent = { intent -> launcher.launch(intent) }
        )

        Log.d("msg","${client.isCoinbaseWalletInstalled}")
    }

    private  fun handshake() {

        try {
            val requestAccount = Web3JsonRPC.RequestAccounts().action()
            val handShakeActions = listOf(requestAccount)
            Log.d("msg" , "=== requestAccount $requestAccount")
            client.initiateHandshake(
                initialActions = handShakeActions
            ) { result: Result<List<ActionResult>>, account: Account? ->
                result.onSuccess { _: List<ActionResult> ->
                    Log.d("msg","=== ${account.toString()}")
                }
                result.onFailure { err ->
                    Log.d("msg","=== $err")
                }
            }
        }
        catch (exception: Exception) {
            Log.d("msg","=== ${exception.message}")
        }

    }
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initialize the launcher
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data ?: return@registerForActivityResult
            client.handleResponse(uri)
        }
        initCoinbaseSdk()
        handshake()
        super.onCreate(savedInstanceState)
        setContent {
            CoinbaseTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting( )
            }
        }
    }

}}

@Composable
fun Greeting() {
    Button(onClick = {


        val requestAccount = Web3JsonRPC.RequestAccounts().action()
        val handShakeActions = listOf(requestAccount)
        Log.d("msg" , "=== requestAccount $requestAccount")

    }
    ) {


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoinbaseTestTheme {
        Greeting()
    }
}