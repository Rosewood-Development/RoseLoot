package dev.rosewood.roseloot.loot.item.component.common.stable;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ProfileComponent implements LootItemComponent {

    private final UUID uuid;
    private final String name;
    private final List<ProfileProperty> properties;

    public ProfileComponent(ConfigurationSection section) {
        ConfigurationSection profileSection = section.getConfigurationSection("profile");
        if (profileSection != null) {
            String uuidString = profileSection.getString("id");
            this.uuid = uuidString != null ? UUID.fromString(uuidString) : null;
            this.name = profileSection.getString("name");
            
            this.properties = new ArrayList<>();
            ConfigurationSection propertiesSection = profileSection.getConfigurationSection("properties");
            if (propertiesSection != null) {
                for (String key : propertiesSection.getKeys(false)) {
                    ConfigurationSection propertySection = propertiesSection.getConfigurationSection(key);
                    if (propertySection == null) 
                        continue;
                        
                    String name = propertySection.getString("name");
                    String value = propertySection.getString("value");
                    String signature = propertySection.getString("signature");
                    
                    if (name != null && value != null)
                        this.properties.add(new ProfileProperty(name, value, signature));
                }
            }
        } else {
            this.uuid = null;
            this.name = null;
            this.properties = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        if (this.uuid != null || this.name != null || (this.properties != null && !this.properties.isEmpty())) {
            ResolvableProfile profile = ResolvableProfile.resolvableProfile()
                    .uuid(this.uuid)
                    .name(this.name)
                    .addProperties(this.properties != null ? this.properties : List.of())
                    .build();
                    
            itemStack.setData(DataComponentTypes.PROFILE, profile);
        }
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.PROFILE))
            return;

        ResolvableProfile profile = itemStack.getData(DataComponentTypes.PROFILE);
        stringBuilder.append("profile:\n");
        
        if (profile.uuid() != null)
            stringBuilder.append("  id: ").append(profile.uuid()).append('\n');
            
        if (profile.name() != null)
            stringBuilder.append("  name: ").append(profile.name()).append('\n');
        
        if (!profile.properties().isEmpty()) {
            stringBuilder.append("  properties:\n");
            int index = 0;
            for (ProfileProperty property : profile.properties()) {
                stringBuilder.append("    ").append(index++).append(":\n");
                stringBuilder.append("      name: ").append(property.getName()).append('\n');
                stringBuilder.append("      value: ").append(property.getValue()).append('\n');
                if (property.isSigned())
                    stringBuilder.append("      signature: ").append(property.getSignature()).append('\n');
            }
        }
    }

} 
