package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Combate;
import model.Equipo;
import model.GrupoPPV;
import model.Loitador;
import model.Marca;
import model.PPV;
import model.Titulo;

/**
 *
 * @author iagom
 */
public class OperacionsBD {

    private static final String URL = "jdbc:mysql://zephyr.proxy.rlwy.net:11945/wwe_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000&socketTimeout=30000";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection conn = null;

    public OperacionsBD() {
        conn = getConexion();
    }

    public static Connection getConexion() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return conn;
    }

    public static void cerrarConexion() throws SQLException {
        getConexion().close();
    }
    // COMBATE 

    public List<Combate> listarPorPpv(int idPpv) {
        List<Combate> lista = new ArrayList<>();
        String sql = "SELECT id_combate, id_ppv, id_titulo_en_xogo, id_loitador_ganador, tipo_combate, orde_no_ppv "
                + "FROM combate WHERE id_ppv = ? ORDER BY orde_no_ppv ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPpv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Combate c = new Combate();
                    c.setIdCombate(rs.getInt("id_combate"));
                    c.setIdPpv(rs.getInt("id_ppv"));

                    int idTitulo = rs.getInt("id_titulo_en_xogo");
                    c.setIdTituloEnXogo(rs.wasNull() ? null : idTitulo);

                    int idGanador = rs.getInt("id_loitador_ganador");
                    c.setIdLoitadorGanador(rs.wasNull() ? null : idGanador);

                    c.setTipoCombate(rs.getString("tipo_combate"));
                    c.setOrdeNoPpv(rs.getInt("orde_no_ppv"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardar(Combate c) {
        if (c.getIdCombate() == 0) {
            return combateInsertar(c);
        } else {
            return combateActualizar(c);
        }
    }

    private boolean combateInsertar(Combate c) {
        String sql = "INSERT INTO combate (id_ppv, id_titulo_en_xogo, id_loitador_ganador, tipo_combate, orde_no_ppv) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdPpv());

            if (c.getIdTituloEnXogo() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, c.getIdTituloEnXogo());
            }

            if (c.getIdLoitadorGanador() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, c.getIdLoitadorGanador());
            }

            ps.setString(4, c.getTipoCombate());
            ps.setInt(5, c.getOrdeNoPpv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean combateActualizar(Combate c) {
        String sql = "UPDATE combate SET id_ppv = ?, id_titulo_en_xogo = ?, id_loitador_ganador = ?, tipo_combate = ?, orde_no_ppv = ? WHERE id_combate = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdPpv());

            if (c.getIdTituloEnXogo() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, c.getIdTituloEnXogo());
            }

            if (c.getIdLoitadorGanador() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, c.getIdLoitadorGanador());
            }

            ps.setString(4, c.getTipoCombate());
            ps.setInt(5, c.getOrdeNoPpv());
            ps.setInt(6, c.getIdCombate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean combateEliminar(int idCombate) {
        String sql = "DELETE FROM combate WHERE id_combate = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Loitador> listarParticipantesPorCombate(int idCombate) {
        List<Loitador> lista = new ArrayList<>();
        String sql = "SELECT l.id_loitador, l.nome, l.estado, l.categoria_peso, l.entrada, l.foto_url, l.veces_consultado "
                + "FROM loitador l "
                + "JOIN combate_loitador cl ON l.id_loitador = cl.id_loitador "
                + "WHERE cl.id_combate = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Loitador l = new Loitador();
                    l.setId_loitador(rs.getInt("id_loitador"));
                    l.setNome(rs.getString("nome"));
                    l.setEstado(rs.getString("estado"));
                    l.setCategoria_peso(rs.getString("categoria_peso"));
                    l.setEntrada(rs.getString("entrada"));
                    l.setFoto_url(rs.getString("foto_url"));
                    l.setVeces_consultado(rs.getInt("veces_consultado"));
                    lista.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardarCombateYParticipantes(Combate c, List<Integer> idParticipantes) {
        try {
            conn.setAutoCommit(false);

            boolean exitoCombate;
            if (c.getIdCombate() == 0) {
                exitoCombate = combateInsertarConClave(c);
            } else {
                exitoCombate = combateActualizar(c);
            }

            if (!exitoCombate) {
                conn.rollback();
                return false;
            }

            String sqlDelete = "DELETE FROM combate_loitador WHERE id_combate = ?";
            try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                psDel.setInt(1, c.getIdCombate());
                psDel.executeUpdate();
            }

            String sqlInsert = "INSERT INTO combate_loitador (id_combate, id_loitador) VALUES (?, ?)";
            try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                for (Integer idLoitador : idParticipantes) {
                    psIns.setInt(1, c.getIdCombate());
                    psIns.setInt(2, idLoitador);
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean combateInsertarConClave(Combate c) {
        String sql = "INSERT INTO combate (id_ppv, id_titulo_en_xogo, id_loitador_ganador, tipo_combate, orde_no_ppv) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getIdPpv());

            if (c.getIdTituloEnXogo() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, c.getIdTituloEnXogo());
            }

            if (c.getIdLoitadorGanador() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, c.getIdLoitadorGanador());
            }

            ps.setString(4, c.getTipoCombate());
            ps.setInt(5, c.getOrdeNoPpv());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdCombate(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //EQUIPO 
    public List<Equipo> equipoListar() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT equipo.*, marca.nome FROM equipo "
                + "INNER JOIN marca ON equipo.id_marca = marca.id_marca";

        try (Connection con = OperacionsBD.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo eq = new Equipo();
                eq.setId_equipo(rs.getInt("id_equipo"));
                eq.setNome_equipo(rs.getString("nome_equipo"));
                eq.setDescripcion(rs.getString("descripcion"));
                eq.setFoto_url(rs.getString("foto_url"));
                Marca m = new Marca();
                m.setId_marca(rs.getInt("id_marca"));
                m.setNome_marca(rs.getString("nome"));
                eq.setMarca(m);

                lista.add(eq);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
        }
        return lista;
    }

    public boolean equipoInsertar(Equipo eq) {
        String sql = "INSERT INTO equipo (nome_equipo, id_marca, descripcion, foto_url) VALUES (?, ?, ?, ?)";
        try (Connection con = OperacionsBD.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, eq.getNome_equipo());
            ps.setInt(2, eq.getMarca().getId_marca());
            ps.setString(3, eq.getDescripcion());
            ps.setString(4, eq.getFoto_url());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar equipo: " + e.getMessage());
            return false;
        }
    }

    public boolean equipoActualizar(Equipo eq) {
        String sql = "UPDATE equipo SET nome_equipo=?, id_marca=?, descripcion=?, foto_url=? WHERE id_equipo=?";
        try (Connection con = OperacionsBD.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, eq.getNome_equipo());
            ps.setInt(2, eq.getMarca().getId_marca());
            ps.setString(3, eq.getDescripcion());
            ps.setString(4, eq.getFoto_url());
            ps.setInt(5, eq.getId_equipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar equipo: " + e.getMessage());
            return false;
        }
    }

    public boolean equipoEliminar(int id) {
        String sql = "DELETE FROM equipo WHERE id_equipo=?";
        try (Connection con = OperacionsBD.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar equipo: " + e.getMessage());
            return false;
        }
    }

    // LUCHADOR 
    public void luchadorGuardar(Loitador l) throws SQLException {
        String sql;
        if (l.getId_loitador() == 0) {
            sql = "INSERT INTO loitador (nome, estado, categoria_peso, entrada, foto_url,veces_consultado) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE loitador SET nome = ?, estado = ?, categoria_peso = ?, entrada = ?, foto_url = ?, veces_consultado = ? WHERE id_loitador = ?";
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getNome());
            ps.setString(2, l.getEstado());
            ps.setString(3, l.getCategoria_peso());
            ps.setString(4, l.getEntrada());
            ps.setString(5, l.getFoto_url());
            ps.setInt(6, l.getVeces_consultado()+1);

            if (l.getId_loitador() != 0) {
                ps.setInt(7, l.getId_loitador());
            }

            ps.executeUpdate();
        }
    }

    public List<Loitador> luchadorListar() throws SQLException {
        List<Loitador> lista = new ArrayList<>();
        String sql = "SELECT * FROM loitador ORDER BY nome ASC";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Loitador l = new Loitador();
                l.setId_loitador(rs.getInt("id_loitador"));
                l.setNome(rs.getString("nome"));
                l.setEstado(rs.getString("estado"));
                l.setCategoria_peso(rs.getString("categoria_peso"));
                l.setEntrada(rs.getString("entrada"));
                l.setFoto_url(rs.getString("foto_url"));
                l.setVeces_consultado(rs.getInt("veces_consultado"));
                lista.add(l);
            }
        }
        return lista;
    }

    public void luchadorEliminar(int id) throws SQLException {
        String sql = "DELETE FROM loitador WHERE id_loitador = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void incrementarVecesConsultado(int idLoitador) throws SQLException {
        String sql = "UPDATE loitador SET veces_consultado = veces_consultado + 1 WHERE id_loitador = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLoitador);
            pstmt.executeUpdate();
        }
    }

    // MARCA
    public void marcaGuardar(Marca m) throws SQLException {
        String sql;
        if (m.getId_marca() == 0) {
            sql = "INSERT INTO marca (nome, foto_url) VALUES (?, ?)";
        } else {
            sql = "UPDATE marca SET nome = ?, foto_url = ? WHERE id_marca = ?";
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNome_marca());
            ps.setString(2, m.getLogo_url());
            if (m.getId_marca() != 0) {
                ps.setInt(3, m.getId_marca());
            }
            ps.executeUpdate();
        }
    }

    public List<Marca> marcaListar() throws SQLException {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT * FROM marca ORDER BY nome";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Marca(
                        rs.getInt("id_marca"),
                        rs.getString("nome"),
                        rs.getString("foto_url")
                ));
            }
        }
        return lista;
    }

    public void marcaEliminar(int id) throws SQLException {
        String sql = "DELETE FROM marca WHERE id_marca = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    // PPV 

    public List<PPV> PPVListar() throws SQLException {
        List<PPV> lista = new ArrayList<>();
        String sql = "SELECT p.id_ppv, p.nome, p.data_celebracion, p.estado, p.localizacion, p.foto_url, p.id_grupo_ppv, "
                + "g.nome_grupo, g.descripcion_importancia, g.data_habitual, g.foto_url AS grupo_foto "
                + "FROM ppv p "
                + "INNER JOIN grupo_ppv g ON p.id_grupo_ppv = g.id_grupo_ppv "
                + "ORDER BY p.nome ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PPV p = new PPV();
                p.setId_ppv(rs.getInt("id_ppv"));
                p.setNombre(rs.getString("nome"));
                p.setDataCelebracion(rs.getDate("data_celebracion"));
                p.setEstado(rs.getString("estado"));
                p.setLocalizacion(rs.getString("localizacion"));
                p.setUrlPoster(rs.getString("foto_url"));

                GrupoPPV g = new GrupoPPV();
                g.setIdGrupoPpv(rs.getInt("id_grupo_ppv"));
                g.setNomeGrupo(rs.getString("nome_grupo"));
                g.setDescripcionImportancia(rs.getString("descripcion_importancia"));
                g.setDataHabitual(rs.getString("data_habitual"));
                g.setFotoUrl(rs.getString("grupo_foto"));

                p.setGrupoPPV(g);
                lista.add(p);
            }
        }
        return lista;
    }

    public void PPVGuardar(PPV p) throws SQLException {
        if (p.getId_ppv() == 0) {
            PPVInsertar(p);
        } else {
            PPVActualizar(p);
        }
    }

    private void PPVInsertar(PPV p) throws SQLException {
        String sql = "INSERT INTO ppv (nome, data_celebracion, estado, localizacion, foto_url, id_grupo_ppv) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());

            if (p.getDataCelebracion() != null) {
                ps.setDate(2, new java.sql.Date(p.getDataCelebracion().getTime()));
            } else {
                ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            }

            ps.setString(3, p.getEstado() != null ? p.getEstado() : "pendente");

            if (p.getLocalizacion() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, p.getLocalizacion());
            }

            if (p.getUrlPoster() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, p.getUrlPoster());
            }

            ps.setInt(6, p.getGrupoPPV().getIdGrupoPpv());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId_ppv(rs.getInt(1));
                }
            }
        }
    }

    private void PPVActualizar(PPV p) throws SQLException {
        String sql = "UPDATE ppv SET nome = ?, data_celebracion = ?, estado = ?, localizacion = ?, foto_url = ?, id_grupo_ppv = ? WHERE id_ppv = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());

            if (p.getDataCelebracion() != null) {
                ps.setDate(2, new java.sql.Date(p.getDataCelebracion().getTime()));
            } else {
                ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            }

            ps.setString(3, p.getEstado() != null ? p.getEstado() : "pendente");

            if (p.getLocalizacion() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, p.getLocalizacion());
            }

            if (p.getUrlPoster() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, p.getUrlPoster());
            }

            ps.setInt(6, p.getGrupoPPV().getIdGrupoPpv());
            ps.setInt(7, p.getId_ppv());

            ps.executeUpdate();
        }
    }

    public void PPVEliminar(int id) throws SQLException {
        String sql = "DELETE FROM ppv WHERE id_ppv = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    // GRUPO PPV 

    public List<GrupoPPV> grupoPPVListar() {
        List<GrupoPPV> lista = new ArrayList<>();
        String sql = "SELECT id_grupo_ppv, nome_grupo, descripcion_importancia, data_habitual, foto_url "
                + "FROM grupo_ppv ORDER BY nome_grupo ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                GrupoPPV g = new GrupoPPV();
                g.setIdGrupoPpv(rs.getInt("id_grupo_ppv"));
                g.setNomeGrupo(rs.getString("nome_grupo"));
                g.setDescripcionImportancia(rs.getString("descripcion_importancia"));
                g.setDataHabitual(rs.getString("data_habitual"));
                g.setFotoUrl(rs.getString("foto_url"));

                lista.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean grupoPPVInsertar(GrupoPPV g) {
        String sql = "INSERT INTO grupo_ppv (nome_grupo, descripcion_importancia, data_habitual, foto_url) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getNomeGrupo());

            if (g.getDescripcionImportancia() == null) {
                ps.setNull(2, Types.LONGVARCHAR);
            } else {
                ps.setString(2, g.getDescripcionImportancia());
            }

            if (g.getDataHabitual() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, g.getDataHabitual());
            }

            if (g.getFotoUrl() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, g.getFotoUrl());
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // TITULO
    public List<Titulo> tituloListar() throws SQLException {
        List<Titulo> lista = new ArrayList<>();
        String sql = "SELECT * FROM titulo ORDER BY nome ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Titulo t = new Titulo();
                t.setId_titulo(rs.getInt("id_titulo"));
                t.setNombre(rs.getString("nome"));
                t.setEstado(rs.getString("estado"));
                t.setFechaVigencia(rs.getDate("data_vixencia"));
                t.setUrlFoto(rs.getString("foto_url"));
                t.setMaximo(rs.getBoolean("es_titulo_maximo"));
                lista.add(t);
            }
        }
        return lista;
    }

    public void tituloGuardar(Titulo t) throws SQLException {
        if (t.getId_titulo() == 0) {
            tituloInsertar(t);
        } else {
            tituloActualizar(t);
        }
    }

    private void tituloInsertar(Titulo t) throws SQLException {
        String sql = "INSERT INTO titulo (nome, estado, data_vixencia, foto_url , es_titulo_maximo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getEstado());
            ps.setDate(3, (Date) t.getFechaVigencia());
            ps.setString(4, t.getUrlFoto());
            ps.setBoolean(5, t.isMaximo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId_titulo(rs.getInt(1));
                }
            }
        }
    }

    private void tituloActualizar(Titulo t) throws SQLException {
        String sql = "UPDATE titulo SET nome = ?, estado = ?, data_vixencia = ?, foto_url= ?, es_titulo_maximo = ? WHERE id_titulo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getEstado());
            ps.setDate(3, (Date) t.getFechaVigencia());
            ps.setString(4, t.getUrlFoto());
            ps.setBoolean(5, t.isMaximo());
            ps.setInt(6, t.getId_titulo());
            ps.executeUpdate();
        }
    }

    public void tituloEliminar(int id) throws SQLException {
        String sql = "DELETE FROM titulo WHERE id_titulo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ESTADISTICAS
    public String getLoitadorMaisCombatesPPV() throws SQLException {
        String sql = "SELECT l.nome, COUNT(cl.id_combate) as total "
                + "FROM loitador l "
                + "JOIN combate_loitador cl ON l.id_loitador = cl.id_loitador "
                + "GROUP BY l.id_loitador ORDER BY total DESC LIMIT 1";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getString("nome") : "Ningún";
        }
    }

    public String getMarcaConMaisLoitadores() throws SQLException {
        String sql = "SELECT m.nome, COUNT(l.id_loitador) as total "
                + "FROM marca m "
                + "JOIN loitador l ON m.id_marca = l.id_marca "
                + "GROUP BY m.id_marca ORDER BY total DESC LIMIT 1";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getString("nome") : "Ningunha";
        }
    }

    public String getPPVConMaisFechas() throws SQLException {
        String sql = "SELECT gp.nome_grupo, COUNT(p.id_ppv) as total "
                + "FROM grupo_ppv gp "
                + "JOIN ppv p ON gp.id_grupo_ppv = p.id_grupo_ppv "
                + "GROUP BY gp.id_grupo_ppv ORDER BY total DESC LIMIT 1";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getString("nome_grupo") : "Ningún";
        }
    }

    public List<String> getLoitadoresUltimaSemana() throws SQLException {
        String sql = "SELECT nome FROM loitador "
                + "WHERE data_modificacion >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
        return ejecutarConsultaLista(sql);
    }

    public List<String> getTitulosUltimaSemana() throws SQLException {
        String sql = "SELECT nome FROM titulo "
                + "WHERE data_modificacion >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
        return ejecutarConsultaLista(sql);
    }

    private List<String> ejecutarConsultaLista(String sql) throws SQLException {
        List<String> resultados = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultados.add(rs.getString(1));
            }
        }
        return resultados;
    }

    public String getLoitadorMaisConsultado() {
        String sql = "SELECT nome FROM loitador ORDER BY veces_consultado DESC LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("nome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Non hai datos";
    }
}
