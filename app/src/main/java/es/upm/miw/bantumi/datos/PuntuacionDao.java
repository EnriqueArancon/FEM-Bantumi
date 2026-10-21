package es.upm.miw.bantumi.datos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PuntuacionDao {

    @Insert
    long insert(Puntuacion puntuacion);

    @Query("SELECT * FROM puntuaciones ORDER BY max_semillas DESC LIMIT 10")
    List<Puntuacion> getMejoresResultados();

    @Query("SELECT * FROM puntuaciones")
    List<Puntuacion> getAll();

    @Query("DELETE FROM puntuaciones")
    void deleteAll();
}
