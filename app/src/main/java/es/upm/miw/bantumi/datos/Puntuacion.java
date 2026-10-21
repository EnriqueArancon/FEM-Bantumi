package es.upm.miw.bantumi.datos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "puntuaciones")
public class Puntuacion {

    @PrimaryKey(autoGenerate = true)
    protected int id;

    @ColumnInfo(name = "nombre_jugador")
    protected String nombreJugador;

    @ColumnInfo(name = "fecha_hora")
    protected String fechaHora;

    @ColumnInfo(name = "semillas_almacen1")
    protected int semillasAlmacen1;

    @ColumnInfo(name = "semillas_almacen2")
    protected int semillasAlmacen2;

    @ColumnInfo(name = "max_semillas")
    protected int maxSemillas;

    public Puntuacion(String nombreJugador, String fechaHora, int semillasAlmacen1, int semillasAlmacen2) {
        this.nombreJugador = nombreJugador;
        this.fechaHora = fechaHora;
        this.semillasAlmacen1 = semillasAlmacen1;
        this.semillasAlmacen2 = semillasAlmacen2;
        this.maxSemillas = Math.max(semillasAlmacen1, semillasAlmacen2);
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getSemillasAlmacen1() {
        return semillasAlmacen1;
    }

    public void setSemillasAlmacen1(int semillasAlmacen1) {
        this.semillasAlmacen1 = semillasAlmacen1;
    }

    public int getSemillasAlmacen2() {
        return semillasAlmacen2;
    }

    public void setSemillasAlmacen2(int semillasAlmacen2) {
        this.semillasAlmacen2 = semillasAlmacen2;
    }

    public int getMaxSemillas() {
        return maxSemillas;
    }

    public void setMaxSemillas(int maxSemillas) {
        this.maxSemillas = maxSemillas;
    }
}
