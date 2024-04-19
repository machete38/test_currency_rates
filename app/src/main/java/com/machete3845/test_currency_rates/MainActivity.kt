package com.machete3845.test_currency_rates

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {


    var paused: Boolean = false
    var alert: AlertDialog? = null
    var handler: Handler? = null

    lateinit var rv: RecyclerView
    lateinit var tv: TextView
    lateinit var pb: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.rv)
        tv = findViewById(R.id.tv_top)
        pb = findViewById(R.id.pb)
        updateData()

        rv.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        );
    }

    //Первичное обновление данных
    private fun updateData() {
        alert?.dismiss()
        pb.visibility = View.VISIBLE
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cbr-xml-daily.ru/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val api = retrofit.create(CurrApi::class.java)
                val model = api.readJson()
                rv.layoutManager = GridLayoutManager(applicationContext, 1)
                rv.adapter = CurrencyAdapter(model.valute)
                pb.visibility = View.GONE
                val sdf = SimpleDateFormat("dd/MM/yyyy в HH:mm:ss")
                val currentDate = sdf.format(Date())
                tv.text = "Курсы валют - обновлено "+ currentDate
                upd()


            }
            catch (e: Exception)
            {
               println(e.printStackTrace())
                pb.visibility = View.GONE
                showAD()
                upd()
            }

        }

    }

    // Вторичное обновление данных (с задержкой)
    private fun upd() {
            handler?.removeCallbacksAndMessages(null)
            handler = Handler(Looper.getMainLooper())
            handler?.postDelayed(
                {
                    if (!paused) {
                        updateData()
                    }
                },
                30000
            )
    }

    // Показывает AlertDialog если данные не обновляются
    private fun showAD() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("К сожалению, обновить данные не получилось. Пожалуйста, проверьте подключение к интернету")
            .setCancelable(true).setNegativeButton("Попробовать снова", DialogInterface.OnClickListener { dialogInterface, i ->
                updateData()
            })
        alert?.dismiss()
        alert = builder.create()
        alert?.setTitle("Ошибка обновления данных")
        alert?.show()
    }


    class CurrencyAdapter(private val list: LinkedHashMap<String, Currency>): RecyclerView.Adapter<CurrAdVH>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrAdVH {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_curr_info, parent, false)
            return CurrAdVH(itemView)
        }

        override fun onBindViewHolder(holder: CurrAdVH, position: Int) {
            val key = list.keys.toList().get(position)
            val obj = list.get(key)

            val textTitle = obj?.nominal.toString()+" "+ (obj?.name ?: String) +" ("+ (obj?.charCode
                ?: String) +")"
            val textRate = "= "+ obj?.valute.toString()+" руб."

            holder.tv_title.text = textTitle
            holder.tv_rate.text = textRate

        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    class CurrAdVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title: TextView = itemView.findViewById(R.id.tv_curr_title)
        val tv_rate: TextView = itemView.findViewById(R.id.tv_curr_rate)
    }

    override fun onPause() {
        paused = true
        super.onPause()
    }

    override fun onResume() {
        paused = false
        updateData()
        super.onResume()
    }

}