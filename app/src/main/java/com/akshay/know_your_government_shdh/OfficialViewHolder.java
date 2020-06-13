package com.akshay.know_your_government_shdh;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder
{
    TextView officeName, name, party;
    public OfficialViewHolder(@NonNull View itemView)
    {
        super(itemView);
        officeName = itemView.findViewById(R.id.officeName);
        name = itemView.findViewById(R.id.name);
        party = itemView.findViewById(R.id.party);
    }
}