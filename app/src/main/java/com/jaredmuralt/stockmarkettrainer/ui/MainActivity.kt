package com.jaredmuralt.stockmarkettrainer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jaredmuralt.stockmarkettrainer.R
import com.jaredmuralt.stockmarkettrainer.api.RetrofitUtils
import com.jaredmuralt.stockmarkettrainer.api.StockAPIEndpoints
import com.jaredmuralt.stockmarkettrainer.model.GameState
import com.jaredmuralt.stockmarkettrainer.model.StockData
import com.jaredmuralt.stockmarkettrainer.util.DateValueFormatter
import com.jaredmuralt.stockmarkettrainer.util.StockSymbols
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    enum class MainStates {
        LOADING, LOADED, ERROR
    }

    private val INIT_MONEY = 10000f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        loadGameState()

        getStockHistory(StockSymbols.getRandomSymbol())
    }

    private fun saveGameState(addToHistory : Boolean) {
        val mPrefs = getSharedPreferences(getString(R.string.prefs_id), Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        if(addToHistory){
            val history = JSONArray(mPrefs.getString(getString(R.string.preference_history), "[]"))
            history.put(GameState.money.toString())
            prefsEditor.putString(getString(R.string.preference_history), history.toString())
        }
        prefsEditor.putFloat(getString(R.string.preference_money), GameState.money)
        prefsEditor.putInt(getString(R.string.preference_turns), GameState.turn)
        prefsEditor.apply()
    }

    private fun loadGameState(){
        val mPrefs = getSharedPreferences(getString(R.string.prefs_id), Context.MODE_PRIVATE)
        GameState.money = mPrefs.getFloat(getString(R.string.preference_money), 10000f)
        GameState.turn = mPrefs.getInt(getString(R.string.preference_turns), 1)
    }

    private fun resetHistory() {
        val mPrefs = getSharedPreferences(getString(R.string.prefs_id), Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        prefsEditor.putString(getString(R.string.preference_history), "[]")
        prefsEditor.apply()
    }

    private fun setState(state: MainStates) {
        when (state) {
            MainStates.LOADED -> {
                progressBar.visibility = View.GONE
                invalidSymbol.visibility = View.VISIBLE
                chartLayout.visibility = View.VISIBLE
                bottomButtons.visibility = View.VISIBLE
                invalidSymbol.visibility = View.GONE
                nextButton.visibility = View.GONE
            }
            MainStates.LOADING -> {
                progressBar.visibility = View.VISIBLE
                invalidSymbol.visibility = View.GONE
                chartLayout.visibility = View.GONE
                bottomButtons.visibility = View.GONE
                invalidSymbol.visibility = View.GONE
                chart.clear()
            }
            MainStates.ERROR -> {
                progressBar.visibility = View.GONE
                invalidSymbol.visibility = View.GONE
                chartLayout.visibility = View.GONE
                bottomButtons.visibility = View.GONE
                invalidSymbol.visibility = View.VISIBLE
                chart.clear()
            }
        }
    }

    private fun getStockHistory(symbol: String) {
        setState(MainStates.LOADING)

        val request = RetrofitUtils.buildService(StockAPIEndpoints::class.java)

        val from  = "0"
        val to = Calendar.getInstance().timeInMillis.toString().dropLast(3)
        val call = request.getStockHistory(
            symbol = symbol,
            resolution = "W",
            from = from,
            to = to,
            key = getString(R.string.api_key)
        )

        call.enqueue(object : Callback<StockData> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<StockData>, response: Response<StockData>) {
                if (response.isSuccessful) {
                    val stockData = response.body()!!

                    if (!stockData.c.isNullOrEmpty()) {

                        val startWeekIndex = (10 until stockData.t.size-55).random()
                        val endWeekIndex = (startWeekIndex + 10 until startWeekIndex + 50).random()
                        val numWeeks = endWeekIndex - startWeekIndex

                        val entries: ArrayList<Entry> = ArrayList()
                        val combinedDates = ArrayList<String>()
                        for ((index, data) in stockData.c.withIndex()) {
                            entries.add(
                                Entry(stockData.t[index].toFloat(), data.toFloat())
                            )
                            combinedDates.add(stockData.t[index].toString())
                        }

                        // history line
                        val pastDataSet = LineDataSet(entries.subList(0, startWeekIndex), getString(
                            R.string.past
                        ))
                        pastDataSet.color = getColor(R.color.pastBlue)
                        pastDataSet.lineWidth = 3f
                        pastDataSet.setDrawCircles(false)
                        pastDataSet.setDrawValues(false)

                        // future line
                        val futureDataSet = LineDataSet(entries.subList(startWeekIndex-1, endWeekIndex), getString(
                            R.string.future
                        ))
                        if(stockData.c[startWeekIndex] > stockData.c[endWeekIndex])
                            futureDataSet.color = getColor(R.color.declineRed)
                        else futureDataSet.color = getColor(R.color.acceptGreen)
                        futureDataSet.lineWidth = 3f
                        futureDataSet.setDrawCircles(false)
                        futureDataSet.setDrawValues(false)

                        // setup chart
                        val chartData = LineData()
                        chartData.addDataSet(pastDataSet)
                        chart.data = chartData

                        chart.animateXY(500, 500)

                        val desc = Description()
                        desc.isEnabled = false

                        val l = chart.legend
                        l.formSize = 12f
                        l.form = Legend.LegendForm.CIRCLE
                        l.textSize = 14f
                        l.textColor = Color.BLACK
                        l.xEntrySpace = 14f

                        chart.xAxis.valueFormatter = DateValueFormatter()

                        chart.description = desc
                        chart.setPinchZoom(false)
                        chart.isDoubleTapToZoomEnabled = false
                        chart.setNoDataTextColor(Color.TRANSPARENT)
                        chart.setScaleEnabled(false)
                        chart.setNoDataText("")

                        // load results
                        showResults(symbol, numWeeks, pastDataSet, null)
                        setState(MainStates.LOADED)

                        // set up buttons
                        acceptButton.setOnClickListener {
                            var percentChange = (stockData.c[endWeekIndex].toFloat() / stockData.c[startWeekIndex].toFloat())
                            if(percentChange.isNaN()) percentChange = 1f
                            GameState.money *= percentChange.toFloat()
                            nextButton.text = NumberFormat.getPercentInstance(Locale.US)
                                .format((percentChange - 1)) + " - NEXT"
                            showResults(symbol, numWeeks, pastDataSet, futureDataSet)
                            saveGameState(true)

                            // confetti
                            if(percentChange > 1) {
                                viewKonfetti.build()
                                    .addColors(getColor(R.color.acceptGreen))
                                    .setDirection(0.0, 359.0)
                                    .setSpeed(20f, 30f)
                                    .setFadeOutEnabled(true)
                                    .setTimeToLive(1000L)
                                    .addShapes(Shape.Square, Shape.Circle)
                                    .addSizes(Size(12))
                                    .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                                    .streamFor(300, 500L)
                            }
                        }

                        declineButton.setOnClickListener {
                            nextButton.text = getString(R.string.next)
                            showResults(symbol, numWeeks, pastDataSet, futureDataSet)
                            saveGameState(true)
                        }

                        nextButton.setOnClickListener {
                            // next turn
                            GameState.turn++
                            setState(MainStates.LOADING)
                            saveGameState(false)
                            getStockHistory(StockSymbols.getRandomSymbol())
                        }

                    } else {
                        getStockHistory(StockSymbols.getRandomSymbol())
                    }
                } else {
                    getStockHistory(StockSymbols.getRandomSymbol())
                }
            }

            override fun onFailure(call: Call<StockData>, t: Throwable) {
                // stock load failed, try another stock
                getStockHistory(StockSymbols.getRandomSymbol())
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showResults(symbol: String, numWeeks: Int, pastDataSet : LineDataSet, futureDataSet : LineDataSet?) {
        // update chart with future values
        val chartData = LineData()
        chartData.addDataSet(pastDataSet)
        chartData.addDataSet(futureDataSet)
        chart.data = chartData
        chart.invalidate()

        // update all other views
        stock_label.text = "$symbol - $numWeeks Weeks"
        total_money.text =
            NumberFormat.getCurrencyInstance(Locale.US).format(GameState.money)
        total_percent_return.text = NumberFormat.getPercentInstance(Locale.US)
            .format((GameState.money - INIT_MONEY) / INIT_MONEY) + " Return"
        total_turns.text = "Turn ${GameState.turn}"
        nextButton.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_reset -> {
                MaterialDialog(this).title(R.string.reset_game)
                    .message(R.string.reset_message).show {
                        positiveButton(android.R.string.yes) {

                            //clear history
                            resetHistory()

                            //reset game state
                            GameState.money = INIT_MONEY
                            GameState.turn = 1
                            saveGameState(false)

                            getStockHistory(StockSymbols.getRandomSymbol())
                        }
                        negativeButton(android.R.string.cancel)
                    }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}