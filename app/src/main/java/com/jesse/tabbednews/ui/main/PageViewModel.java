package com.jesse.tabbednews.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel
{

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>()
    {
        @Override
        public String apply(Integer input)
        {
            return "Hello world from section: " + input;
        }
    });

    public void setIndex(int index)
    {
        mIndex.setValue(index);
    }

    public String getNewsURL()
    {
        switch (mIndex.getValue())
        {
            case 1:
                return "https://newsapi.org/v2/top-headlines?country=nz&apiKey=7f32fd5b23e947abafa4b92c55b42898";
            case 2:
                return "https://newsapi.org/v2/top-headlines?country=us&apiKey=7f32fd5b23e947abafa4b92c55b42898";
            case 3:
                return "https://newsapi.org/v2/top-headlines?country=gb&apiKey=7f32fd5b23e947abafa4b92c55b42898";
            default:
                return "https://newsapi.org/v2/top-headlines?country=nz&apiKey=7f32fd5b23e947abafa4b92c55b42898";
        }
    }

    public LiveData<String> getText()
    {
        return mText;
    }
}