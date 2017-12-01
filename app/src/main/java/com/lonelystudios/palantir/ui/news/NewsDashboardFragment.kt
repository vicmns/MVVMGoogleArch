package com.lonelystudios.palantir.ui.news

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lonelystudios.palantir.R
import com.lonelystudios.palantir.vo.sources.Source

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewsDashboardFragment.OnNewsDashboardFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NewsDashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsDashboardFragment : Fragment() {

    private var mListener: OnNewsDashboardFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_news_sources, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            //mListener!!.onSourceSelected(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnNewsDashboardFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnNewsDashboardFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnNewsDashboardFragmentInteractionListener {
        fun onSourceSelected(sources: Source)
    }

    companion object {
        val TAG = "NewsDashboard"
        fun newInstance(): NewsDashboardFragment {
            val fragment = NewsDashboardFragment()
            return fragment
        }
    }
}// Required empty public constructor
