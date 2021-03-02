package com.example.client_leger.tutorial;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.client_leger.databinding.TutorialPageBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TutorialPageAdapter extends RecyclerView.Adapter<TutorialPageAdapter.TutorialPageViewHolder> {
    private List<TutorialPage> tutorialPages;

    public TutorialPageAdapter()
    {
        tutorialPages = new ArrayList<>();
    }

    public TutorialPageAdapter(List<TutorialPage> tutorialPages)
    {
        this.tutorialPages = tutorialPages;
    }

    public List<TutorialPage> getTutorialPages()
    {
        return tutorialPages;
    }

    public void setTutorialPages(List<TutorialPage> tutorialPages)
    {
        this.tutorialPages = tutorialPages;
    }

    @NonNull
    @Override
    public TutorialPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new TutorialPageAdapter.TutorialPageViewHolder(TutorialPageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialPageViewHolder holder, int position)
    {
        holder.bind(tutorialPages.get(position));
    }

    @Override
    public int getItemCount()
    {
        return tutorialPages != null ? tutorialPages.size() : 0;
    }

    public class TutorialPageViewHolder extends RecyclerView.ViewHolder
    {
        private TutorialPageBinding binding;

        public TutorialPageViewHolder(TutorialPageBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TutorialPage tutorialPage)
        {
            binding.tutorialPageImage.setImageResource(tutorialPage.getImage());
            binding.tutorialPageDescription.setText(tutorialPage.getDescription());
        }
    }
}
