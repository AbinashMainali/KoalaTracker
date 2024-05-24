package com.example.csc202assignment




import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.location.Location
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.csc202assignment.databinding.FragmentKoalaDetailBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


private const val TAG = "KoalaDetailFragment"

class KoalaDetailFragment: Fragment()  {

    private lateinit var mLocationField: TextView
    private lateinit var mMapButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val koalaListViewModel: KoalaListViewModel by viewModels()

    private var _binding: FragmentKoalaDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: KoalaDetailFragmentArgs by navArgs()


    private val koalaDetailViewModel: KoalaDetailViewModel by viewModels {
        KoalaDetailViewModelFactory(args.koalaId)
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val koala = Koala(
            id = UUID.randomUUID(),
            title = "",
            place = "",
            date = Date(),

        )

        Log.d(TAG, "The Koala ID is: ${args.koalaId}")
    }*/


    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            koalaDetailViewModel.updateKoala { oldKoala ->
                oldKoala.copy(photoFileName = photoName)
            }
        }
    }
    private var photoName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKoalaDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            koalaTitle.doOnTextChanged { text, _, _, _ ->
                koalaDetailViewModel.updateKoala { oldKoala ->
                    oldKoala.copy(title = text.toString())
                }
            }
            placeText.doOnTextChanged { text, _, _, _ ->
                koalaDetailViewModel.updateKoala { oldKoala ->
                    oldKoala.copy(place = text.toString())
                }
            }



            koalaCamera.setOnClickListener {
                photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.csc202assignment.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }

          /*  val captureImageIntent = takePhoto.contract.createIntent(
                requireContext(), null

            )
            koalaCamera.isEnabled = canResolveIntent(captureImageIntent)*/
        }

        mLocationField = view.findViewById(R.id.gps_textView)
        mMapButton = view.findViewById(R.id.show_gps)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                koalaDetailViewModel.koala.collect { koala ->
                    koala?.let { updateUi(it)
                    }
                }
            }
        }



        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            koalaDetailViewModel.updateKoala { it.copy(date = newDate) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUi(koala: Koala) {
        binding.apply {
            if (koalaTitle.text.toString() != koala.title) {
                koalaTitle.setText(koala.title)
            }
            if (placeText.text.toString() != koala.place) {
                placeText.setText(koala.place)
            }

            koalaDate.text = koala.date.toString()
            koalaDate.setOnClickListener {
                findNavController().navigate(
                   KoalaDetailFragmentDirections.selectDate(koala.date)

                )
            }

            shareButton.setOnClickListener{
                val reportIntent = Intent(Intent.ACTION_SEND).apply{
                    type="text/plain"
                    putExtra(Intent.EXTRA_TEXT, getKoalaReport(koala))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.koala_report_subject))
                }
                val chooserIntent = Intent.createChooser(
                    reportIntent,
                    getString(R.string.send_report)
                )
                startActivity(chooserIntent)
            }


            deleteButton.setOnClickListener{
                deleteKoalaVisit(koala)
            }


            koalaPhoto.setOnClickListener {

                val zoomPhoto= ZoomedPhotoFragment.newInstance(koala.photoFileName)

                fragmentManager?.let { it1 -> zoomPhoto.show(it1,null) }

            }

            updatePhoto(koala.photoFileName)


            mMapButton.setOnClickListener {
                  /*  val mapIntent = Intent(Intent.ACTION_VIEW).apply{
                        putExtra(Intent.EXTRA_TEXT, mLocationField.text)
                        askPermission(koala)
                    }
                startActivity(mapIntent)*/
                askPermission(koala)
                val mapIntent = Intent(this@KoalaDetailFragment.context, MapsActivity::class.java)
                        mapIntent.putExtra("Receipt Location", mLocationField.text)
                startActivity(mapIntent)



            }

        }
    }


    private fun deleteKoalaVisit(koala: Koala) {
        viewLifecycleOwner.lifecycleScope.launch {

            val deletedKoala = Koala(
                id = koala.id,
                title = koala.title,
                place = koala.place,
                date = koala.date,
                lat = koala.lat,
                lon = koala.lon

            )
            koalaListViewModel.deleteKoala(deletedKoala)
            findNavController().navigate(KoalaDetailFragmentDirections.deleteKoalaDetail(deletedKoala.id))
        }
    }

    @Suppress("DEPRECATION")
    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.koalaPhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }

            if (photoFile?.exists() == true) {
                binding.koalaPhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.koalaPhoto.setImageBitmap(scaledBitmap)
                    binding.koalaPhoto.tag = photoFileName
                }
            } else {
                binding.koalaPhoto.setImageBitmap(null)
                binding.koalaPhoto.tag = null
            }
        }
    }

    private fun getKoalaReport(koala: Koala): String {
        val dateString = DateFormat.getLongDateFormat(context).format(koala.date).toString()

        return getString(
            R.string.koala_report, koala.title, dateString, koala.place)

    }



        private fun askPermission(koala: Koala) {
        //Asking gps permission and availability in play services

          if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            Log.d(TAG, "Try to get a location")
            if(GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(requireContext())== ConnectionResult.SUCCESS){
            }
        }
            if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())== ConnectionResult.SUCCESS) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            koala.lat = location.latitude.toString()
                            koala.lon = location.longitude.toString()
                            mLocationField.text = String.format(
                                "Lat: %s, Lon: %s",
                                koala.lat,
                                koala.lon
                            )
                            Log.i("LOCATION", "Got a fix:$location")

                        }
                    }
            }
            if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())== ConnectionResult.SUCCESS) {
                fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, object: CancellationToken(){
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                    override fun isCancellationRequested()= false
                }).addOnSuccessListener { location: Location?->
                    location?.let {
                        Log.i("LOCATION", "Got a fix: $location")
                    }
                }
            }
    }

}






