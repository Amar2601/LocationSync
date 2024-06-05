package com.example.firstapplication

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id"                ) var id               : Int?    = null,
    @SerializedName("first_name"        ) var firstName        : String? = null,
    @SerializedName("last_name"         ) var lastName         : String? = null,
    @SerializedName("mobile"            ) var mobile           : String? = null,
    @SerializedName("email"             ) var email            : String? = null,
    @SerializedName("gender"            ) var gender           : String? = null,
    @SerializedName("image"             ) var image            : String? = null,
    @SerializedName("dob"               ) var dob              : String? = null,
    @SerializedName("current_address"   ) var currentAddress   : String? = null,
    @SerializedName("permanent_address" ) var permanentAddress : String? = null,
    @SerializedName("occupation"        ) var occupation       : String? = null,
    @SerializedName("status"            ) var status           : Int?    = null,
    @SerializedName("reset_request"     ) var resetRequest     : Int?    = null,
    @SerializedName("fcm_id"            ) var fcmId            : String? = null,
    @SerializedName("school_id"         ) var schoolId         : Int?    = null,
    @SerializedName("vehicle_id"        ) var vehicleId        : Int?    = null,
    @SerializedName("language"          ) var language         : String? = null,
    @SerializedName("email_verified_at" ) var emailVerifiedAt  : String? = null,
    @SerializedName("created_at"        ) var createdAt        : String? = null,
    @SerializedName("updated_at"        ) var updatedAt        : String? = null,
    @SerializedName("deleted_at"        ) var deletedAt        : String? = null,
    @SerializedName("full_name"         ) var fullName         : String? = null,
    @SerializedName("school_names"      ) var schoolNames      : String? = null,
    @SerializedName("role"              ) var role             : String? = null

)
