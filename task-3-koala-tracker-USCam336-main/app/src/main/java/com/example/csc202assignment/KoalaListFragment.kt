package com.example.csc202assignment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csc202assignment.databinding.FragmentKoalaListBinding
import kotlinx.coroutines.launch
import java.util.*

class KoalaListFragment: Fragment() {
    private var _binding: FragmentKoalaListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val koalaListViewModel: KoalaListViewModel by viewModels()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKoalaListBinding.inflate(inflater, container, false)
        binding.koalaRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                koalaListViewModel.koalas.collect { koalas ->
                    binding.koalaRecyclerView.adapter =
                        KoalaListAdapter(koalas){ koalaId ->
                            findNavController().navigate(
                                KoalaListFragmentDirections.showKoalaDetail(koalaId)
                            )
                        }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("DEPRECATION")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.fragment_koala_list, menu)

    }

        @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_koalaVisit -> {
                showNewKoalaVisit()
                true
            }
            R.id.help ->{
                var url = "https://wildlifewarriors.org.au/conservation-projects/koala-conservation”"
                val intent = Intent(this@KoalaListFragment.context, ShowWebView::class.java)


                 intent.putExtra("passedUrl", url)
                startActivity(intent)
                true

            }
            else -> super.onOptionsItemSelected(item)
        }

    }



    private fun showNewKoalaVisit() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newKoala = Koala(
                id = UUID.randomUUID(),
                title = "",
                place = "",
                date = Date(),
                lat = "",
                lon = ""

            )
            koalaListViewModel.addKoala(newKoala)
            findNavController().navigate(KoalaListFragmentDirections.showKoalaDetail(newKoala.id))
        }
    }


}



