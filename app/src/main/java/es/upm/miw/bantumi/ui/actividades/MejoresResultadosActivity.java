package es.upm.miw.bantumi.ui.actividades;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.datos.AppDatabase;
import es.upm.miw.bantumi.datos.Puntuacion;
import es.upm.miw.bantumi.ui.fragmentos.BorrarResultadosDialog;

public class MejoresResultadosActivity extends AppCompatActivity {

    private ListView lvMejoresResultados;
    private TextView tvSinResultados;
    private ArrayAdapter<Puntuacion> adapter;
    private List<Puntuacion> listaPuntuaciones;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mejores_resultados);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.txtOpcionMejoresResultados);
        }

        lvMejoresResultados = findViewById(R.id.lvMejoresResultados);
        tvSinResultados = findViewById(R.id.tvSinResultados);
        lvMejoresResultados.setEmptyView(tvSinResultados);

        db = AppDatabase.getInstance(this);
        listaPuntuaciones = db.puntuacionDao().getMejoresResultados();

        adapter = new PuntuacionAdapter(this, listaPuntuaciones);
        lvMejoresResultados.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mejores_resultados_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.opcBorrarTodo) {
            new BorrarResultadosDialog().show(getSupportFragmentManager(), "BORRAR_DIALOG");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void borrarPuntuaciones() {
        db.puntuacionDao().deleteAll();
        listaPuntuaciones.clear();
        adapter.notifyDataSetChanged();
        Snackbar.make(lvMejoresResultados, R.string.txtResultadosBorrados, Snackbar.LENGTH_LONG).show();
    }

    private static class PuntuacionAdapter extends ArrayAdapter<Puntuacion> {
        public PuntuacionAdapter(Context context, List<Puntuacion> puntuaciones) {
            super(context, R.layout.item_puntuacion, puntuaciones);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_puntuacion, parent, false);
            }

            Puntuacion item = getItem(position);
            if (item != null) {
                TextView tvPosicion = convertView.findViewById(R.id.tvPosicion);
                TextView tvNombreJugador = convertView.findViewById(R.id.tvNombreJugador);
                TextView tvFechaHora = convertView.findViewById(R.id.tvFechaHora);
                TextView tvPuntuacion = convertView.findViewById(R.id.tvPuntuacion);

                tvPosicion.setText(String.format("#%d", position + 1));
                tvNombreJugador.setText(item.getNombreJugador());
                tvFechaHora.setText(item.getFechaHora());
                tvPuntuacion.setText(String.format("%d - %d", item.getSemillasAlmacen1(), item.getSemillasAlmacen2()));
            }

            return convertView;
        }
    }
}
