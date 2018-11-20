package com.example.mayank.task.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mayank.task.FullImageActivity;
import com.example.mayank.task.R;
import com.example.mayank.task.models.Worldpopulation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WorldPopulationAdpter extends RecyclerView.Adapter<WorldPopulationAdpter.WorldPopulationViewHolder> {


    List<Worldpopulation> list = new ArrayList<>();
    Context context;

    public WorldPopulationAdpter(Context context,List<Worldpopulation> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WorldPopulationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view   = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wordpopulation_layout,viewGroup,false);
        return  new WorldPopulationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorldPopulationViewHolder worldPopulationViewHolder, final int i) {
        worldPopulationViewHolder.country.setText(list.get(i).getCountry());
        worldPopulationViewHolder.population.setText(list.get(i).getPopulation());
        worldPopulationViewHolder.rank.setText("Rank : "+list.get(i).getRank().toString());
        Picasso.get().load(list.get(i).getFlag()).into(worldPopulationViewHolder.countryFlag);

        worldPopulationViewHolder.countryFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, FullImageActivity.class);
                intent.putExtra("flag",list.get(i).getFlag());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class  WorldPopulationViewHolder extends RecyclerView.ViewHolder{

        ImageView countryFlag;
        TextView rank,population,country;


        public WorldPopulationViewHolder(View itemView) {
            super(itemView);

            countryFlag = itemView.findViewById(R.id.flag);
            rank = itemView.findViewById(R.id.rank);
            population = itemView.findViewById(R.id.population);
            country = itemView.findViewById(R.id.name);
        }
    }



}
