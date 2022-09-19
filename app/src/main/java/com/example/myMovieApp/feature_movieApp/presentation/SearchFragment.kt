package com.example.myMovieApp.feature_movieApp.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()
        setHasOptionsMenu(true)

        movieSeriesAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(it)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favourite_clicked) {
            if (item.isChecked && !isFabClicked){
                isFabClicked = true
                item.setIcon(R.drawable.ic_fav_clicked)
            } else {
                isFabClicked = false
                item.setIcon(R.drawable.ic_fav)
            }

        }


        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toolbar_menu, menu)
        searchIcon = menu.findItem(R.id.search_clicked)
        searchView = searchIcon.actionView as SearchView


        val favIcon = menu.findItem((R.id.favourite_clicked))

        if (viewModel.safeGetMoviesSeries()){
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
                        if (text.toString().isNotEmpty()) {
                            // TODO: search from net or db
                            if (hasDbItems) {
                                if (isFabClicked){
//                                    viewModel.getFavMoviesSeries(text.toString())
//                                viewModel.getFavMoviesSeries()
//                                    .observe(viewLifecycleOwner, Observer { movieSeries ->
//                                        movieSeriesAdapter.differ.submitList(movieSeries)
//                                    })

                                } else {
                                    viewModel.searchMovies(text.toString())
                                }

                            } else {
                                viewModel.searchMovies(text.toString())
                            }
//                            viewModel.searchMovies(text.toString())
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
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
        button_retry.setOnClickListener {
            if (searchIcon.toString().isNotEmpty()) {
                if (hasDbItems) {
//                    viewModel.searchMovies(searchIcon.toString())
                    viewModel.getFavMoviesSeries(searchIcon.toString())
//                    viewModel.getFavMoviesSeries().observe(viewLifecycleOwner, Observer { movieSeries ->
//                            movieSeriesAdapter.differ.submitList(movieSeries)
//                        })
                } else {
                    viewModel.searchMovies(searchIcon.toString())
                }
            } else {
                hideErrorMessage()
            }
        }
    }
}