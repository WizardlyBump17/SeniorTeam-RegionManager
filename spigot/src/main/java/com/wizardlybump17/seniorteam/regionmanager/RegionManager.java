package com.wizardlybump17.seniorteam.regionmanager;

import com.wizardlybump17.seniorteam.regionmanager.command.RegionCommand;
import com.wizardlybump17.seniorteam.regionmanager.command.reader.RegionArgsReader;
import com.wizardlybump17.seniorteam.regionmanager.command.reader.RegionFlagTypeArgsReader;
import com.wizardlybump17.seniorteam.regionmanager.command.reader.RegionFlagValueArgsReader;
import com.wizardlybump17.seniorteam.regionmanager.listener.PlayerListener;
import com.wizardlybump17.seniorteam.regionmanager.listener.RegionListener;
import com.wizardlybump17.wlib.command.CommandManager;
import com.wizardlybump17.wlib.command.args.ArgsReaderRegistry;
import com.wizardlybump17.wlib.command.holder.BukkitCommandHolder;
import com.wizardlybump17.wlib.config.holder.BukkitConfigHolderFactory;
import com.wizardlybump17.wlib.config.registry.ConfigHandlerRegistry;
import com.wizardlybump17.wlib.config.registry.ConfigHolderFactoryRegistry;
import org.bukkit.Bukkit;

public class RegionManager extends com.wizardlybump17.seniorteam.regionmanager.api.RegionManager {

    @Override
    public void onEnable() {
        super.onEnable();

        Bukkit.getPluginManager().registerEvents(new RegionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        initCommands();
    }

    @Override
    protected void initConfigs() {
        BukkitConfigHolderFactory factory = new BukkitConfigHolderFactory(this);
        ConfigHolderFactoryRegistry.getInstance().put(RegionManager.class, factory);
        ConfigHolderFactoryRegistry.getInstance().put(com.wizardlybump17.seniorteam.regionmanager.api.RegionManager.class, factory);

        super.initConfigs();

        ConfigHandlerRegistry.getInstance().register(RegionCommand.class);
    }

    private void initCommands() {
        ArgsReaderRegistry.INSTANCE.add(new RegionArgsReader(getRegionCache()));
        ArgsReaderRegistry.INSTANCE.add(new RegionFlagTypeArgsReader(getRegionFlagTypeCache()));
        ArgsReaderRegistry.INSTANCE.add(new RegionFlagValueArgsReader(getRegionFlagValueReaderRegistry()));

        new CommandManager(new BukkitCommandHolder(this)).registerCommands(
                new RegionCommand(this)
        );
    }
}
