package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentDetailBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.item_main.*
import kotlinx.coroutines.NonDisposableHandle.parent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailFragment(): Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: GitHubUserViewModel by activityViewModels()
    private lateinit var closeButton: ImageView
    private val repositoryAPI by lazy {
        RetrofitInstance.retrofit.create(GitHubRepositoryAPI::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        closeButton = binding.imageView1
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        closeButton.setOnClickListener{
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.login.let {
            val text1 = binding.textView1
            text1.text = it
            if (it != null) {
                repositoryAPI.getGitHubRepositoryData(it,"updated","desc",100,1)
                    .enqueue(object : Callback<Array<GitHubRepositoryResponse>> {
                        override fun onFailure(call: Call<Array<GitHubRepositoryResponse>>?, t: Throwable?) {
                            Toast.makeText(
                                activity,
                                "load error",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onResponse(call: Call<Array<GitHubRepositoryResponse>>?, response: Response<Array<GitHubRepositoryResponse>>) {
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    val repositoryLanguageList: ArrayList<String> = arrayListOf()
                                    response.body()!!.forEach { it ->
                                        repositoryLanguageList.add(it.language)
                                    }
                                    val repositoryLanguageMap: Map<String, Int> = repositoryLanguageList.groupingBy { it }.eachCount()
                                    val dimensions = repositoryLanguageMap.keys.toList()
                                    val values = repositoryLanguageMap.values.map { num -> num.toFloat() }.toList()
                                    val entryList = mutableListOf<PieEntry>()
                                    for(i in values.indices){
                                        entryList.add(
                                            PieEntry(values[i], dimensions[i])
                                        )
                                    }
                                    val pieDataSet = PieDataSet(entryList, "candle")
                                    pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                                    val pieData = PieData(pieDataSet)
                                    val pieChart = binding.pieChart1
                                    pieChart.data = pieData
                                    pieChart.legend.isEnabled = false
                                    pieChart.isClickable = true
                                    pieChart.invalidate()

                                    pieChart.setOnChartValueSelectedListener(object :
                                        OnChartValueSelectedListener {
                                        override fun onNothingSelected() { }

                                        override fun onValueSelected(e: Entry?, h: Highlight?) {

                                            Toast.makeText(
                                                activity,
                                                h.toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    })
                                }
                            }
                        }
                    })
            }
        }
        sharedViewModel.html.let{
            val text2 = binding.textView2
            val url = it
            text2.text = url
            text2.setOnClickListener {
                openUrl(url)
            }
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }

    private fun openUrl(url: String?){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        activity?.startActivity(intent)
    }
}