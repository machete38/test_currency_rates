package com.machete3845.test_currency_rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {


    lateinit var rv: RecyclerView
    lateinit var tv: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.rv)
        tv = findViewById(R.id.tv_top)
        updateData()
    }

    private fun updateData() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cbr-xml-daily.ru/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        CoroutineScope(Dispatchers.Main).launch {
            val api = retrofit.create(CurrApi::class.java)
            val model = api.readJson()
            rv.layoutManager = GridLayoutManager(applicationContext, 1)
            rv.adapter = CurrencyAdapter(model.valute)

        }

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

}