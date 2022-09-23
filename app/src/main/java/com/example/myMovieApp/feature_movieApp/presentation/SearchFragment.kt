package com.example.myMovieApp.feature_movieApp.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myMovieApp.R
import com.example.myMovieApp.adapter.MovieSeriesAdapter
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.common.Constants.SEARCH_TIME_DELAY
import com.example.myMovieApp.common.Resource
import com.example.myMovieApp.databinding.FragmentItemBinding
import com.example.myMovieApp.feature_movieApp.data.api.repository.ItemType
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.android.synthetic.main.load_state_footer.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_item) {

    lateinit var viewModel: MovieAppViewModel
    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    lateinit var movieSeriesAdapter: MovieSeriesAdapter
    lateinit var searchIcon: MenuItem
    lateinit var searchView: SearchView
    private var hasDbItems: Boolean = false
    private var isFabClicked: Boolean = false
    private lateinit var lastSearch: String
    private var fabState: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()
        setHasOptionsMenu(true)

        movieSeriesAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(it)
            // update the model with the videos
            val type = if (it.name.isNullOrEmpty()) ItemType.MOVIE else ItemType.SERIES
            it.id?.let { it2 ->
                viewModel.getMovieSeriesGenre(type, it2)
            }
            findNavController().navigate(action)
        }
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
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchMovies(searchIcon.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
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

    /* here we handle the action for menu items in toolbar */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favourite_clicked -> {
                fabState = !item.isChecked
                item.isChecked = fabState
                isFabClicked = fabState
                item.icon =
                    if (fabState) ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_fav_clicked
                    ) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_fav)

                return true
            }
            R.id.search_clicked -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    /* here we handle the action for menu items in toolbar */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toolbar_menu, menu)
        searchIcon = menu.findItem(R.id.search_clicked)
        searchView = searchIcon.actionView as SearchView

        val favIcon = menu.findItem((R.id.favourite_clicked))
        if (viewModel.safeGetMoviesSeries()) {
            favIcon.isVisible = true
            hasDbItems = true
        } else {
            favIcon.isVisible = false
            hasDbItems = false
        }

        var job: Job? = null
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                text?.let {
                    binding.recyclerView.scrollToPosition(0)//no animation
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_TIME_DELAY)
                    text?.let {
                        if (text.isNotEmpty()) {
                            lastSearch = text
                            if (hasDbItems) {
                                if (isFabClicked) {
                                    viewModel.getFavMoviesSeries(text)
                                        .observe(viewLifecycleOwner, Observer { favMovies ->
                                            movieSeriesAdapter.differ.submitList(favMovies)
                                        })
                                } else {
                                    viewModel.searchMovies(text)
                                }

                            } else {
                                viewModel.searchMovies(text)
                            }

                        } else {
                            lastSearch = ""
                            if (viewModel.hasInternetConnection()) movieSeriesAdapter.differ.submitList(emptyList()) else viewModel.searchMovieSeries.postValue(Resource.Error("Network Failure"))
                        }
                    }
                }
                return true
            }
        })

        viewModel.searchMovieSeries.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { movSeriesResponse ->
                        movieSeriesAdapter.differ.submitList(movSeriesResponse.results.toList())
                        val totalPages =
                            movSeriesResponse.total_pages / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchMovieSeriesPage == totalPages
                        if (isLastPage) {
                            recycler_view.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        viewModel.searchMovieSeries.postValue(Resource.Error(message))
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
            button_retry.setOnClickListener {
                retryHandle()
            }
        })

    }

    private fun retryHandle() {
        if (lastSearch.isNotEmpty()) {
            if (hasDbItems) {
                if (fabState) {
                    hideErrorMessage()
                    viewModel.getFavMoviesSeries(lastSearch)
                } else {
                    hideErrorMessage()
                    viewModel.searchMovies(lastSearch)
                }
            } else {
                viewModel.searchMovieSeries.postValue(Resource.Error("Network Failure"))
            }
        } else {
            if (viewModel.hasInternetConnection()) {
                viewModel.searchMovieSeries.postValue(Resource.Error("Start typing again"))
                button_retry.visibility = View.INVISIBLE
            } else {
                hideProgressBar()
                viewModel.searchMovieSeries.postValue(Resource.Error("Network Failure"))
            }

        }
    }
}