package com.blueduck.dajumgum

import com.blueduck.dajumgum.model.Tag
import com.blueduck.dajumgum.enums.DefectType
import com.blueduck.dajumgum.model.ACDefect
import com.blueduck.dajumgum.model.Customer
import com.blueduck.dajumgum.model.InspectionDefect
import com.blueduck.dajumgum.model.TemperatureDefect
import com.blueduck.dajumgum.util.FirebaseCollections
import com.blueduck.dajumgum.util.FirebaseDocumentProperties
import com.blueduck.dajumgum.util.convertJsonToStringArray
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * FirebaseService class responsible for interacting with Firebase Firestore.
 * Provides functions to save a tag and fetch tags after a specified timestamp.
 * @param firestore An instance of FirebaseFirestore used for Firestore operations.
 */

class FirebaseService @Inject constructor( private val firestore: FirebaseFirestore) {




    suspend fun getAllCustomers(userId: String): List<Customer> {
        val customers = mutableListOf<Customer>()

        try {
            val userCollectionRef = firestore.collection(FirebaseCollections.USERS)
            val userDocumentRef = userCollectionRef.document(userId)
            val customersCollectionRef = userDocumentRef.collection(FirebaseCollections.CUSTOMERS)
            val snapshot = customersCollectionRef.get().await()

            for (document in snapshot.documents) {
                val id = document.getString(FirebaseDocumentProperties.Customer.ID)!!
                val name = document.getString(FirebaseDocumentProperties.Customer.NAME)!!
                val email = document.getString(FirebaseDocumentProperties.Customer.EMAIL)!!
                val phone = document.getString(FirebaseDocumentProperties.Customer.PHONE) ?: ""
                val address = document.getString(FirebaseDocumentProperties.Customer.ADDRESS)!!
                val floorPlanImageUrl = document.getString(FirebaseDocumentProperties.Customer.FLOOR_PLAN_IMAGE_URL)!!
                val width = document.getDouble(FirebaseDocumentProperties.Customer.WIDTH)!!
                val height = document.getDouble(FirebaseDocumentProperties.Customer.HEIGHT)!!
                val dateOfInspection = document.getLong(FirebaseDocumentProperties.Customer.DATE_OF_INSPECTION)!!
                val customer = Customer(
                    id = id,
                    name = name,
                    email = email,
                    mobile = phone,
                    address = address,
                    floorPlanImageUrl = floorPlanImageUrl,
                    width = width,
                    height = height,
                    dateOfInspection = dateOfInspection
                )
                customers.add(customer)
            }
        } catch (e: Exception) {
            println("fdfds ${e.message}")
            // Handle any exceptions during data retrieval
            e.printStackTrace()
        }

        return customers
    }

    fun createNewCustomer(customer: Customer, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val customersCollectionRef = firestore.collection(FirebaseCollections.USERS).document(userId).collection(FirebaseCollections.CUSTOMERS)
        val customerDocumentRef = customersCollectionRef.document(customer.id).set(customer)
        customerDocumentRef.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun createInspectionDefect(defect: InspectionDefect, userId: String,  onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val defectDocumentRef = firestore.collection(FirebaseCollections.USERS).document(userId)
            .collection(FirebaseCollections.CUSTOMERS).document(defect.customerId)
            .collection(FirebaseCollections.INSPECTION).document(defect.id).set(defect)
        defectDocumentRef.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun createTemperatureDefect(temperatureDefect: TemperatureDefect, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val defectDocumentRef = firestore.collection(FirebaseCollections.USERS).document(userId)
            .collection(FirebaseCollections.CUSTOMERS).document(temperatureDefect.customerId)
            .collection(FirebaseCollections.TEMPERATURE).document(temperatureDefect.id).set(temperatureDefect)
        defectDocumentRef.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun createACDefect(defect: ACDefect, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val defectDocumentRef = firestore.collection(FirebaseCollections.USERS).document(userId)
            .collection(FirebaseCollections.CUSTOMERS).document(defect.customerId)
            .collection(FirebaseCollections.AC).document(defect.id).set(defect)
        defectDocumentRef.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }


    fun getInspectionDefects(userId: String, customerId: String, onSuccess: (ArrayList<InspectionDefect>) -> Unit, onFailure: (Exception) -> Unit) {
        val defectsCollectionRef = firestore.collection(FirebaseCollections.USERS).document(userId)
            .collection(FirebaseCollections.CUSTOMERS).document(customerId)
            .collection(FirebaseCollections.INSPECTION)
        val list = arrayListOf<InspectionDefect>()
        defectsCollectionRef.get().addOnSuccessListener {
            val documents = it.documents
            for (doc in documents){
                val id = doc.getString(FirebaseDocumentProperties.InspectionDefect.ID)!!
                val defectNumber = doc.get(FirebaseDocumentProperties.InspectionDefect.DEFECT_NUMBER)!!
                val cId = doc.getString(FirebaseDocumentProperties.InspectionDefect.CUSTOMER_ID)!!
                val defectType = doc.getString(FirebaseDocumentProperties.InspectionDefect.DEFECT_TYPE) ?: DefectType.INSPECTION.value
                val farImageUrl = doc.getString(FirebaseDocumentProperties.InspectionDefect.FAR_IMAGE_URL)!!
                val zoomedImageUrl = doc.getString(FirebaseDocumentProperties.InspectionDefect.ZOOMED_IMAGE_URL)!!
                val category = doc.getString(FirebaseDocumentProperties.InspectionDefect.CATEGORY)!!
                val position = doc.get(FirebaseDocumentProperties.InspectionDefect.POSITION).toString()
                val inspection = doc.get(FirebaseDocumentProperties.InspectionDefect.INSPECTION).toString()
                val status = doc.getString(FirebaseDocumentProperties.InspectionDefect.STATUS)!!
                val createdAt = doc.getLong(FirebaseDocumentProperties.InspectionDefect.CREATED_AT) ?: 0
                val updatedAt = doc.getLong(FirebaseDocumentProperties.InspectionDefect.UPDATED_AT) ?: 0
                val inspectionDefect = InspectionDefect(
                    id = id,
                    defectNumber = defectNumber.toString().toInt(),
                    customerId = cId,
                    defectType = defectType,
                    farImageUrl = farImageUrl,
                    zoomedImageUrl = zoomedImageUrl,
                    category = category,
                    position = convertJsonToStringArray(position),
                    inspection = convertJsonToStringArray(inspection),
                    status = status,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
                list.add(inspectionDefect)
                onSuccess(list)
            }

            }.addOnFailureListener {
                onFailure(it)
            }
    }

    fun getTemperatureDefects(userId: String, customerId: String, onSuccess: (ArrayList<TemperatureDefect>) -> Unit, onFailure: (Exception) -> Unit) {
        val defectsCollectionRef = firestore.collection(FirebaseCollections.USERS).document(userId)
            .collection(FirebaseCollections.CUSTOMERS).document(customerId)
            .collection(FirebaseCollections.TEMPERATURE)
        val list = arrayListOf<TemperatureDefect>()
        defectsCollectionRef.get().addOnSuccessListener {
            val documents = it.documents
            for (doc in documents){
                val id = doc.getString(FirebaseDocumentProperties.TemperatureDefect.ID)!!
                val defectNumber = doc.get(FirebaseDocumentProperties.TemperatureDefect.DEFECT_NUMBER)!!
                val cId = doc.getString(FirebaseDocumentProperties.TemperatureDefect.CUSTOMER_ID)!!
                val farImageUrl = doc.getString(FirebaseDocumentProperties.InspectionDefect.FAR_IMAGE_URL) ?: ""
                val zoomedImageUrl = doc.getString(FirebaseDocumentProperties.InspectionDefect.ZOOMED_IMAGE_URL) ?: ""
                val defectType = doc.getString(FirebaseDocumentProperties.TemperatureDefect.DEFECT_TYPE) ?: DefectType.TEMPERATURE.value
                val category = doc.getString(FirebaseDocumentProperties.TemperatureDefect.CATEGORY)!!
                val position = doc.get(FirebaseDocumentProperties.TemperatureDefect.POSITION).toString()
                val temperature = doc.get(FirebaseDocumentProperties.TemperatureDefect.TEMPERATURE).toString()
                val status = doc.getString(FirebaseDocumentProperties.TemperatureDefect.STATUS).toString()
                val createdAt = doc.getLong(FirebaseDocumentProperties.TemperatureDefect.CREATED_AT) ?: 0
                val updatedAt = doc.getLong(FirebaseDocumentProperties.TemperatureDefect.UPDATED_AT) ?: 0
                val defect = TemperatureDefect(
                    id = id,
                    defectNumber = defectNumber.toString().toInt(),
                    customerId = cId,
                    defectType = defectType,
                    farImageUrl = farImageUrl,
                    zoomedImageUrl = zoomedImageUrl,
                    category = category,
                    position = convertJsonToStringArray(position),
                    temperature = convertJsonToStringArray(temperature ),
                    status = status,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
                list.add(defect)
                onSuccess(list)
            }

        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun getACDefects(userId: String, customerId: String, onSuccess: (ArrayList<ACDefect>) -> Unit, onFailure: (Exception) -> Unit) {
        val defectsCollectionRef = firestore.collection(FirebaseCollections.USERS).document(userId)
            .collection(FirebaseCollections.CUSTOMERS).document(customerId)
            .collection(FirebaseCollections.AC)
        val list = arrayListOf<ACDefect>()
        defectsCollectionRef.get().addOnSuccessListener {
            val documents = it.documents
            for (doc in documents){
                val id = doc.getString(FirebaseDocumentProperties.ACDefect.ID)!!
                val cId = doc.getString(FirebaseDocumentProperties.ACDefect.CUSTOMER_ID)!!
                val defectType = doc.getString(FirebaseDocumentProperties.ACDefect.DEFECT_TYPE) ?: DefectType.AC.value
                val category = doc.getString(FirebaseDocumentProperties.ACDefect.CATEGORY)!!
                val hcho = doc.getString(FirebaseDocumentProperties.ACDefect.HCHO) ?: ""
                val tvoc = doc.getString(FirebaseDocumentProperties.ACDefect.TVOC)!!
                val radon = doc.getString(FirebaseDocumentProperties.ACDefect.RADON)!!
                val status = doc.getString(FirebaseDocumentProperties.ACDefect.STATUS).toString()
                val createdAt = doc.getLong(FirebaseDocumentProperties.ACDefect.CREATED_AT) ?: 0
                val updatedAt = doc.getLong(FirebaseDocumentProperties.ACDefect.UPDATED_AT) ?: 0
                val defect = ACDefect(
                    id = id,
                    customerId = cId,
                    defectType = defectType,
                    category = category,
                    hcho = hcho,
                    tvoc = tvoc,
                    radon = radon,
                    status = status,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
                list.add(defect)
                onSuccess(list)
            }

        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun getCategoryTags(onSuccess: (ArrayList<Tag>) -> Unit, onFailure: (Exception) -> Unit) {
         firestore.collection(FirebaseCollections.CATEGORY_TAG).get().addOnSuccessListener {
             val documents = it.documents
             val tags = ArrayList<Tag>()
             for (doc in documents){
                 val id = doc.getString(FirebaseDocumentProperties.MyTag.ID)!!
                 val name = doc.getString(FirebaseDocumentProperties.MyTag.NAME)!!
                 val timestamp = doc.getLong(FirebaseDocumentProperties.MyTag.TIMESTAMP)!!
                 val tag = Tag(
                    id = id,
                     name = name,
                     timestamp = timestamp
                 )
                 tags.add(tag)
             }
             onSuccess(tags)
         }.addOnFailureListener {
            onFailure(it)
         }
    }

    fun getTemperatureTags(onSuccess: (ArrayList<Tag>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseCollections.TEMPERATURE_TAG).get().addOnSuccessListener {
            val documents = it.documents
            val tags = ArrayList<Tag>()
            for (doc in documents){
                val id = doc.getString(FirebaseDocumentProperties.MyTag.ID)!!
                val name = doc.getString(FirebaseDocumentProperties.MyTag.NAME)!!
                val timestamp = doc.getLong(FirebaseDocumentProperties.MyTag.TIMESTAMP)!!
                val tag = Tag(
                    id = id,
                    name = name,
                    timestamp = timestamp
                )
                tags.add(tag)
            }
            onSuccess(tags)
        }.addOnFailureListener {
            onFailure(it)
        }

    }

    fun getInspectionTags(onSuccess: (ArrayList<Tag>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseCollections.INSPECTION_TAG).get().addOnSuccessListener {
            val documents = it.documents
            val tags = ArrayList<Tag>()
            for (doc in documents){
                val id = doc.getString(FirebaseDocumentProperties.MyTag.ID)!!
                val name = doc.getString(FirebaseDocumentProperties.MyTag.NAME)!!
                val timestamp = doc.getLong(FirebaseDocumentProperties.MyTag.TIMESTAMP)!!
                val tag = Tag(
                    id = id,
                    name = name,
                    timestamp = timestamp
                )
                tags.add(tag)
            }
            onSuccess(tags)
        }.addOnFailureListener {
            onFailure(it)
        }

    }

    fun getPositionTags(onSuccess: (ArrayList<Tag>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseCollections.POSITION_TAG).get().addOnSuccessListener {
            val documents = it.documents
            val tags = ArrayList<Tag>()
            for (doc in documents){
                val id = doc.getString(FirebaseDocumentProperties.MyTag.ID)!!
                val name = doc.getString(FirebaseDocumentProperties.MyTag.NAME)!!
                val timestamp = doc.getLong(FirebaseDocumentProperties.MyTag.TIMESTAMP)!!
                val tag = Tag(
                    id = id,
                    name = name,
                    timestamp = timestamp
                )
                tags.add(tag)
            }
            onSuccess(tags)
        }.addOnFailureListener {
            onFailure(it)
        }

    }

    fun saveCategoryTag(newTag: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val tag = Tag(UUID.randomUUID().toString(), newTag, (System.currentTimeMillis() / 1000))
        firestore.collection(FirebaseCollections.CATEGORY_TAG).add(tag).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun savePositionTag(newTag: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val tag = Tag(UUID.randomUUID().toString(), newTag, (System.currentTimeMillis() / 1000))
        firestore.collection(FirebaseCollections.POSITION_TAG).add(tag).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun saveTemperatureTag(newTag: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val tag = Tag(UUID.randomUUID().toString(), newTag, (System.currentTimeMillis() / 1000))
        firestore.collection(FirebaseCollections.TEMPERATURE_TAG).add(tag).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun saveInspectionTag(newTag: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val tag = Tag(UUID.randomUUID().toString(), newTag, (System.currentTimeMillis() / 1000))
        firestore.collection(FirebaseCollections.INSPECTION_TAG).add(tag).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun deleteCategoryTag(tag: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseCollections.CATEGORY_TAG).whereEqualTo(FirebaseDocumentProperties.MyTag.NAME, tag)
            .get().addOnSuccessListener {
                for (doc in it) {
                    doc.reference.delete()
                }
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }
    }

    fun deletePositionTag(tag: String,  onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseCollections.POSITION_TAG).whereEqualTo(FirebaseDocumentProperties.MyTag.NAME, tag)
            .get().addOnSuccessListener {
                for (doc in it) {
                    doc.reference.delete()
                }
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }
    }

    fun deleteInspectionTag(tag: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseCollections.INSPECTION_TAG).whereEqualTo(FirebaseDocumentProperties.MyTag.NAME, tag)
            .get().addOnSuccessListener {
                for (doc in it) {
                    doc.reference.delete()
                }
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }
    }

    fun deleteTemperatureTag(tag: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection(FirebaseCollections.TEMPERATURE_TAG).whereEqualTo(FirebaseDocumentProperties.MyTag.NAME, tag)
            .get().addOnSuccessListener {
                for (doc in it) {
                    doc.reference.delete()
                }
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }
    }


}
