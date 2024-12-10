package com.android.brewr.model.recommendation

import com.android.brewr.model.coffee.Coffee
import com.android.brewr.model.journey.Journey
import com.android.brewr.model.journey.JourneysRepository
import com.android.brewr.utils.KNNHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class recommendationViewModel(private val journeyRepository: JourneysRepository) {
    private val data_ = MutableStateFlow<List<Pair<List<Journey>, String>>>(emptyList())
    val data: StateFlow<List<Pair<List<Journey>, String>>> = data_.asStateFlow()

    private val recommendedJourneys_ = MutableStateFlow<List<Journey>>(emptyList())
    val recommendedJourneys: StateFlow<List<Journey>> = recommendedJourneys_.asStateFlow()

    private val recommendedCoffees_ = MutableStateFlow<List<Coffee>>(emptyList())
    val recommendedCoffees: StateFlow<List<Coffee>> = recommendedCoffees_.asStateFlow()


    private val recommendedCoffee_ = MutableStateFlow<Coffee?>(null)
    val recommendedCoffee: StateFlow<Coffee?> = recommendedCoffee_.asStateFlow()

    private val knnHelper:KNNHelper = TODO()}


//    fun updateKnnHelper(){
////          knnHelper.featuresPreProcessing() //在哪
//    }
//
//    fun getData(){
//        journeyRepository.getJourneysOfAllOtherUsers(onSuccess = { data_.value = it }, onFailure = {})
//    }
//
//    fun updateRecommendedJourneys(){
//        val uid = knnHelper.getKNNResult()
//        journeyRepository.getJourneysOfTheUser(uid,onSuccess = { recommendedJourneys_.value = it }, onFailure = {})
//    }
//    fun updateRecommendedCoffees(){
//        //get location of each, filter current location 20km
//    }
//    fun getRecommendedCoffee(){
//        //get the highest rated one
//    }
//    fun getNextRecommendedCoffee(){
//        //change the recommended to then next second of the list, if there is no more recommend, show"..."
//    }
//}
