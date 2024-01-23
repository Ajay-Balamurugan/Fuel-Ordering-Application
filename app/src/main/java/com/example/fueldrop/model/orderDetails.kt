package com.example.fueldrop.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class orderDetails(): Parcelable {
    var userUid: String? = null
    var userName: String? = null
    var fuelNames: MutableList<String>? = null
    var fuelImages: MutableList<String>? = null
    var fuelPrices: MutableList<String>? = null
    var fuelQuantities: MutableList<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var itemPushKey: String? = null
    var currentTime: Long = 0

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userId: String,
        name: String,
        fuelItemName: ArrayList<String>,
        fuelItemPrice: ArrayList<String>,
        fuelItemQuantities: ArrayList<Int>,
        address: String,
        phone: String,
        time: Long,
        itempushkey: String?,
        totalPrice: String?
    ) : this(){
        this.userUid = userId
        this.userName = name
        this.fuelNames = fuelItemName
        this.fuelQuantities = fuelItemQuantities
        this.fuelPrices = fuelItemPrice
        this.address = address
        this.totalPrice = totalPrice
        this.phoneNumber = phone
        this.currentTime = time
        this.itemPushKey = itempushkey
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<orderDetails> {
        override fun createFromParcel(parcel: Parcel): orderDetails {
            return orderDetails(parcel)
        }

        override fun newArray(size: Int): Array<orderDetails?> {
            return arrayOfNulls(size)
        }
    }
}