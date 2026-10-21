package es.upm.miw.bantumi.ui.fragmentos;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.ui.actividades.MejoresResultadosActivity;

public class BorrarResultadosDialog extends DialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MejoresResultadosActivity activity = (MejoresResultadosActivity) requireActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle(R.string.txtDialogoBorrarTitulo)
                .setMessage(R.string.txtDialogoBorrarPregunta)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.borrarPuntuaciones();
                            }
                        })
                .setNegativeButton(
                        getString(android.R.string.cancel),
                        null);

        return builder.create();
    }
}
