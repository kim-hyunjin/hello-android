package com.example.workoutapp.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workoutapp.WorkOutApp
import com.example.workoutapp.data.history.HistoryDao
import com.example.workoutapp.databinding.ActivityHistoryBinding
import com.example.workoutapp.utils.extensions.gone
import com.example.workoutapp.utils.extensions.visible
import kotlinx.coroutines.launch

class HistoryActivity: AppCompatActivity() {

    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarHistoryActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "HISTORY"
        val dao = (application as WorkOutApp).db.historyDao()
        getAllHistoryData(dao)
    }

    private fun getAllHistoryData(historyDao: HistoryDao) {
        lifecycleScope.launch {
            historyDao.fetchALLDates().collect {
                if (it.isNotEmpty()) {
                    binding?.tvHistory?.visible()
                    binding?.rvHistory?.visible()
                    binding?.tvNoDataAvailable?.gone()

                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)
                    val dates = ArrayList<String>(it.map { historyEntity -> historyEntity.date }.toList())
                    val historyAdapter = HistoryAdapter(dates)
                    binding?.rvHistory?.adapter = historyAdapter

                } else {
                    binding?.tvHistory?.gone()
                    binding?.rvHistory?.gone()
                    binding?.tvNoDataAvailable?.visible()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}