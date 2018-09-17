package com.example.neoman.pokemon_go

import android.location.Location

class Pokemon{
    var name:String?=null
    var desc:String?=null
    var loc:Location?=null
    var img:Int?=null
    var power:Double?=null
    var caught:Boolean=false

    constructor(name:String,image:Int,desc:String,lat:Double,long:Double,power:Double){
        this.name=name
        this.desc=desc
        this.loc=Location(name)
        this.loc!!.latitude=lat
        this.loc!!.longitude=long
        this.img=image
        this.power=power
    }




}