package com.example.storyapp.filters;

import android.widget.Filter;

import com.example.storyapp.adapters.AdapterCategory;
import com.example.storyapp.models.ModelCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelCategory> filerList;
    //adapter in which filter need to be implemented
    AdapterCategory adapterCategory;

    //constructor
    public FilterCategory(ArrayList<ModelCategory> filerList, AdapterCategory adapterCategory) {
        this.filerList = filerList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //value should not be null and empty
        if(constraint!=null && constraint.length()>0){

            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels=new ArrayList<>();

            for(int i=0;i<filerList.size();i++){
                //validate
                if(filerList.get(i).getCategory().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filerList.get(i));
                }
            }

            results.count=filteredModels.size();
            results.values=filteredModels;

        }
        else{
            results.count=filerList.size();
            results.values=filerList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter changes
        adapterCategory.categoryArrayList=(ArrayList<ModelCategory>) results.values;

        //notify changes
        adapterCategory.notifyDataSetChanged();

    }
}
