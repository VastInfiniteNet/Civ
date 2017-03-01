package com.programmerdan.minecraft.banstick;

import com.programmerdan.minecraft.banstick.handler.BanStickCommandHandler;
import com.programmerdan.minecraft.banstick.handler.BanStickDatabaseHandler;
import com.programmerdan.minecraft.banstick.handler.BanStickEventHandler;
import com.programmerdan.minecraft.banstick.handler.BanStickIPDataHandler;
import com.programmerdan.minecraft.banstick.handler.BanStickProxyHandler;
import com.programmerdan.minecraft.banstick.handler.BanStickScrapeHandler;
import com.programmerdan.minecraft.banstick.handler.BanStickTorUpdater;

import vg.civcraft.mc.civmodcore.ACivMod;

public class BanStick extends ACivMod {
	private static BanStick instance;
	private BanStickCommandHandler commandHandler;
	private BanStickEventHandler eventHandler;
	private BanStickDatabaseHandler databaseHandler;
	private BanStickTorUpdater torUpdater;
	private BanStickProxyHandler proxyHandler;
	private BanStickIPDataHandler ipdataUpdater;
	private BanStickScrapeHandler scrapeHandler;
	
	@Override
	public void onEnable() {
		super.onEnable();

		saveDefaultConfig();
		reloadConfig();
		
		BanStick.instance = this;
		connectDatabase();
		if (!this.isEnabled()) return;

		registerEventHandler();
		registerCommandHandler();
		registerTorHandler();
		registerProxyHandler();
		registerIPDataHandler();
		registerScrapeHandler();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		if (this.eventHandler != null) this.eventHandler.shutdown();
		if (this.proxyHandler != null) this.proxyHandler.shutdown();
		if (this.scrapeHandler != null) this.scrapeHandler.shutdown();
		if (this.ipdataUpdater != null) this.ipdataUpdater.end();
		if (this.torUpdater != null) this.torUpdater.shutdown();
		if (this.databaseHandler != null) this.databaseHandler.doShutdown();
	}
	
	private void connectDatabase() {
		try {
			this.databaseHandler = new BanStickDatabaseHandler(getConfig());
		} catch (Exception e) {
			this.severe("Failed to establish database", e);
			this.setEnabled(false);
		}
	}
	
	public BanStickIPDataHandler getIPDataHandler() {
		return this.ipdataUpdater;
	}

	private void registerCommandHandler() {
		if (!this.isEnabled()) return;
		try {
			this.commandHandler = new BanStickCommandHandler(getConfig());
		} catch (Exception e) {
			this.severe("Failed to set up command handling", e);
			this.setEnabled(false);
		}
	}

	private void registerEventHandler() {
		if (!this.isEnabled()) return;
		try {
			this.eventHandler = new BanStickEventHandler(getConfig());
		} catch (Exception e) {
			this.severe("Failed to set up event capture / handling", e);
			this.setEnabled(false);
		}	
	}
	
	private void registerTorHandler() {
		if (!this.isEnabled()) return;
		try {
			this.torUpdater = new BanStickTorUpdater(getConfig());
		} catch (Exception e) {
			this.severe("Failed to set up TOR updater!", e);
		}
	}
	
	private void registerProxyHandler() {
		if (!this.isEnabled()) return;
		try {
			this.proxyHandler = new BanStickProxyHandler(getConfig(), getPlugin().getClassLoader());
		} catch (Exception e) {
			this.severe("Failed to set up Proxy updaters!", e);
		}
	}
	
	private void registerIPDataHandler() {
		if (!this.isEnabled()) return;
		try {
			this.ipdataUpdater = new BanStickIPDataHandler(getConfig());
		} catch (Exception e) {
			this.severe("Failed to set up dynamic IPData updater!", e);
		}
	}
	
	private void registerScrapeHandler() {
		if (!this.isEnabled()) return;
		try {
			this.scrapeHandler = new BanStickScrapeHandler(getConfig(), getPlugin().getClassLoader());
		} catch (Exception e) {
			this.severe("Failed to set up anonymous proxy scrapers", e);
		}
	}

	/**
	 * 
	 * @return the static global instance. Not my fav pattern, but whatever.
	 */
	public static BanStick getPlugin() {
		return BanStick.instance;
	}
	
	/**
	 * 
	 * @return the name of this plugin.
	 */
	@Override
	protected String getPluginName() {
		return "BanStick";
	}

	public void saveCache() {
		this.databaseHandler.doShutdown();
	}

}
