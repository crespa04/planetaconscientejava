package com.app.planetaconsciente.repository;

import com.app.planetaconsciente.model.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    // Consulta para obtener todas las fuentes distintas
    @Query("SELECT DISTINCT n.fuente FROM Noticia n WHERE n.fuente IS NOT NULL ORDER BY n.fuente")
    List<String> findAllFuentesDistinct();

    // Consulta paginada con filtros avanzados
    @Query("SELECT n FROM Noticia n WHERE " +
        "(:busqueda IS NULL OR LOWER(n.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(n.resumen) LIKE LOWER(CONCAT('%', :busqueda, '%'))) AND " +
        "(:fuente IS NULL OR :fuente = '' OR n.fuente = :fuente) AND " +
        "(:fechaDesde IS NULL OR n.fechaPublicacion >= :fechaDesde) AND " +
        "(:fechaHasta IS NULL OR n.fechaPublicacion <= :fechaHasta) " +
        "ORDER BY n.fechaPublicacion DESC")
    Page<Noticia> findByFiltros(
        @Param("busqueda") String busqueda,
        @Param("fuente") String fuente,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta,
        Pageable pageable
    );

    // Consulta sin paginación para exportar a PDF
    @Query("SELECT n FROM Noticia n WHERE " +
        "(:busqueda IS NULL OR LOWER(n.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(n.resumen) LIKE LOWER(CONCAT('%', :busqueda, '%'))) AND " +
        "(:fuente IS NULL OR :fuente = '' OR n.fuente = :fuente) AND " +
        "(:fechaDesde IS NULL OR n.fechaPublicacion >= :fechaDesde) AND " +
        "(:fechaHasta IS NULL OR n.fechaPublicacion <= :fechaHasta) " +
        "ORDER BY n.fechaPublicacion DESC")
    List<Noticia> findByFiltrosForExport(
        @Param("busqueda") String busqueda,
        @Param("fuente") String fuente,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    // Consulta para noticias destacadas
    List<Noticia> findByDestacadaTrueOrderByFechaPublicacionDesc();

    // Consulta para buscar por ID
    Optional<Noticia> findById(Long id);

    // Consulta para verificar existencia
    boolean existsById(Long id);

    // Consulta adicional para búsqueda por título (opcional)
    List<Noticia> findByTituloContainingIgnoreCase(String titulo);

    // Consulta adicional para noticias recientes (opcional)
    List<Noticia> findTop5ByOrderByFechaPublicacionDesc();

    // Consulta para verificar existencia por título (evitar duplicados)
    boolean existsByTituloIgnoreCase(String titulo);
}