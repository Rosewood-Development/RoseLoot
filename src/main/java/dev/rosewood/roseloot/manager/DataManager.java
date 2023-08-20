package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.SQLiteConnector;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import dev.rosewood.roseloot.database.migrations._1_Add_Table_Cooldowns;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DataManager extends AbstractDataManager {

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(
                _1_Add_Table_Cooldowns.class
        );
    }

    @Override
    public void reload() {
        this.databaseConnector = new SQLiteConnector(this.rosePlugin);
    }

    public void setCooldowns(Collection<CooldownManager.Cooldown> cooldowns) {
        this.databaseConnector.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "cooldowns")) {
                statement.executeUpdate();
            }

            if (cooldowns.isEmpty())
                return;

            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "cooldowns (id, player, expiration) VALUES (?, ?, ?)")) {
                for (CooldownManager.Cooldown cooldown : cooldowns) {
                    statement.setString(1, cooldown.id());
                    statement.setString(2, cooldown.player().toString());
                    statement.setLong(3, cooldown.expiration());
                    statement.addBatch();
                }

                statement.executeBatch();
            }
        });
    }

    public Collection<CooldownManager.Cooldown> getCooldowns() {
        List<CooldownManager.Cooldown> cooldownList = new ArrayList<>();
        this.databaseConnector.connect(connection -> {
            ResultSet cooldowns = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "cooldowns").executeQuery();
            while (cooldowns.next()) {
                cooldownList.add(new CooldownManager.Cooldown(
                        cooldowns.getString("id"),
                        UUID.fromString(cooldowns.getString("player")),
                        cooldowns.getLong("expiration")
                ));
            }
        });
        return cooldownList;
    }

}
