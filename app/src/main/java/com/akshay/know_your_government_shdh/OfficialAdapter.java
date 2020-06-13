package com.akshay.know_your_government_shdh;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder>
{
    private static final String TAG = "OfficialAdapter";
    private List<Official> officialList;
    private MainActivity mainActivity;

    public OfficialAdapter(List<Official> officialList, MainActivity mainActivity)
    {
        this.officialList = officialList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_entry,parent,false);
        itemView.setOnClickListener(mainActivity);
        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position)
    {
        Official temp = officialList.get(position);
        holder.officeName.setText(temp.getTitle());
        holder.name.setText(temp.getName());
        holder.party.setText(temp.getParty());
    }

    @Override
    public int getItemCount()
    {
        return officialList.size();
    }
}
