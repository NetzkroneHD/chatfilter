package de.netzkronehd.chatfilter.database;

import de.netzkronehd.chatfilter.config.ChatFilterConfig;
import de.netzkronehd.chatfilter.database.model.UuidAndName;
import de.netzkronehd.chatfilter.dependency.Dependency;
import de.netzkronehd.chatfilter.dependency.DependencyManager;
import de.netzkronehd.chatfilter.message.MessageState;
import de.netzkronehd.chatfilter.violation.FilterViolation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class Database {

    protected Connection connection;

    protected Class<?> driverClass;

    public void loadDriverClass(DependencyManager dependencyManager) {
        dependencyManager.getClassLoader(getDependency()).ifPresentOrElse(
                driverClass -> this.driverClass = driverClass,
                () -> {throw new RuntimeException("Dependency not loaded: " + getDependency().name());}
        );
    }

    public void connect(ChatFilterConfig.DatabaseConfig config) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        connect(config.getHost(), config.getPort(), config.getDatabase(), config.getUsername(), config.getPassword());
    }

    public void connect(String host, int port, String database, String user, String password) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, IOException {
        connection = createConnection(host, port, database, user, password);
    }

    public abstract Connection createConnection(String host, int port, String database, String user, String password) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, IOException;

    public void createTables() throws SQLException {
        connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS chatfilter_players
                (
                    player_uniqueId VARCHAR(36) PRIMARY KEY,
                    player_name     VARCHAR(16) NOT NULL
                )
                """).executeUpdate();
        connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS chatfilter_violations
                (
                     id              INT PRIMARY KEY AUTO_INCREMENT,
                     player_uniqueId VARCHAR(36),
                     filter_name     TEXT NOT NULL,
                     message_text    TEXT NOT NULL,
                     message_state   VARCHAR(8),
                     message_time    LONG,
                     FOREIGN KEY (player_uniqueId) REFERENCES chatfilter_players(player_uniqueId)
                 )
                """).executeUpdate();
    }

    public int insertPlayer(UUID playerUniqueId, String playerName) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO chatfilter_players (player_uniqueId, player_name)
                VALUES (?, ?)
                """);
        ps.setString(1, playerUniqueId.toString());
        ps.setString(2, playerName);
        return ps.executeUpdate();
    }

    public int insertOrUpdatePlayer(UUID playerUniqueId, String playerName) throws SQLException {
        if (playerExists(playerUniqueId)) {
            return setName(playerUniqueId, playerName);
        } else {
            return insertPlayer(playerUniqueId, playerName);
        }
    }

    public boolean playerExists(UUID playerUniqueId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("""
                SELECT player_uniqueId
                FROM chatfilter_players
                WHERE player_uniqueId = ?
                """);
        ps.setString(1, playerUniqueId.toString());
        return ps.executeQuery().next();
    }

    public int setName(UUID playerUniqueId, String playerName) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("UPDATE chatfilter_players SET player_name = ? WHERE player_uniqueId = ?");
        ps.setString(1, playerName);
        ps.setString(2, playerUniqueId.toString());
        return ps.executeUpdate();
    }

    public Optional<UuidAndName> getUuid(String playerName) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT player_uniqueId, player_name FROM chatfilter_players WHERE LOWER(player_name) = ?");
        ps.setString(1, playerName.toLowerCase());
        final ResultSet rs = ps.executeQuery();
        if (!rs.next()) return Optional.empty();
        return Optional.of(UuidAndName.of(UUID.fromString(rs.getString("player_uniqueId")), rs.getString("player_name")));
    }

    public FilterViolation insertViolation(UUID playerUniqueId, String filterName, String messageText, MessageState state, long messageTime) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO chatfilter_violations (player_uniqueId, filter_name, message_text, message_state, message_time)
                VALUES (?, ?, ?, ?, ?)
                """, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, playerUniqueId.toString());
        ps.setString(2, filterName);
        ps.setString(3, messageText);
        ps.setString(4, state.name());
        ps.setLong(5, messageTime);
        ps.executeUpdate();

        final ResultSet generatedKeys = ps.getGeneratedKeys();
        generatedKeys.next();
        final int id = generatedKeys.getInt(1);
        return new FilterViolation(id, playerUniqueId, filterName, state, messageText, messageTime);
    }

    public Optional<FilterViolation> getViolation(int id) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM chatfilter_violations WHERE id = ?");
        ps.setInt(1, id);
        final var rs = ps.executeQuery();
        if (!rs.next()) return Optional.empty();
        return Optional.of(getViolationFromResultSet(rs));
    }

    public List<FilterViolation> listViolations(UUID playerUniqueId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM chatfilter_violations WHERE player_uniqueId = ?");
        ps.setString(1, playerUniqueId.toString());
        final ResultSet rs = ps.executeQuery();
        final List<FilterViolation> violations = new ArrayList<>();
        while(rs.next()) {
            violations.add(getViolationFromResultSet(rs));
        }
        return violations;
    }

    public List<FilterViolation> listViolations(UUID playerUniqueId, long fromTime, long toTime) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM chatfilter_violations WHERE player_uniqueId = ? AND message_time >= ? AND message_time <= ?");
        ps.setString(1, playerUniqueId.toString());
        ps.setLong(2, fromTime);
        ps.setLong(3, toTime);
        final ResultSet rs = ps.executeQuery();
        final List<FilterViolation> violations = new ArrayList<>();
        while(rs.next()) {
            violations.add(getViolationFromResultSet(rs));
        }
        return violations;
    }

    public List<FilterViolation> listViolations(UUID playerUniqueId, String filterName) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM chatfilter_violations WHERE player_uniqueId = ? AND filter_name = ?");
        ps.setString(1, playerUniqueId.toString());
        ps.setString(2, filterName);
        final ResultSet rs = ps.executeQuery();
        final List<FilterViolation> violations = new ArrayList<>();
        while(rs.next()) {
            violations.add(getViolationFromResultSet(rs));
        }
        return violations;
    }

    public List<FilterViolation> listViolations(UUID playerUniqueId, String filterName, long fromTime, long toTime) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM chatfilter_violations WHERE player_uniqueId = ? AND lower(filter_name) = ? AND message_time >= ? AND message_time <= ?");
        ps.setString(1, playerUniqueId.toString());
        ps.setString(2, filterName.toLowerCase());
        ps.setLong(3, fromTime);
        ps.setLong(4, toTime);
        final ResultSet rs = ps.executeQuery();
        final List<FilterViolation> violations = new ArrayList<>();
        while(rs.next()) {
            violations.add(getViolationFromResultSet(rs));
        }
        return violations;
    }

    public List<FilterViolation> listViolations() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("SELECT * FROM chatfilter_violations");
        final ResultSet rs = ps.executeQuery();
        final List<FilterViolation> violations = new ArrayList<>();
        while(rs.next()) {
            violations.add(getViolationFromResultSet(rs));
        }
        return violations;
    }

    public int deleteViolation(int id) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("DELETE FROM chatfilter_violations WHERE id = ?");
        ps.setInt(1, id);
        return ps.executeUpdate();
    }

    public int deleteViolations(UUID playerUniqueId) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("DELETE FROM chatfilter_violations WHERE player_uniqueId = ?");
        ps.setString(1, playerUniqueId.toString());
        return ps.executeUpdate();
    }

    public int deleteViolations(UUID playerUniqueId, long fromTime, long toTime) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("DELETE FROM chatfilter_violations WHERE player_uniqueId = ? AND message_time >= ? AND message_time <= ?");
        ps.setString(1, playerUniqueId.toString());
        ps.setLong(2, fromTime);
        ps.setLong(3, toTime);
        return ps.executeUpdate();
    }

    public int deleteViolations(UUID playerUniqueId, String filterName, long fromTime, long toTime) throws SQLException {
        final PreparedStatement ps = connection.prepareStatement("DELETE FROM chatfilter_violations WHERE player_uniqueId = ? AND lower(filter_name) = ? AND message_time >= ? AND message_time <= ?");
        ps.setString(1, playerUniqueId.toString());
        ps.setString(2, filterName.toLowerCase());
        ps.setLong(3, fromTime);
        ps.setLong(4, toTime);
        return ps.executeUpdate();
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public void close() throws SQLException {
        if (connection == null) return;
        connection.close();
    }

    private FilterViolation getViolationFromResultSet(ResultSet rs) throws SQLException {
        return new FilterViolation(
                rs.getInt("id"),
                UUID.fromString(rs.getString("player_uniqueId")),
                rs.getString("filter_name"),
                MessageState.valueOf(rs.getString("message_state")),
                rs.getString("message_text"),
                rs.getLong("message_time")
        );
    }

    public abstract String getName();
    public abstract String getClassName();
    public abstract Dependency getDependency();

}
