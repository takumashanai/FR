package com.example.myapplication.fragment

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.*
import com.example.myapplication.adapter.DetailUserAdapter
import com.example.myapplication.api.GitHubRepositoryAPI
import com.example.myapplication.data.GitHubRepositoryResponse
import com.example.myapplication.data.GitHubRepositoryUser
import com.example.myapplication.databinding.FragmentDetailBinding
import com.example.myapplication.db.AppDatabase
import com.example.myapplication.objects.RetrofitInstance
import com.example.myapplication.viewmodel.GitHubUserViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailFragment: Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: GitHubUserViewModel by activityViewModels()
    private val repositoryAPI by lazy {
        RetrofitInstance.retrofit.create(GitHubRepositoryAPI::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val text1 = binding.textView1
        val pieChart = binding.pieChart1
        val image1 = binding.imageView1
        val image2 = binding.imageView2
        val text3 = binding.textView3
        val text2 = binding.textView2
        val text4 = binding.textView4
        val view1 = binding.view1
        val database = activity?.let { AppDatabase.getDatabase(it) }
        val dao = database?.gitHubRepositoryDao()

        image2.clipToOutline = true
        sharedViewModel.login?.let { login ->
            text1.text = login
            text2.setOnClickListener {
                openUrl("https://github.com/${login}?tab=repositories")
            }
            text4.setOnClickListener {
                openUrl("https://github.com/${login}?tab=followers")
            }
            repositoryAPI.getGitHubRepositoryData(context?.let { Signature.getAccessToken(it) },login,"updated","desc",100,1)
                .enqueue(object : Callback<Array<GitHubRepositoryResponse>> {
                    override fun onFailure(call: Call<Array<GitHubRepositoryResponse>>?, t: Throwable?) {
                        Toast.makeText(
                            activity,
                            "load error",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onResponse(call: Call<Array<GitHubRepositoryResponse>>?, response: Response<Array<GitHubRepositoryResponse>>) {
                        if (response.isSuccessful && response.code() == 200) {
                            response.body()?.let {
                                val repositoryLanguageList: ArrayList<String> = arrayListOf()
                                response.body()!!.forEach { body ->
                                    repositoryLanguageList.add(body.language)
                                }
                                val repositoryLanguageMap: Map<String, Int> = repositoryLanguageList.groupingBy { item -> item }.eachCount()
                                val dimensions = repositoryLanguageMap.keys.toList()
                                val values = repositoryLanguageMap.values.map { num -> num.toFloat() }.toList()
                                val entryList = mutableListOf<PieEntry>()
                                for(i in values.indices){
                                    entryList.add(
                                        PieEntry(values[i], dimensions[i])
                                    )
                                }
                                val pieDataSet = PieDataSet(entryList, "candle")
                                pieDataSet.valueLinePart1OffsetPercentage = 100f
                                pieDataSet.sliceSpace = 3f
                                pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                                pieDataSet.setValueTextColors(ColorTemplate.COLORFUL_COLORS.toList())
                                pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                                pieDataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
                                pieDataSet.valueTextSize = 16f
                                pieDataSet.isUsingSliceColorAsValueLineColor = true

                                val pieData = PieData(pieDataSet)
                                pieData.setValueFormatter(MyValueFormatter())
                                values.minOrNull()?.let { min ->
                                    val minAngle = (min / values.sum()) * 360
                                    val minAngleForSlices = when{
                                        minAngle < minOf(15f,((360 / entryList.size)/1.3).toFloat()) ->
                                        {
                                            pieChart.setEntryLabelTextSize(14f)
                                            minOf(15f,((360 / entryList.size)/1.3).toFloat())
                                        }
                                        else -> {
                                            pieChart.setEntryLabelTextSize(18f)
                                            0f
                                        }
                                    }
                                    pieChart.minAngleForSlices = minAngleForSlices
                                }

                                pieChart.setExtraOffsets(0f, 10f, 0f, 10f)
                                pieChart.data = pieData
                                pieChart.setEntryLabelColor(requireActivity().resources.getColor(R.color.black,null))
                                pieChart.animateY(1000, Easing.EaseInOutCubic)
                                pieChart.description.text = "100 latest repositories"
                                pieChart.legend.isEnabled = false
                                pieChart.isClickable = true
                                pieChart.invalidate()

                                pieChart.setOnChartValueSelectedListener(object :
                                    OnChartValueSelectedListener {
                                    override fun onNothingSelected() { }

                                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                                        h?.let { high ->
                                            val language = entryList[high.x.toInt()].label
                                            sharedViewModel.setLanguage(language)
                                            val detailUserList: ArrayList<GitHubRepositoryUser> = arrayListOf()
                                            response.body()!!.forEach { item ->
                                                if(item.language == language) {
                                                    detailUserList.add(GitHubRepositoryUser(
                                                        id = item.id,
                                                        title = item.title,
                                                        html = item.html,
                                                        homepage = item.homepage,
                                                        description = item.description,
                                                        star = item.star
                                                    ))
                                                }
                                            }
                                            viewLifecycleOwner.lifecycleScope.launch {
                                                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                                    dao?.clearAll()
                                                    dao?.insertAll(detailUserList)
                                                }
                                            }
                                            sharedViewModel.setColorNum(ColorTemplate.COLORFUL_COLORS.toList()[high.x.toInt() % 5])
                                        }

                                    }
                                })

                                sharedViewModel.avatar?.let{ url ->
                                    Glide.with(image1.context)
                                        .load(url)
                                        .circleCrop()
                                        .into(image1)
                                }
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "${response.code()} error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        }
        sharedViewModel.language.observe(viewLifecycleOwner){
            it?.let { language ->
                text3.text = requireActivity().resources.getString(R.string.language, language)
            } ?: let{
                text3.text = requireActivity().resources.getString(R.string.init_language)
            }
        }

        sharedViewModel.colorNum.observe(viewLifecycleOwner){
            it?.let { color ->
                text3.setTextColor(color)
                view1.setBackgroundColor(color)
            }
        }

        sharedViewModel.repos?.let {
            text2.text = text2.context.getString(R.string.repos,it)
        }
        sharedViewModel.followers?.let {
            text4.text = text3.context.getString(R.string.followers,it)
        }

        val adapter = DetailUserAdapter(DetailUserAdapter.UserComparator)
        val recyclerView = binding.recyclerView1
        val layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val itemDecoration = DividerItemDecoration(activity,layoutManager.orientation)
        itemDecoration.setDrawable(ColorDrawable(requireActivity().resources.getColor(R.color.black,null)))
        recyclerView.addItemDecoration(itemDecoration)

        sharedViewModel.html?.let{
            image2.setOnClickListener { v ->
                openUrl(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dao?.getFlow()?.collectLatest(adapter::submitList)
            }
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }

    private fun openUrl(url: String){
        if(url.isNotBlank()) {
            var webpage = Uri.parse(url)
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://$url")
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = webpage
            activity?.startActivity(intent)
        }
    }
}
