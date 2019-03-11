package ca.qc.mtl.mohaila.kotlinsoquestions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = Adapter()
    }

    override fun onResume() {
        super.onResume()
        getQuestions()
    }

    private fun getQuestions() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(BASE_URL).openConnection() as HttpURLConnection
                try {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val questions = Gson().fromJson(reader, Questions::class.java)
                    reader.close()

                    CoroutineScope(Dispatchers.Main).launch {
                        val adapter = recyclerview.adapter as Adapter
                        adapter.setQuestions(questions.items)
                    }
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@MainActivity, R.string.so_parse_error, Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@MainActivity, R.string.internet_error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val BASE_URL =
            "https://api.stackexchange.com/2.1/questions?order=desc&sort=creation&site=stackoverflow&tagged=kotlin"
    }
}
