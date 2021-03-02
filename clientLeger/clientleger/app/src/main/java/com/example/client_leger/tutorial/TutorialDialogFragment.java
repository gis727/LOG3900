package com.example.client_leger.tutorial;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.client_leger.R;
import com.example.client_leger.databinding.DialogFragmentTutorialBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

public class TutorialDialogFragment extends DialogFragment
{
    public static final int COMPLETE_OVERVIEW_SECTION = 0;
    public static final int PARTY_CREATION_SECTION = 1;
    public static final int FFA_SECTION = 2;
    public static final int SOLO_SECTION = 3;
    public static final int COOP_SECTION = 4;
    public static final int COMMANDS_SECTION = 5;

    public static final int[] tutorialSectionImagesResources =
            {
                    R.array.tutorial_overview_images,
                    R.array.tutorial_party_creation_images,
                    R.array.tutorial_ffa_images,
                    R.array.tutorial_solo_images,
                    R.array.tutorial_coop_images,
                    R.array.tutorial_commands_images
            };

    public static final int[] tutorialSectionDescriptionsResources =
            {
                    R.array.tutorial_overview_descriptions,
                    R.array.tutorial_party_creation_descriptions,
                    R.array.tutorial_ffa_descriptions,
                    R.array.tutorial_solo_descriptions,
                    R.array.tutorial_coop_descriptions,
                    R.array.tutorial_commands_descriptions
            };

    private DialogFragmentTutorialBinding binding;
    private TutorialPageAdapter tutorialPageAdapter;

    public TutorialDialogFragment()
    {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        binding = DialogFragmentTutorialBinding.inflate(inflater, null, false);

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(binding.getRoot()).create();

        tutorialPageAdapter = new TutorialPageAdapter();
        binding.tutorialViewPager.setAdapter(tutorialPageAdapter);

        binding.tutorialViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);
                setActiveTutorialPageIndicator(position);
            }
        });

        binding.tutorialCompleteOverview.setOnClickListener(view1 ->
        {
            setUpTutorialPageAdapter(COMPLETE_OVERVIEW_SECTION);
            setUpTutorialPageIndicators();
            setActiveTutorialPageIndicator(0);
            showTutorialPages();
        });

        binding.tutorialPartyCreation.setOnClickListener(view1 ->
        {
            setUpTutorialPageAdapter(PARTY_CREATION_SECTION);
            setUpTutorialPageIndicators();
            setActiveTutorialPageIndicator(0);
            showTutorialPages();
        });

        binding.tutorialFreeForAll.setOnClickListener(view1 ->
        {
            setUpTutorialPageAdapter(FFA_SECTION);
            setUpTutorialPageIndicators();
            setActiveTutorialPageIndicator(0);
            showTutorialPages();
        });

        binding.tutorialSolo.setOnClickListener(view1 ->
        {
            setUpTutorialPageAdapter(SOLO_SECTION);
            setUpTutorialPageIndicators();
            setActiveTutorialPageIndicator(0);
            showTutorialPages();
        });

        binding.tutorialCoop.setOnClickListener(view1 ->
        {
            setUpTutorialPageAdapter(COOP_SECTION);
            setUpTutorialPageIndicators();
            setActiveTutorialPageIndicator(0);
            showTutorialPages();
        });

        binding.tutorialCommands.setOnClickListener(view1 ->
        {
            setUpTutorialPageAdapter(COMMANDS_SECTION);
            setUpTutorialPageIndicators();
            setActiveTutorialPageIndicator(0);
            showTutorialPages();
        });

        binding.tutorialCloseButton.setOnClickListener(view13 -> showTutorialSections());

        binding.tutorialActionButton.setOnClickListener(view12 ->
        {
            if (binding.tutorialViewPager.getCurrentItem() + 1 < tutorialPageAdapter.getItemCount())
                binding.tutorialViewPager.setCurrentItem(binding.tutorialViewPager.getCurrentItem() + 1);
            else
                showTutorialSections();
        });

        return dialog;
    }

    private void setUpTutorialPageAdapter(int tutorialSection)
    {
        Resources res = getResources();

        TypedArray tutorialPageImages = res.obtainTypedArray(tutorialSectionImagesResources[tutorialSection]);
        String[] tutorialPageDescriptions = res.getStringArray(tutorialSectionDescriptionsResources[tutorialSection]);
        List<TutorialPage> tutorialPages = new ArrayList<>();

        int nTutorialPages = tutorialPageImages.length();

        for (int i = 0; i < nTutorialPages; i++)
        {
            tutorialPages.add(new TutorialPage(tutorialPageImages.getResourceId(i, 0), tutorialPageDescriptions[i]));
        }

        tutorialPageAdapter.setTutorialPages(tutorialPages);
        tutorialPageAdapter.notifyDataSetChanged();
    }

    private void setUpTutorialPageIndicators()
    {
        binding.tutorialPageIndicators.removeAllViews();

        ImageView[] indicators = new ImageView[tutorialPageAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0,8,0);
        for (int i = 0; i < indicators.length; i++)
        {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getContext(), R.drawable.tutorial_page_inactive_indicator
            ));
            indicators[i].setLayoutParams(layoutParams);
            binding.tutorialPageIndicators.addView(indicators[i]);
        }
    }

    private void setActiveTutorialPageIndicator(int index)
    {
        int childCount = binding.tutorialPageIndicators.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            ImageView imageView = (ImageView) binding.tutorialPageIndicators.getChildAt(i);
            if (i == index)
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(), R.drawable.tutorial_page_active_indicator)
                );
            else
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(), R.drawable.tutorial_page_inactive_indicator)
                );
        }

        if (index == tutorialPageAdapter.getItemCount() - 1)
            binding.tutorialActionButton.setText("Finish");
        else
            binding.tutorialActionButton.setText("Next");
    }

    private void showTutorialPages()
    {
        binding.tutorialSections.setVisibility(View.GONE);
        binding.tutorialPageContainer.setVisibility(View.VISIBLE);
    }

    private void showTutorialSections()
    {
        binding.tutorialViewPager.setCurrentItem(0, false);
        binding.tutorialSections.setVisibility(View.VISIBLE);
        binding.tutorialPageContainer.setVisibility(View.GONE);
    }
}
