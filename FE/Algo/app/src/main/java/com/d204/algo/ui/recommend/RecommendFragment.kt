package com.d204.algo.ui.recommend

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ToggleButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import com.d204.algo.ApplicationClass
import com.d204.algo.base.BaseFragment
import com.d204.algo.base.BaseViewModel
import com.d204.algo.data.model.Problem
import com.d204.algo.databinding.FragmentRecommendBinding
import com.d204.algo.presentation.utils.Constants
import com.d204.algo.presentation.viewmodel.RecommendFragmentViewModel
import com.d204.algo.presentation.viewmodel.RecommendUIModel
import com.d204.algo.ui.custom.RecommendProblemView
import com.d204.algo.ui.extension.observe
import com.d204.algo.ui.extension.showSnackBar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecommendFragment : BaseFragment<FragmentRecommendBinding, BaseViewModel>() {
    companion object {
        @JvmStatic
        fun newInstance() =
            RecommendFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun getViewBinding(): FragmentRecommendBinding =
        FragmentRecommendBinding.inflate(layoutInflater)

    override val viewModel: RecommendFragmentViewModel by viewModels()
    private val espHelper = ApplicationClass.preferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 뷰모델 변수에 옵저빙을 등록한다.
        observe(viewModel.getStrongs(), ::onViewStrongListChange)
        observe(viewModel.getWeaks(), ::onViewWeakListChange)
        observe(viewModel.getSimilars(), ::onViewSimilarListChange)

        // 추천 리스트에 옵저빙 등록
        observe(viewModel.getSelectedStrongs(), ::onViewStrongChange)
        observe(viewModel.getSelectedWeaks(), ::onViewWeakChange)
        observe(viewModel.getSelectedSimilars(), ::onViewSimilarChange)

        // 각 30개의 추천 문제 리스트를 서버에서 가져온다 -> postValue -> 변경되면 observe에서 인지하고 binding ui를 갱신
//        viewModel.getStrongList(espHelper.prefUserId)
//        viewModel.getWeakList(espHelper.prefUserId)
//        viewModel.getSimilarList(espHelper.prefUserId)
        viewModel.getStrongList(1)
        viewModel.getWeakList(1)
        viewModel.getSimilarList(1)

        // 리프레쉬 등록
        with(binding) {
            recommendStrongRefresh.setOnClickListener {
                viewModel.loadConstStrongList()
                setProblemAnimation(recommendStrongAnimLayout)
            }
            recommendWeakRefresh.setOnClickListener {
                viewModel.loadConstWeakList()
                setProblemAnimation(recommendWeakAnimLayout)
            }
            recommendLikeRefresh.setOnClickListener {
                viewModel.loadConstSimilarList()
                setProblemAnimation(recommendLikeAnimLayout)
            }
        }
    }

    private fun setProblemAnimation(layout: LinearLayoutCompat) {
        val animation = AnimationUtils.loadAnimation(context, com.d204.algo.R.anim.recommend_animation)
        animation.duration = 200
        val controller = LayoutAnimationController(animation)
        layout.layoutAnimation = controller
        layout.startLayoutAnimation()
    }

    private fun setProblem(v: RecommendProblemView, b: ToggleButton, problem: Problem) {
        val curResource = Constants.TIER[problem.problemLevel] // 여기에 잘못된 값이 들어가면 아래 부분도 실행안됨
        v.setDifficultyImage(curResource)
        v.setProblemNumber(problem.problemNumber.toString())
        v.setProblemTitle(problem.title)
        b.isChecked = problem.problemLike
    }

    private fun refreshStrong(list: List<Problem>) {
        Log.d("서버", "refreshStrong: $list")
        try {
            with(binding) {
                for (i in 0 until minOf(list.size, 3)) {
                    setProblem(
                        when (i) {
                            0 -> recommendStrong1
                            1 -> recommendStrong2
                            2 -> recommendStrong3
                            else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                        },
                        when (i) {
                            0 -> recommendStrongBookmarkButton1
                            1 -> recommendStrongBookmarkButton2
                            2 -> recommendStrongBookmarkButton3
                            else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                        },
                        list[i],
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("서버", "refreshStrong: $e")
            showSnackBar(binding.root, "서버가 혼잡합니다. 나중에 다시 시도해주세요.")
        }
    }

    private fun refreshWeak(list: List<Problem>) {
        try {
            with(binding) {
                for (i in 0 until minOf(list.size, 3)) {
                    setProblem(
                        when (i) {
                            0 -> recommendWeak1
                            1 -> recommendWeak2
                            2 -> recommendWeak3
                            else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                        },
                        when (i) {
                            0 -> recommendWeakBookmarkButton1
                            1 -> recommendWeakBookmarkButton2
                            2 -> recommendWeakBookmarkButton3
                            else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                        },
                        list[i],
                    )
                }
            }
        } catch (e: Exception) {
            showSnackBar(binding.root, "서버가 혼잡합니다. 나중에 다시 시도해주세요.")
        }
    }

    private fun refreshSimilar(list: List<Problem>) {
        try {
            with(binding) {
                for (i in 0 until minOf(list.size, 3)) {
                    setProblem(
                        when (i) {
                            0 -> recommendLike1
                            1 -> recommendLike2
                            2 -> recommendLike3
                            else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                        },
                        when (i) {
                            0 -> recommendLikeBookmarkButton1
                            1 -> recommendLikeBookmarkButton2
                            2 -> recommendLikeBookmarkButton3
                            else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                        },
                        list[i],
                    )
                }
            }
        } catch (e: Exception) {
            showSnackBar(binding.root, "서버가 혼잡합니다. 나중에 다시 시도해주세요.")
        }
    }

    private fun onViewStrongListChange(result: RecommendUIModel) {
        if (result.isRedelivered) return
        when (result) {
            is RecommendUIModel.Error -> handleErrorMessage(result.error)
            is RecommendUIModel.Loading -> handleLoading(true)
            is RecommendUIModel.Success -> {
                handleLoading(false)
                viewModel.setConstStrongs(result.data) // 정적리스트에 담아주고 // // setConstStrongs() 안에 loadConstStrongList() 정적리스트 가져오면  3개만 담는 추천 리스트에 postValue
            }
        }
    }

    private fun onViewWeakListChange(result: RecommendUIModel) {
        if (result.isRedelivered) return
        when (result) {
            is RecommendUIModel.Error -> handleErrorMessage(result.error)
            is RecommendUIModel.Loading -> handleLoading(true)
            is RecommendUIModel.Success -> {
                handleLoading(false)
                viewModel.setConstWeaks(result.data) // 정적리스트에 담아주고
            }
        }
    }

    private fun onViewSimilarListChange(result: RecommendUIModel) {
        if (result.isRedelivered) return
        when (result) {
            is RecommendUIModel.Error -> handleErrorMessage(result.error)
            is RecommendUIModel.Loading -> handleLoading(true)
            is RecommendUIModel.Success -> {
                handleLoading(false)
                viewModel.setConstSimilars(result.data) // 정적리스트에 담아주고
            }
        }
    }

    private fun onViewStrongChange(result: RecommendUIModel) {
        if (result.isRedelivered) return
        when (result) {
            is RecommendUIModel.Error -> handleErrorMessage(result.error)
            is RecommendUIModel.Loading -> handleLoading(true)
            is RecommendUIModel.Success -> {
                handleLoading(false)
                val subList = result.data.subList(0, minOf(result.data.size, 3))
                refreshStrong(subList)
                setStrongBookMarkClicker(subList)
            }
        }
    }

    private fun onViewWeakChange(result: RecommendUIModel) {
        if (result.isRedelivered) return
        when (result) {
            is RecommendUIModel.Error -> handleErrorMessage(result.error)
            is RecommendUIModel.Loading -> handleLoading(true)
            is RecommendUIModel.Success -> {
                handleLoading(false)
                val subList = result.data.subList(0, minOf(result.data.size, 3))
                refreshWeak(subList)
                setWeakBookMarkClicker(subList)
            }
        }
    }

    private fun onViewSimilarChange(result: RecommendUIModel) {
        if (result.isRedelivered) return
        when (result) {
            is RecommendUIModel.Error -> handleErrorMessage(result.error)
            is RecommendUIModel.Loading -> handleLoading(true)
            is RecommendUIModel.Success -> {
                handleLoading(false)
                val subList = result.data.subList(0, minOf(result.data.size, 3))
                refreshSimilar(subList)
                setSimilarBookMarkClicker(subList)
            }
        }
    }

    private fun bookmarkClicker(v: RecommendProblemView, b: ToggleButton, p: Problem) = View.OnClickListener {
        Log.d("클릭", "bookmarkClicker: $p")
        Log.d("클릭", "bookmarkClicker: ${b.isChecked}")
        viewModel.postProblemLike(
            problemId = p.id,
            userId = 1,
//                userId = ApplicationClass.preferencesHelper.prefUserId,
            problemLike = b.isChecked,
        )
    }

    private fun setStrongBookMarkClicker(list: List<Problem>) {
        with(binding) {
            for (i in 0 until minOf(list.size, 3)) {
                when (i) {
                    0 -> recommendStrongBookmarkButton1.setOnClickListener(bookmarkClicker(recommendStrong1, recommendStrongBookmarkButton1, list[0]))
                    1 -> recommendStrongBookmarkButton2.setOnClickListener(bookmarkClicker(recommendStrong2, recommendStrongBookmarkButton2, list[1]))
                    2 -> recommendStrongBookmarkButton3.setOnClickListener(bookmarkClicker(recommendStrong3, recommendStrongBookmarkButton3, list[2]))
                    else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                }
            }
        }
    }

    private fun setWeakBookMarkClicker(list: List<Problem>) {
        with(binding) {
            for (i in 0 until minOf(list.size, 3)) {
                when (i) {
                    0 -> recommendWeakBookmarkButton1.setOnClickListener(bookmarkClicker(recommendWeak1, recommendWeakBookmarkButton1, list[0]))
                    1 -> recommendWeakBookmarkButton2.setOnClickListener(bookmarkClicker(recommendWeak2, recommendWeakBookmarkButton2, list[1]))
                    2 -> recommendWeakBookmarkButton3.setOnClickListener(bookmarkClicker(recommendWeak3, recommendWeakBookmarkButton3, list[2]))
                    else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                }
            }
        }
    }

    private fun setSimilarBookMarkClicker(list: List<Problem>) {
        with(binding) {
            for (i in 0 until minOf(list.size, 3)) {
                when (i) {
                    0 -> recommendLikeBookmarkButton1.setOnClickListener(bookmarkClicker(recommendLike1, recommendLikeBookmarkButton1, list[0]))
                    1 -> recommendLikeBookmarkButton2.setOnClickListener(bookmarkClicker(recommendLike2, recommendLikeBookmarkButton2, list[1]))
                    2 -> recommendLikeBookmarkButton3.setOnClickListener(bookmarkClicker(recommendLike3, recommendLikeBookmarkButton3, list[2]))
                    else -> throw IndexOutOfBoundsException("없는 인덱스: $i")
                }
            }
        }
    }
}
