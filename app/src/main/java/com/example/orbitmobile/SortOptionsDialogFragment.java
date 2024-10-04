package com.example.orbitmobile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SortOptionsDialogFragment extends DialogFragment {

    public interface OnSortOptionSelectedListener {
        void onSortOptionSelected(String sortBy, boolean isAscending);
    }

    private OnSortOptionSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSortOptionSelectedListener) {
            listener = (OnSortOptionSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSortOptionSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Inflate the custom dialog layout
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_sort_options, null);
        builder.setView(view);

        RadioGroup sortGroup = view.findViewById(R.id.sort_options_group);
        RadioButton ratingOption = view.findViewById(R.id.sort_by_rating);
        RadioButton lowHighOption = view.findViewById(R.id.sort_low_high);
        RadioButton highLowOption = view.findViewById(R.id.sort_high_low);

        // Set the dialog buttons
        builder.setNegativeButton("Clear", (dialog, which) -> {
            // If the listener is not null, we clear the sorting option
            if (listener != null) {
                listener.onSortOptionSelected("", true); // Reset sorting option
            }
        });

        builder.setPositiveButton("Sort by", (dialog, which) -> {
            // Determine which radio button is selected and pass it back to the listener
            if (sortGroup.getCheckedRadioButtonId() == ratingOption.getId()) {
                listener.onSortOptionSelected("rating", false); // Sorting by rating
            } else if (sortGroup.getCheckedRadioButtonId() == lowHighOption.getId()) {
                listener.onSortOptionSelected("price", true); // Sort from lowest to highest price
            } else if (sortGroup.getCheckedRadioButtonId() == highLowOption.getId()) {
                listener.onSortOptionSelected("price", false); // Sort from highest to lowest price
            }
        });

        return builder.create();
    }
}
