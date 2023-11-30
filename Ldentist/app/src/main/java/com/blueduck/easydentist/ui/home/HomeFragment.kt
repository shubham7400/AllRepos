package com.blueduck.easydentist.ui.home

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.blueduck.easydentist.R
import com.blueduck.easydentist.databinding.FragmentHomeBinding
import com.blueduck.easydentist.databinding.ItemPostBinding
import com.blueduck.easydentist.databinding.ItemPostImageBinding
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.model.Post
import com.blueduck.easydentist.model.PostImage
import com.blueduck.easydentist.preferences.getUser
import com.blueduck.easydentist.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zires.switchsegmentedcontrol.ZiresSwitchSegmentedControl
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private val viewModel: HomeViewModel by activityViewModels()

    private val posts = arrayListOf<Post>()
    lateinit var adapter: PostAdapter

    private lateinit var appUser: AppUser

    private lateinit var progressDialog: ProgressDialog
    lateinit var uploadProgressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        configureUi()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureUi() {
        appUser = requireContext().getUser()!!
        progressDialog = getProgressDialog(requireContext())
        uploadProgressDialog = getUploadImageProgressDialog(requireContext())
        binding.tvUserImage.text = appUser.name[0].toString()
        setClickListeners()
        val getAllPostResponse = viewModel.getAllPostResponse.value
        if (getAllPostResponse == null){
            viewModel.getAllPost(appUser.userId, appUser = appUser)
        }
        setPostAdapter()
        setViewModelObserver()
        binding.switchBeforeAfter.setChecked(!viewModel.beforeAfterTabState)
    }


    // It sets a ViewModel observer for the getAllPostResponse LiveData.
    private fun setViewModelObserver() {
        viewModel.getAllPostResponse.observe(viewLifecycleOwner){ allPostResponse ->
            when (allPostResponse){
                is Response.Success -> {
                    posts.clear()
                    allPostResponse.data?.let {
                       it.forEach { post -> posts.add(post) }
                    }
                    updatePosts()
                    progressDialog.dismiss()
                }
                is Response.Loading -> {
                    progressDialog.show()
                }
                is Response.Failure -> {
                    val error = allPostResponse.e.message ?: ""
                    showToast(error)
                    progressDialog.show()
                }
            }
        }
     }


    // The setPostAdapter() function is used to display the list of posts in a RecyclerView.
    private fun setPostAdapter() {
        adapter = PostAdapter(posts)
        binding.rvPost.adapter = adapter
        updatePosts()

        // on click on post send the user to ViewDoctorOrOtherPostFragment fragment to view post
        adapter.onPostClick = {
            val arg = Bundle()
            arg.putSerializable(POST_OBJECT, it)
            findNavController().navigate(R.id.action_homeFragment_to_viewDoctorOrOtherPostFragment, arg)
        }
    }


    // The updatePosts() function is used to filter the posts based on the state of the Before/After switch and then update the adapter with the filtered posts.
    private fun updatePosts() {
        val filteredPost = if (binding.switchBeforeAfter.getIsChecked()) {
            posts.filter { it.diagnosisReport.isEmpty() }
        } else {
            posts.filter { it.diagnosisReport.isNotEmpty() }
        }
        adapter.submitList(ArrayList(filteredPost))
        adapter.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners() {
        binding.ivCalender.setOnClickListener {
            showCalendar()
        }
        binding.switchBeforeAfter.setOnToggleSwitchChangeListener(object : ZiresSwitchSegmentedControl.OnSwitchChangeListener {
            override fun onToggleSwitchChangeListener(isChecked: Boolean) {
                viewModel.beforeAfterTabState = isChecked
                updatePosts()
            }
        })
         binding.fabAddPost.setOnClickListener {
             findNavController().navigate(R.id.action_homeFragment_to_viewDoctorOrOtherPostFragment)
         }
        binding.cvProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }


    // The code also includes a showCalendar() function which displays a DatePickerDialog and sets the date of the posts to display.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Show the date picker dialog
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val cal = Calendar.getInstance()
            cal.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
            val selectedDateInMillis = cal.timeInMillis
            val date = convertMillisToDateString(selectedDateInMillis)
            binding.tvDate.text = date
            viewModel.getAllPost(appUser.userId, date, appUser = appUser)
        }, year, month, day)
        datePickerDialog.show()
    }



}


// adaptor to show posts
class PostAdapter(var list: ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    var onPostClick: (Post) -> Unit = {}
    class ViewHolder( val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         return ViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val post = list[position]
        holder.binding.apply {
            this.tvUserName.text = post.creatorName
            this.tvUserImage.text = post.creatorName[0].toString()
            this.tvDate.text = convertMillisToDateTimeString(post.createdAt)
            val adapter = PostImageAdapter(post, post.postImages)
            this.rvPostImages.adapter = adapter
            adapter.onPostImageClick = {
                onPostClick(post)
            }
            this.llPost.setOnClickListener { onPostClick(post) }
        }
    }

    fun submitList(posts: ArrayList<Post>) {
        list = posts
    }

}



// adapter to show the list of images in the post
class PostImageAdapter(var post: Post?, var list: ArrayList<PostImage>, private val showDeleteIcon: Boolean = false) : RecyclerView.Adapter<PostImageAdapter.ViewHolder>() {


    var onImageDeleteClick: (PostImage) -> Unit = {}

    var onPostImageClick: (String) -> Unit = {}
    class ViewHolder(val binding: ItemPostImageBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = list[position]
        holder.binding.apply {
            if (image.originalImageUrl.contains(CONTENT)) {
                this.ivPostImage.setImageURI(Uri.parse(image.originalImageUrl))
            }else{
                Glide.with(holder.binding.ivPostImage.context)
                    .load(image.thumbnailImageUrl)
                    .apply(RequestOptions().override(500, 500))
                    .into(this.ivPostImage)
            }
            this.ivPostImage.setOnClickListener { onPostImageClick(image.originalImageUrl) }
            if (post != null){
                if (post!!.diagnosisReport.isNotEmpty()) {
                    this.ivDelete.visibility = View.GONE
                }
            }
            if (!showDeleteIcon) {
                this.ivDelete.visibility = View.GONE
            }
            this.ivDelete.setOnClickListener {
                onImageDeleteClick(image)
            }
        }
    }

    fun submitList(images: ArrayList<PostImage>) {
        list = images
    }

}