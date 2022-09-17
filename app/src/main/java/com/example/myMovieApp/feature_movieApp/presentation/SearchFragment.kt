package com.example.myMovieApp.feature_movieApp.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myMovieApp.R
import com.example.myMovieApp.adapter.MovieSeriesAdapter
import com.example.myMovieApp.adapter.PagingAdapter
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.common.Constants.SEARCH_TIME_DELAY
import com.example.myMovieApp.common.Resource
import com.example.myMovieApp.common.StringUtils
import com.example.myMovieApp.databinding.FragmentItemBinding
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.SearchMoviesDao
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.android.synthetic.main.load_state_footer.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_item){
    lateinit var viewModel: MovieAppViewModel
    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    lateinit var movieSeriesAdapter: MovieSeriesAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        _binding = view.let { FragmentItemBinding.bind(it) }
//        pagingAdapter = PagingAdapter(this)
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()

        movieSeriesAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(it)

            findNavController().navigate(action)
        }
        // TODO ELEGXOS GIA TO AN I DB EXEI ESTW ENA ELEMENT GIA NA EMFANIZEI TO KOUMPI
        var job: Job? = null
        etSearch.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()) {
                        viewModel.searchMovies(editable.toString())
                    }
                }
            }
        }
        viewModel.searchMovieSeries.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { movSeriesResponse ->
                        movieSeriesAdapter.differ.submitList(movSeriesResponse.results.toList())
                        val totalPages = movSeriesResponse.total_pages / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchMovieSeriesPage == totalPages
                        if(isLastPage) {
                            recycler_view.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
        button_retry.setOnClickListener {
            if (etSearch.text.toString().isNotEmpty()) {
                viewModel.searchMovies(etSearch.text.toString())
            } else {
                hideErrorMessage()
            }
        }

//        setHasOptionsMenu(true)
    }
    private fun hideProgressBar() {
        progress_bar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        load_state_footer.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {

        load_state_footer.visibility = View.VISIBLE
        textview_error.text = message
        isError = true
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.searchMovies(etSearch.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setUpRecyclerView() {
        movieSeriesAdapter = MovieSeriesAdapter()
        recycler_view.apply {
            adapter = movieSeriesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }







//    private fun setUpRecycler(){
//        binding.apply {
//            recyclerView.setHasFixedSize(true)
//            recyclerView.itemAnimator = null
//            recyclerView.adapter = pagingAdapter
//                .withLoadStateHeaderAndFooter(
//                    header = LoadStateAdapter { pagingAdapter.retry() },
//                    footer = LoadStateAdapter { pagingAdapter.retry() }
//                )
//            buttonRetry.setOnClickListener {
//                pagingAdapter.retry()
//            }
//        }
//        viewModel.resultData.observe(viewLifecycleOwner) {
//            pagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
//        }
//
//
//        pagingAdapter.addLoadStateListener { loadState ->
//            binding.apply {
//                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
//                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
//                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
//                textViewError.isVisible = loadState.source.refresh is LoadState.Error
//
//                //empty view
//                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && pagingAdapter.itemCount < 1) {
//                    recyclerView.isVisible = false
//                    textViewError.isVisible = true
//                } else {
//                    textViewError.isVisible = false
//                }
//            }
//        }
//    }
//
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//
//        inflater.inflate(R.menu.toolbar_menu, menu)
//
//        val searchIcon = menu.findItem(R.id.search_clicked)
//        val searchView = searchIcon.actionView as SearchView
//        val favouriteIcon = menu.findItem(R.id.favourite_clicked)
//        val favouriteSearchView = favouriteIcon.isVisible
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                setUpRecycler()
//                p0?.let {
//                    binding.recyclerView.scrollToPosition(0)//no animation
//                    searchView.clearFocus()
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                if (StringUtils.isEmptyOrNull(p0)) {
//                    viewModel.searchMovies(p0!!)
//                    setUpRecycler()
//
//                }
//                return true
//            }
//
//        })
//    }

//    override fun itemClicked(movie: MovieResultModel) {
//        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(movie)
//
//        findNavController().navigate(action)
//    }


}