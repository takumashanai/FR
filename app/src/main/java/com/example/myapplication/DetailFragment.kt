package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentDetailBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailFragment(): Fragment() {
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

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val closeButton = binding.imageView1
        closeButton.setOnClickListener{
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
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
                                    response.body()!!.forEach { body ->
                                        repositoryLanguageList.add(body.language)
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
                                    pieChart.animateY(1000, Easing.EaseInOutCubic)
                                    pieChart.description = null
                                    pieChart.legend.isEnabled = false
                                    pieChart.isClickable = true
                                    pieChart.invalidate()

                                    pieChart.setOnChartValueSelectedListener(object :
                                        OnChartValueSelectedListener {
                                        override fun onNothingSelected() { }

                                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                                            val text3 = binding.textView3
                                            val language = entryList[h?.x?.toInt()!!].label
                                            text3.text = if(language == null) text3.context.getString(R.string.language,"None stated") else text3.context.getString(R.string.language,language)
                                            val detailUserList: ArrayList<DetailUser> = arrayListOf()
                                            response.body()!!.forEach { it ->
                                                if(it.language == language) {
                                                    detailUserList.add(DetailUser(
                                                        id = it.id,
                                                        title = it.title,
                                                        html = it.html,
                                                        homepage = it.homepage,
                                                        description = it.description,
                                                        star = it.star
                                                    ))
                                                }
                                            }
                                            val colorInfo = ColorTemplate.COLORFUL_COLORS.toList()[h.x.toInt()%5]
                                            val view1 = binding.view1
                                            text3.setTextColor(colorInfo)
                                            view1.setBackgroundColor(colorInfo)
                                            sharedViewModel.detailUserList?.value = detailUserList
                                        }
                                    })

                                    sharedViewModel.avatar.let{ url ->
                                        val image2 = binding.imageView2
                                        Glide.with(image2.context)
                                            .load(url)
                                            .circleCrop()
                                            .into(image2)
                                    }
                                }
                            }
                        }
                    })
            }
        }

        val adapter = DetailUserAdapter(DetailUserAdapter.UserComparator)
        val recyclerView = binding.recyclerView1
        val layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val itemDecoration = DividerItemDecoration(activity,layoutManager.orientation)
        itemDecoration.setDrawable(ColorDrawable(resources.getColor(R.color.black,null)))
        recyclerView.addItemDecoration(itemDecoration)

        sharedViewModel.detailUserList?.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

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
        sharedViewModel.detailUserList?.value = null
        sharedViewModel.setLogin(null)
        sharedViewModel.setAvatar(null)
        sharedViewModel.setHtml(null)
    }

    private fun openUrl(url: String?){
        var webpage = Uri.parse(url)
        if (url != null) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://$url");
            }
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = webpage
        activity?.startActivity(intent)
    }
}