package com.blueduck.easydentist.ui.home

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.FragmentViewDoctorOrOtherPostBinding
import com.blueduck.easydentist.enums.FirebaseCollection
import com.blueduck.easydentist.enums.FirebaseDocumentField
import com.blueduck.easydentist.enums.UserPosition
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.model.Post
import com.blueduck.easydentist.model.PostImage
import com.blueduck.easydentist.preferences.getUser
import com.blueduck.easydentist.util.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import dagger.hilt.android.AndroidEntryPoint
import me.echodev.resizer.Resizer
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ViewDoctorOrOtherPostFragment : Fragment() {
    lateinit var binding: FragmentViewDoctorOrOtherPostBinding

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var appUser: AppUser

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var adapter: PostImageAdapter

    private var imageFile: File? = null
    private var photoUri: Uri? = null

    private val imagesUri = arrayListOf<Uri>()
    private val newPostImages = arrayListOf<PostImage>()

    private lateinit var uploadProgressDialog: ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    private var post: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewDoctorOrOtherPostBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.imageUris.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.imageUris.clear()
        imagesUri.forEach { viewModel.imageUris.add(it) }
    }



    private fun configureUi() {
        uploadProgressDialog = getUploadImageProgressDialog(requireContext())
        progressDialog = getSimpleProgressDialog(requireContext())
        appUser = requireContext().getUser()!!
        post =  try {
            requireArguments().getSerializable("post_object") as Post
        }catch (e: Exception){
            null
        }

        // if post is null that means current user will create this post for himself
        if (post != null){
            binding.tvUserName.text = post!!.creatorName
            binding.tvUserImage.text = post!!.creatorName[0].toString()
            binding.tvDate.text = convertMillisToDateTimeString(post!!.createdAt)
        }else{
            binding.tvUserName.text = appUser.name
            binding.tvUserImage.text = appUser.name[0].toString()
            binding.tvDate.text = convertMillisToDateTimeString(System.currentTimeMillis())
        }
        if (appUser.position == UserPosition.OTHER.value) {
            binding.etDiagnosisReport.isEnabled = false
            if (post != null){
                if (post!!.diagnosisReport.isNotEmpty()) {
                    binding.ivAddImage.visibility = View.GONE
                }
            }
        } else {
            if (post != null){
                if (post!!.diagnosisReport.isNotEmpty()){
                    if (appUser.userId == post!!.userId) {
                        binding.ivAddImage.visibility = View.VISIBLE
                    } else {
                        binding.ivAddImage.visibility = View.GONE
                    }
                }
                if (appUser.userId == post!!.userId) {
                    binding.ivAddImage.visibility = View.VISIBLE
                } else {
                    binding.ivAddImage.visibility = View.GONE
                }
            }
        }
        setViewModelObserver()
        setOnClickListeners()
        if (post != null){
            binding.etDiagnosisReport.setText(post!!.diagnosisReport)
        }
        setPostImageAdapter()
        if (viewModel.imageUris.isNotEmpty()){
            imagesUri.clear()
            viewModel.imageUris.forEach { imagesUri.add(it) }
            imagesUri.forEach {
               val pi = adapter.list.firstOrNull{ postImage ->  postImage.originalImageUrl == it.toString()}
                if (pi == null){
                    adapter.list.add(PostImage(it.toString(), it.toString()))
                }
            }
            adapter.notifyDataSetChanged()
        }
    }


    // this method observe the createPostResponse property of the view model
    private fun setViewModelObserver() {
        // this observer to listen the response of the create new post api response
         viewModel.createPostResponse.observe(viewLifecycleOwner) { createPostResponse ->
            when (createPostResponse){
                is Response.Success -> {
                    createPostResponse.data?.let { newPost ->
                        when(val getAllPostResponse = viewModel.getAllPostResponse.value){
                            is Response.Success -> {
                                // if new post created successfully, then we add new post to the list
                                getAllPostResponse.data?.add(newPost)
                            }
                            else -> {}
                        }
                        post = newPost
                        savePost()
                        viewModel.createPostResponse .value = null
                    }
                    progressDialog.dismiss()
                }
                is Response.Loading -> {
                    progressDialog.show()
                }
                is Response.Failure -> {
                    val error = createPostResponse.e.message ?: ""
                    showToast(error)
                    progressDialog.show()
                    requireActivity().onBackPressed()
                }
            }
        }
    }




    // this function creates the new post
    private fun createNewPost() {
        val report = binding.etDiagnosisReport.text?.toString() ?: ""
        val post = Post(
            UUID.randomUUID().toString(),
            requireContext().getUser()!!.userId,
            requireContext().getUser()?.name ?: "",
            report,
            System.currentTimeMillis(),
            convertMillisToDateString(System.currentTimeMillis()),
            arrayListOf()
        )
        viewModel.createPost(post)
    }

    private fun setOnClickListeners() {
        binding.btnSave.setOnClickListener {

            // create new post if user chose to create new post otherwise save data in the existing post
            if (post == null){
                createNewPost()
            }else{
                savePost()
            }
        }
        binding.ivBackBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.ivAddImage.setOnClickListener {
            addPostImage()
        }

        // this listener change the border color of the text field when it get active and in active. and it save the report when it loses the focus
        binding.etDiagnosisReport.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (post != null){
                    saveReport(false)
                }
                binding.etDiagnosisReport.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_cyan_border_cyan_light_10, null))
            }else{
                binding.etDiagnosisReport.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_cyan_dark_border_cyan_light_10, null))
            }
        }
    }

    private fun savePost() {
        // if user added new images to post then save both images and report otherwise save only report
        if (imagesUri.isNotEmpty()) {
            uploadImageToFirebase(imagesUri) {
                post!!.postImages.removeAll { it.originalImageUrl.contains(CONTENT) }
                val postImages = post!!.postImages
                newPostImages.forEach { pi -> postImages.add(pi) }
                saveReportAndImages(postImages)
            }
        }else{
            saveReport()
        }
    }


    // this function saves the diagnosis report and images added to this post
    private fun saveReportAndImages(postImages: ArrayList<PostImage>) {
        val report = binding.etDiagnosisReport.text?.toString() ?: ""
        db.collection(FirebaseCollection.POSTS.value)
            .whereEqualTo(FirebaseDocumentField.POST_ID.value, post!!.postId).get()
            .addOnSuccessListener {
                val doc = it.firstOrNull()
                doc?.let {
                    val updates = hashMapOf<String, Any>(
                        FirebaseDocumentField.DIAGNOSIS_REPORT.value to report,
                        FirebaseDocumentField.POST_IMAGES.value to postImages,
                    )
                    doc.reference.update(updates)
                        .addOnSuccessListener {
                            requireActivity().onBackPressed()
                        }.addOnFailureListener {
                            logError(it.message.toString())
                        }
                }
            }.addOnFailureListener {
                logError(it.message.toString())
            }
    }


    // this function save the diagnosis report
    private fun saveReport(shouldGoBack: Boolean = true) {
        db.collection(FirebaseCollection.POSTS.value)
            .whereEqualTo(FirebaseDocumentField.POST_ID.value, post!!.postId).get()
            .addOnSuccessListener {
                val doc = it.firstOrNull()
                doc?.let {
                    val report = binding.etDiagnosisReport.text?.toString() ?: ""
                    post!!.diagnosisReport = report
                    doc.reference.update(FirebaseDocumentField.DIAGNOSIS_REPORT.value, report)
                        .addOnSuccessListener {
                            if (shouldGoBack){
                                requireActivity().onBackPressed()
                            }
                        }.addOnFailureListener {
                            logError(it.message.toString())
                        }
                }
            }.addOnFailureListener {
                logError(it.message.toString())
            }
    }


    // show the bottom sheet dialog to select the source of image
    private fun addPostImage() {
        val modalBottomSheet = AddImageBottomSheet()
        modalBottomSheet.onGalleryClick = {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                checkForPermissions(Manifest.permission.READ_MEDIA_IMAGES) { granted ->
                    if (granted) {
                        selectImageFromGallery()
                    }
                }
            } else {
                checkForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) { granted ->
                    if (granted) {
                        selectImageFromGallery()
                    }
                }
            }
        }
        modalBottomSheet.onCameraClick = {
            checkForPermissions(Manifest.permission.CAMERA) { granted ->
                if (granted) {
                    captureFromCamera()
                }
            }
        }
        modalBottomSheet.show(requireActivity().supportFragmentManager, "AddImageBottomSheet")
    }

    // take image from camera
    private fun captureFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", it)
                    takeImageResult.launch(photoUri)
                }
            }
        }
    }

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            photoUri?.let {
                imagesUri.add(it)
                adapter.list.add(PostImage(it.toString(), it.toString()))
                adapter.notifyDataSetChanged()
            }
        }
    }



    // select image from gallery
    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")
    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        try {
            uploadProgressDialog.setMessage(resources.getString(R.string.upload_image_message))
            uris.forEach { imagesUri.add(it) }
            uris.forEach {  adapter.list.add(PostImage(it.toString(),it.toString())) }
            adapter.notifyDataSetChanged()
        }catch (e: java.lang.Exception){
            uploadProgressDialog.dismiss()
        }
    }


    // This is a function that uploads images to Firebase storage. It takes an array of URIs of images to upload, the current index, and a completion function as input parameters.
    // It first creates metadata for the image files, creates a reference to Firebase storage, and uploads the image file and metadata to the storage location.
    // It also handles progress updates and any failures that occur during the upload process.
    // Once an image is successfully uploaded, it creates a thumbnail of the image using the Resizer library, uploads the thumbnail, and retrieves the URLs for the original image and thumbnail image.
    // These URLs are then used to create a `PostImage` object which is added to an array of `PostImage` objects.
    // The function then recursively calls itself to upload the next image in the array until all images are uploaded. Finally, once all images are uploaded, it calls the completion function.
    private fun uploadImageToFirebase(imageUris: ArrayList<Uri>,currentIndex: Int = 0, onComplete: () -> Unit) {

        // Create the file metadata
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        val uri = imageUris[currentIndex]
        // Create a storage reference from our app
        val storageRef = firebaseStorage.reference

        // Upload file and metadata to the path 'images/image.jpg'
        val uploadTaskOriginalImage = storageRef.child("images/${uri.lastPathSegment}").putFile(uri, metadata)
        uploadProgressDialog.show()
        uploadTaskOriginalImage.addOnProgressListener {
           // show progress here
        }.addOnPausedListener {
            Log.d("TAG", "Upload is paused")
        }.addOnFailureListener {
            // Handle unsuccessful uploads
            uri.path?.let { tempUri ->
                val tempFile =  File(tempUri)
                if (tempFile.exists()) {
                    tempFile.delete()
                }
            }
            uploadProgressDialog.dismiss()
        }.addOnSuccessListener {    snapshotOriginalImage ->
            snapshotOriginalImage.storage.downloadUrl.addOnSuccessListener { originalImageUrl ->
                val thumbnailFile = createFileFromUri(uri)
                thumbnailFile.let { file ->
                    val thumbnailImage: File = Resizer(requireContext())
                        .setTargetLength(1080)
                        .setQuality(80)
                        .setOutputFormat("JPEG")
                        .setOutputFilename("resized_image")
                        //.setOutputDirPath(storagePath)
                        .setSourceImage(file)
                        .resizedFile
                    val thumbnailImageUri = thumbnailImage.toUri()
                    val random = UUID.randomUUID().toString()
                    val thumbnailImageUploadTask = storageRef.child("images/$random${thumbnailImageUri.lastPathSegment}").putFile(thumbnailImageUri, metadata)
                    thumbnailImageUploadTask.addOnProgressListener {
                        // show progress here
                    }.addOnPausedListener {
                        Log.d("TAG", "Upload is paused")
                    }.addOnFailureListener {
                        // Handle unsuccessful uploads
                        if (file.exists()) {
                            file .delete()
                        }
                        if (thumbnailImage.exists()) {
                            thumbnailImage .delete()
                        }
                        uploadProgressDialog.dismiss()
                    }.addOnSuccessListener { task ->
                        task.storage.downloadUrl.addOnSuccessListener { thumbnailImageUrl ->
                            if (originalImageUrl != null && thumbnailImageUrl != null){
                                newPostImages.add(PostImage(originalImageUrl.toString(), thumbnailImageUrl.toString()))
                            }
                            if (currentIndex < imageUris.size - 1) {
                                uploadImageToFirebase(imageUris, currentIndex + 1, onComplete)
                            }
                            uploadProgressDialog.setMessage(resources.getString(R.string.upload_image_message)+"${currentIndex+1}/${imageUris.size}")
                            if (currentIndex == imageUris.size - 1){
                                uploadProgressDialog.dismiss()
                                onComplete()
                            }
                        }
                    }
                }
            }
        }
    }




    // It reads the contents of the file from the URI and creates a new file using the createImageFile() function. Then, it copies the contents of the input stream to the newly created file using an output stream. Finally, it closes the input and output streams and returns the newly created file.
    private fun createFileFromUri(uri: Uri): File {
        val inputStream: InputStream = requireContext().contentResolver.openInputStream(uri)!!
        val file = createImageFile()
        val out: OutputStream = FileOutputStream(imageFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        out.close()
        inputStream.close()
        return file
    }


    // The function create a new file and returns it
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).also {
            imageFile = it
        }
    }



    // this function set the adaptor to show the images related to this selected post
    private fun setPostImageAdapter() {
        val showDeleteIcon = post?.diagnosisReport?.isEmpty() ?: true
        adapter = PostImageAdapter(post, post?.postImages ?: arrayListOf(), showDeleteIcon)
        binding.rvPostImages.adapter = adapter
        adapter.onPostImageClick = {
            val arg = Bundle()
            arg.putString("image_url", it)
            arg.putString("creator_name", post?.creatorName ?: appUser.name)
            arg.putString("date", if (post == null) "" else convertMillisToDateTimeString(post!!.createdAt))
            findNavController().navigate(
                R.id.action_viewDoctorOrOtherPostFragment_to_viewImageFragment,
                arg
            )
        }
        adapter.onImageDeleteClick = { image ->
            val images = adapter.list
            images.remove(image)
            adapter.submitList(images)
            if (image.originalImageUrl.contains(CONTENT)){
                imagesUri.remove(Uri.parse(image.originalImageUrl))
                adapter.notifyDataSetChanged()
            }else{
                db.collection(FirebaseCollection.POSTS.value)
                    .whereEqualTo(FirebaseDocumentField.POST_ID.value, post!!.postId).get()
                    .addOnSuccessListener {
                        val doc = it.firstOrNull()
                        doc?.let {
                            doc.reference.update(FirebaseDocumentField.POST_IMAGES.value, images)
                                .addOnSuccessListener {
                                    adapter.notifyDataSetChanged()
                                }.addOnFailureListener {
                                    logError(it.message.toString())
                                }
                        }
                    }.addOnFailureListener {
                        logError(it.message.toString())
                    }

            }
         }
    }

}