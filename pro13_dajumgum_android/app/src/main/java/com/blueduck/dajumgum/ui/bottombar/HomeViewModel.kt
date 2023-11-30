package com.blueduck.dajumgum.ui.bottombar

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blueduck.dajumgum.FirebaseService
import com.blueduck.dajumgum.model.ACDefect
import com.blueduck.dajumgum.model.Customer
import com.blueduck.dajumgum.model.InspectionDefect
import com.blueduck.dajumgum.model.InspectionError
import com.blueduck.dajumgum.model.TemperatureDefect
import com.blueduck.dajumgum.model.User
import com.blueduck.dajumgum.preferences.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseService: FirebaseService,
) : ViewModel() {


    var inspectionDefectToEdit: InspectionDefect? = null
    var acDefectToEdit: ACDefect? = null
    var temperatureDefectToEdit: TemperatureDefect? = null

    //lateinit var defectTypeToGenerateReport: DefectType
    var category: String? = null
    lateinit var currentCustomer: Customer

    private val _user = MutableStateFlow<User?>(null)
    val user: Flow<User?> = _user

    var fileUri: Uri? = null

    private val _tags = mutableStateListOf<String>()
    val tags: List<String> get() = _tags

    private val _customers = mutableStateListOf<Customer>()
    val customers: List<Customer> get() = _customers

    private val _inspectionDefects = mutableStateListOf<InspectionDefect>()
    val inspectionDefects: List<InspectionDefect> get() = _inspectionDefects

    private val _temperatureDefects = mutableStateListOf<TemperatureDefect>()
    val temperatureDefects: List<TemperatureDefect> get() = _temperatureDefects

    private val _acDefects = mutableStateListOf<ACDefect>()
    val acDefects: List<ACDefect> get() = _acDefects

    private val _categoryTags = mutableStateListOf<String>()
    val categoryTags: List<String> get() = _categoryTags

    private val _positionTags = mutableStateListOf<String>()
    val positionTags: List<String> get() = _positionTags

    private val _inspectionTags = mutableStateListOf<String>()
    val inspectionTags: List<String> get() = _inspectionTags

    private val _statusTags = mutableStateListOf<String>()
    val statusTags: List<String> get() = _statusTags

    private val _temperatureTags = mutableStateListOf<String>()
    val temperatureTags: List<String> get() = _temperatureTags



    fun getUser(context: Context) {
        viewModelScope.launch {
            DataStoreManager.getUser(context).collect {
                _user.value = it
            }
        }
    }


    fun saveCategoryTag(context: Context, newTag: String, onSuccess: () -> Unit) {
        firebaseService.saveCategoryTag(newTag, onSuccess = {
            viewModelScope.launch {
                DataStoreManager.saveCategoryTag(context, newTag)
                onSuccess()
            }
        }, onFailure = {

        })
    }
    fun savePositionTag(context: Context, newTag: String, onSuccess: () -> Unit) {
        firebaseService.savePositionTag(newTag, onSuccess = {
            viewModelScope.launch {
                DataStoreManager.savePositionTag(context, newTag)
                onSuccess()
            }
        }, onFailure = {

        })
    }
    fun saveTemperatureTag(context: Context, newTag: String, onSuccess: () -> Unit) {
        firebaseService.saveTemperatureTag(newTag, onSuccess = {
            viewModelScope.launch {
                DataStoreManager.saveTemperatureTag(context, newTag)
                onSuccess()
            }
        }, onFailure = {

        })
    }
    fun saveInspectionTag(context: Context, newTag: String, onSuccess: () -> Unit) {
        firebaseService.saveInspectionTag(newTag, onSuccess = {
            viewModelScope.launch {
                DataStoreManager.saveInspectionTag(context, newTag)
                onSuccess()
            }
        }, onFailure = {

        })
    }

    fun getCategoryTags(context: Context) {
        viewModelScope.launch {
            DataStoreManager.getCategoryTags(context).collect {
                _categoryTags.clear()
                if (it.isEmpty()){
                    // call the api to retrive the category tags
                    firebaseService.getCategoryTags(onSuccess = { tags ->
                        tags.forEach { tag ->
                            viewModelScope.launch { DataStoreManager.saveCategoryTag(context,tag.name) }
                        }
                    }, onFailure = {

                    })
                }else{
                    _categoryTags.addAll(it)
                }
            }
        }
    }
    fun getPositionTags(context: Context) {
        viewModelScope.launch {
            DataStoreManager.getPositionTags(context).collect {
                _positionTags.clear()
                if (it.isEmpty()){
                    // call the api to retrive the position tags
                    firebaseService.getPositionTags(onSuccess = { tags ->
                        tags.forEach { tag ->
                            viewModelScope.launch { DataStoreManager.savePositionTag(context,tag.name) }
                        }
                    }, onFailure = {

                    })
                }else{
                    _positionTags.addAll(it)
                }
            }
        }
    }
    fun getInspectionTags(context: Context) {
        viewModelScope.launch {
            DataStoreManager.getInspectionTags(context).collect {
                _inspectionTags.clear()
                if (it.isEmpty()){
                    // call the api to retrive the inspection tags
                    firebaseService.getInspectionTags(onSuccess = { tags ->
                        tags.forEach { tag ->
                            viewModelScope.launch { DataStoreManager.saveInspectionTag(context,tag.name) }
                        }
                    }, onFailure = {

                    })
                }else{
                    _inspectionTags.addAll(it)
                }
            }
        }
    }
    fun getTemperatureTags(context: Context) {
        viewModelScope.launch {
            DataStoreManager.getTemperatureTags(context).collect {
                _temperatureTags.clear()
                if (it.isEmpty()){
                    // call the api to retrive the temperature tags
                    firebaseService.getTemperatureTags(onSuccess = { tags ->
                        tags.forEach { tag ->
                            viewModelScope.launch { DataStoreManager.saveTemperatureTag(context,tag.name) }
                        }
                    }, onFailure = {

                    })
                }else{
                    _temperatureTags.addAll(it)
                }
            }
        }
    }
    fun getStatusTags(context: Context) {
        viewModelScope.launch {
            DataStoreManager.getStatusTags(context).collect {
                _statusTags.clear()
                _statusTags.addAll(it)
            }
        }
    }


    fun getCategoryTagsFromRemote(context: Context) {
        // call the api to retrive the category tags
        firebaseService.getCategoryTags(onSuccess = { tags ->
            tags.forEach { tag ->
                viewModelScope.launch { DataStoreManager.saveCategoryTag(context,tag.name) }
            }
        }, onFailure = {

        })
    }

    fun getPositionTagsFromRemote(context: Context) {
        // call the api to retrive the position tags
        firebaseService.getPositionTags(onSuccess = { tags ->
            tags.forEach { tag ->
                viewModelScope.launch { DataStoreManager.savePositionTag(context,tag.name) }
            }
        }, onFailure = {

        })
    }

    fun getInspectionTagsFromRemote(context: Context) {
        // call the api to retrive the inspection tags
        firebaseService.getInspectionTags(onSuccess = { tags ->
            tags.forEach { tag ->
                viewModelScope.launch { DataStoreManager.saveInspectionTag(context,tag.name) }
            }
        }, onFailure = {

        })
    }

    fun getTemperatureTagsFromRemote(context: Context) {
        // call the api to retrive the temperature tags
        firebaseService.getTemperatureTags(onSuccess = { tags ->
            tags.forEach { tag ->
                viewModelScope.launch { DataStoreManager.saveTemperatureTag(context,tag.name) }
            }
        }, onFailure = {

        })
    }

    fun getAllCustomers(userId: String) {
        viewModelScope.launch {
            val customers = firebaseService.getAllCustomers(userId)
            _customers.clear()
            _customers.addAll(customers)
        }
    }

    fun createNewCustomer(
        customer: Customer,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.createNewCustomer(customer, userId, onSuccess = {
            _customers.add(customer)
            onSuccess()
        }, onFailure = {
            onFailure(it)
        })
    }

    fun getTemperatureDefects(userId: String, customerId: String,  onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseService.getTemperatureDefects(userId, customerId, onSuccess = {
            _temperatureDefects.clear()
            _temperatureDefects.addAll(it)
            onSuccess()
        },
            onFailure = {
                onFailure(it)
            })
    }

    fun getACDefects(userId: String, customerId: String,   onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseService.getACDefects(userId, customerId, onSuccess = {
            _acDefects.clear()
            _acDefects.addAll(it)
            onSuccess()
        },
            onFailure = {
                onFailure(it)
            })
    }

    fun getInspectionDefects(
        userId: String,
        customerId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.getInspectionDefects(userId, customerId, onSuccess = {
            _inspectionDefects.clear()
            _inspectionDefects.addAll(it)
            onSuccess()
        },
            onFailure = {
                onFailure(it)
            })
    }

    fun createInspectionDefect(
        defect: InspectionDefect,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firebaseService.createInspectionDefect(defect, userId,
            onSuccess = {
                _inspectionDefects.add(defect)
                onSuccess()
            },
            onFailure = {
                onFailure(it)
            })
    }


    fun createTemperatureDefect(temperatureDefect: TemperatureDefect, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseService.createTemperatureDefect(temperatureDefect, userId , onSuccess = {
            _temperatureDefects.add(temperatureDefect)
            onSuccess()
        },
            onFailure = {
                onFailure(it)
            })
    }

    fun createACDefect(defect: ACDefect, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseService.createACDefect(defect, userId , onSuccess = {
            _acDefects.add(defect)
            onSuccess()
        },
            onFailure = {
                onFailure(it)
            })
    }

    fun deleteCategoryTag(context: Context, tag: String, onSuccess: () -> Unit) {
        firebaseService.deleteCategoryTag(tag, onSuccess = {
            viewModelScope.launch {
                DataStoreManager.deleteCategoryTag(context, tag)
                onSuccess()
            }
        }, onFailure = {

        })
    }

    fun deletePositionTag(context: Context, tag: String, onSuccess: () -> Unit) {
        firebaseService.deletePositionTag(tag,onSuccess = {
            viewModelScope.launch {
                DataStoreManager.deletePositionTag(context, tag)
                onSuccess()
            }
        }, onFailure = {

        })
    }

    fun deleteInspectionTag(context: Context, tag: String, onSuccess: () -> Unit) {
        firebaseService.deleteInspectionTag(tag, onSuccess = {
            viewModelScope.launch {
                DataStoreManager.deleteInspectionTag(context, tag)
                onSuccess()
            }
        }, onFailure = {

        })
    }

    fun deleteTemperatureTag(context: Context, tag: String, onSuccess: () -> Unit) {
        firebaseService.deleteTemperatureTag(tag, onSuccess = {
            viewModelScope.launch {
                DataStoreManager.deleteTemperatureTag(context, tag)
                onSuccess()
            }
        }, onFailure = {

        })
    }

}