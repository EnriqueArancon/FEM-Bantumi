package es.upm.miw.bantumi.ui.actividades;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;

import es.upm.miw.bantumi.ui.fragmentos.FinalAlertDialog;
import es.upm.miw.bantumi.ui.fragmentos.RecoverGameDialog;
import es.upm.miw.bantumi.ui.fragmentos.RestartGameDialog;
import es.upm.miw.bantumi.ui.fragmentos.SaveGameDialog;
import es.upm.miw.bantumi.datos.AppDatabase;
import es.upm.miw.bantumi.datos.Puntuacion;
import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.dominio.logica.JuegoBantumi;
import es.upm.miw.bantumi.ui.viewmodel.BantumiViewModel;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    public JuegoBantumi juegoBantumi;
    private BantumiViewModel bantumiVM;
    int numInicialSemillas;
    public static final String NOMBRE_FICHERO = "partida_bantumi.txt";
    private boolean puntuacionGuardada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultSemillas = String.valueOf(getResources().getInteger(R.integer.intNumInicialSemillas));
        numInicialSemillas = Integer.parseInt(sharedPrefs.getString("numInicialSemillas", defaultSemillas));
        Log.i(LOG_TAG, "MainActivity.onCreate(): configurando juego con " + numInicialSemillas + " semillas");

        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        crearObservadores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarNombreJugador();
        actualizarAjustesJuego();
    }

    /**
     * Lee el nombre del jugador guardado en Preferencias y lo muestra en el tablero
     */
    private void actualizarNombreJugador() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nombre = sharedPrefs.getString("nombreJugador", getString(R.string.txtPlayer1));
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        if (tvJugador1 != null && nombre != null && !nombre.trim().isEmpty()) {
            tvJugador1.setText(nombre);
        }
    }

    /**
     * Comprueba si ha cambiado la configuración de semillas en Ajustes.
     * Si la partida actual no se ha empezado a jugar, actualiza el tablero automáticamente.
     */
    private void actualizarAjustesJuego() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultSemillas = String.valueOf(getResources().getInteger(R.integer.intNumInicialSemillas));
        int semillasConfiguradas = Integer.parseInt(sharedPrefs.getString("numInicialSemillas", defaultSemillas));
        if (semillasConfiguradas != numInicialSemillas && !juegoBantumi.partidaModificada()) {
            Log.i(LOG_TAG, "MainActivity.actualizarAjustesJuego(): Cambiando semillas de " + numInicialSemillas + " a " + semillasConfiguradas);
            numInicialSemillas = semillasConfiguradas;
            reiniciarPartida();
        }
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe( // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe( // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {

                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }

                });
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos   posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.opcAcercaDe) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.aboutTitle)
                    .setMessage(R.string.aboutMessage)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else if (itemId == R.id.opcReiniciarPartida) {
            new RestartGameDialog(getString(R.string.txtDialogoReiniciarTitulo)).show(getSupportFragmentManager(),
                    "RESTART_DIALOG");

        } else if (itemId == R.id.opcAjustes) {
            startActivity(new Intent(this, BantumiPrefs.class));

        } else if (itemId == R.id.opcGuardarPartida) {
            new SaveGameDialog(getString(R.string.txtDialogoGuardarTitulo)).show(getSupportFragmentManager(),
                    "SAVE_DIALOG");
        } else if (itemId == R.id.opcRecuperarPartida) {
            File archivo = new File(getFilesDir(), NOMBRE_FICHERO);
            if (!archivo.exists()) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.txtNoHayPartidaGuardada,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                // Leemos la partida guardada para comparar
                String partidaGuardada = leerLineaFichero();

                // Si el tablero actual es diferente a lo guardado Y se ha modificado la partida
                // inicial
                if (!juegoBantumi.serializa().equals(partidaGuardada) && juegoBantumi.partidaModificada()) {
                    new RecoverGameDialog(getString(R.string.txtDialogoRecuperarTitulo))
                            .show(getSupportFragmentManager(), "RECOVER_DIALOG");
                } else {
                    // No hay nada que perder: o es igual a lo guardado o el tablero estaba sin
                    // tocar
                    recuperarPartida();
                }
            }
        } else if (itemId == R.id.opcMejoresResultados) {
            startActivity(new Intent(this, MejoresResultadosActivity.class));
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.txtSinImplementar),
                    Snackbar.LENGTH_LONG).show();
        }
        return true;
    }

    private String leerLineaFichero() {
        try {
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
            String linea = fin.readLine();
            fin.close();
            return linea;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Acción que se ejecuta al pulsar sobre cualquier hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        if (juegoBantumi.juegoTerminado()) {
            return;
        }

        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                Log.i(LOG_TAG, "* Juega Jugador");
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                Log.i(LOG_TAG, "* Juega Computador");
                juegoBantumi.juegaComputador();
                break;
            default: // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        String texto = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        if (juegoBantumi.getSemillas(6) == 6 * numInicialSemillas) {
            texto = "¡¡¡ EMPATE !!!";
        }
        Log.i(LOG_TAG, "MainActivity.finJuego(): " + texto + " [J1=" + juegoBantumi.getSemillas(6) + ", J2=" + juegoBantumi.getSemillas(13) + ", inicial=" + numInicialSemillas + "]");

        if (!puntuacionGuardada) {
            guardarPuntuacion();
            puntuacionGuardada = true;
        }

        // terminar
        new FinalAlertDialog(texto).show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    private void guardarPuntuacion() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nombre = sharedPrefs.getString("nombreJugador", getString(R.string.txtPlayer1));
        String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        int semillasJ1 = juegoBantumi.getSemillas(6);
        int semillasJ2 = juegoBantumi.getSemillas(13);

        Puntuacion puntuacion = new Puntuacion(nombre, fechaHora, semillasJ1, semillasJ2);
        AppDatabase.getInstance(this).puntuacionDao().insert(puntuacion);
    }

    /**
     * Guarda el estado actual del juego en un fichero interno de la app
     */
    public void guardarPartida() {
        try {
            String textoJuego = juegoBantumi.serializa();
            FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
            fos.write(textoJuego.getBytes());
            fos.close();
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.txtPartidaGuardadaExito,
                    Snackbar.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error guardando partida: " + e.getMessage());
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.txtPartidaGuardadaError,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Recupera el estado guardado del juego desde el fichero interno
     */
    public void recuperarPartida() {
        String linea = leerLineaFichero();
        if (linea != null) {
            juegoBantumi.deserializa(linea);
            puntuacionGuardada = false;
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.txtPartidaRecuperadaExito,
                    Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.txtPartidaRecuperadaError,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public void reiniciarPartida() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultSemillas = String.valueOf(getResources().getInteger(R.integer.intNumInicialSemillas));
        numInicialSemillas = Integer.parseInt(sharedPrefs.getString("numInicialSemillas", defaultSemillas));
        Log.i(LOG_TAG, "MainActivity.reiniciarPartida(): reiniciando juego con " + numInicialSemillas + " semillas por hueco");

        juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        puntuacionGuardada = false;
    }

}