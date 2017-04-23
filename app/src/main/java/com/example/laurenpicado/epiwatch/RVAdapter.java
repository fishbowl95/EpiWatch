package com.example.laurenpicado.epiwatch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView personStress;
        TextView personMotion;
        TextView personEMG;




        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personStress = (TextView)itemView.findViewById(R.id.person_stress);
            personMotion = (TextView)itemView.findViewById(R.id.person_motion);
            personEMG = (TextView)itemView.findViewById(R.id.person_emg);

        }
    }

    List<Person> persons;

    RVAdapter(List<Person> persons){
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seizures, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personStress.setText(persons.get(i).stress);
        personViewHolder.personMotion.setText(persons.get(i).motion);
        personViewHolder.personEMG.setText(persons.get(i).emg);

    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}









