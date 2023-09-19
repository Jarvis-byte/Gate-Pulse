package com.example.fmoapplication.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TempViewModel extends ViewModel {

    private MutableLiveData<Double> temperatureLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> visibilityLiveData = new MutableLiveData<>();

    private MutableLiveData<String> weatherConditionLiveData = new MutableLiveData<>();

    public LiveData<Double> getTemperatureLiveData() {
        return temperatureLiveData;
    }
    public LiveData<String> getWeatherConditionLiveData() {
        return weatherConditionLiveData;
    }

    public void setWeatherCondition(String weatherCondition) {
        weatherConditionLiveData.setValue(weatherCondition);
    }
    public MutableLiveData<Integer> getVisibilityLiveData() {
        return visibilityLiveData;
    }

    public void setTemperature(double temperature) {
        temperatureLiveData.setValue(temperature);
    }

    public void setVisibility(int visibility) {
        visibilityLiveData.setValue(visibility);
    }

}
