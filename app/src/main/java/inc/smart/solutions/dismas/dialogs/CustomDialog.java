package inc.smart.solutions.dismas.dialogs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import inc.smart.solutions.dismas.R;

public class CustomDialog extends DialogFragment {
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 99;
    public static final String CUSTOM_DIALOG_TITLE = "custom_dialog_title";
    public static final String CUSTOM_DIALOG_MESSAGE = "custom_dialog_message";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =   new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialog_view = inflater.inflate(R.layout.dialog_custom, null);
        builder.setView(dialog_view);

        TextView tvTitle = dialog_view.findViewById(R.id.tvTitle);
        TextView tvMessage = dialog_view.findViewById(R.id.tvMessage);
        if (getArguments() != null) {
            tvTitle.setText(getArguments().getString(CUSTOM_DIALOG_TITLE));
            tvMessage.setText(getArguments().getString(CUSTOM_DIALOG_MESSAGE));
        }

        Button btnCancel = dialog_view.findViewById(R.id.btnCancel);
        Button btnOk = dialog_view.findViewById(R.id.btnOk);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.this.getDialog().cancel();
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),  new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
                CustomDialog.this.getDialog().dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        CustomDialog.this.getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}

