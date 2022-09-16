package com.example.myMovieApp.feature_movieApp.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.myMovieApp.R
import com.example.myMovieApp.adapter.LoadStateAdapter
import com.example.myMovieApp.adapter.PagingAdapter
import com.example.myMovieApp.common.StringUtils
import com.example.myMovieApp.databinding.FragmentItemBinding
import com.example.myMovieApp.feature_movieApp.data.RecyclerDataSource
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_item), PagingAdapter.OnItemClickListener {
    private val viewModel by viewModels<MovieAppViewModel>()
    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = view.let { FragmentItemBinding.bind(it) }

        setHasOptionsMenu(true)
    }
    private fun setUpRecycler(){
        val adapter = PagingAdapter(this)
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter
                .withLoadStateHeaderAndFooter(
                    header = LoadStateAdapter { adapter.retry() },
                    footer = LoadStateAdapter { adapter.retry() }
                )
            buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }
        viewModel.resultData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewModel.trailerResult.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                //empty view
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    textViewError.isVisible = true
                } else {
                    textViewError.isVisible = false
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.toolbar_menu, menu)

        val searchIcon = menu.findItem(R.id.search_clicked)
        val searchView = searchIcon.actionView as SearchView
        val favouriteIcon = menu.findItem(R.id.favourite_clicked)
        val favouriteSearchView = favouriteIcon.isVisible




        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                setUpRecycler()
                p0?.let {
                    binding.recyclerView.scrollToPosition(0)//no animation
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (StringUtils.isEmptyOrNull(p0)) {
                    viewModel.searchMovies(p0!!)
                    setUpRecycler()

                }
                return true
            }

        })
    }

    override fun itemClicked(movie: MovieResultModel) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(movie)

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}