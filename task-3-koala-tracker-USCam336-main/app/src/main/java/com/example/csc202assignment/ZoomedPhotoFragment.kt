package com.example.csc202assignment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class ZoomedPhotoFragment: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.zoom_layout, container, false)
        val imageView = view.findViewById(R.id.zoom_image_view) as ImageView

        val photoFileName = arguments?.getSerializable("PHOTO_URI") as String

        imageView.setImageBitmap(BitmapFactory.decodeFile(requireContext().filesDir.path  +"/"+ photoFileName))

        return view
    }

    companion object {
        fun newInstance(photoFileName: String?): ZoomedPhotoFragment {
            val frag = ZoomedPhotoFragment()
            val args = Bundle()
            args.putSerializable("PHOTO_URI", photoFileName)
            frag.arguments = args
            return frag
        }
    }
}