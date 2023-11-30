package com.blueduck.dajumgum.util

object FirebaseDocumentProperties {
    object User {
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val DOB = "dob"
        const val TITLE = "title"
        const val CUSTOMERS = "customers"
    }
    object Tag {
        const val ID = "id"
        const val NAME = "name"
        const val TIMESTAMP = "timestamp"
    }
    object Customer {
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PHONE = "mobile"
        const val ADDRESS = "address"
        const val FLOOR_PLAN_IMAGE_URL = "floorPlanImageUrl"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val DATE_OF_INSPECTION = "dateOfInspection"
    }
    object InspectionDefect {
        const val ID = "id"
        const val DEFECT_NUMBER = "defectNumber"
        const val CUSTOMER_ID = "customerId"
        const val DEFECT_TYPE = "defectType"
        const val FAR_IMAGE_URL = "farImageUrl"
        const val ZOOMED_IMAGE_URL = "zoomedImageUrl"
        const val CATEGORY = "category"
        const val POSITION = "position"
        const val INSPECTION = "inspection"
        const val STATUS = "status"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
    object TemperatureDefect {
        const val ID = "id"
        const val DEFECT_NUMBER = "defectNumber"
        const val CUSTOMER_ID = "customerId"
        const val DEFECT_TYPE = "defectType"
        const val FAR_IMAGE_URL = "farImageUrl"
        const val ZOOMED_IMAGE_URL = "zoomedImageUrl"
        const val CATEGORY = "category"
        const val POSITION = "position"
        const val TEMPERATURE = "temperature"
        const val STATUS = "status"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
    object ACDefect {
        const val ID = "id"
        const val CUSTOMER_ID = "customerId"
        const val DEFECT_TYPE = "defectType"
        const val CATEGORY = "category"
        const val HCHO = "hcho"
        const val TVOC = "tvoc"
        const val RADON = "radon"
        const val STATUS = "status"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
    object MyTag {
        const val ID = "id"
        const val NAME = "name"
        const val TIMESTAMP = "timestamp"
    }
}