package me.client.module;

import java.util.ArrayList;


import me.client.module.combat.*;
import me.client.module.player.*;
import me.client.module.misc.*;
import me.client.module.movement.*;
import me.client.module.render.*;
public class ModuleManager {

	public ArrayList<Module> modules;
	
	public ModuleManager() {
		(modules = new ArrayList<Module>()).clear();
		this.modules.add(new ClickGUI());
		this.modules.add(new HUD());
		this.modules.add(new SelfDestruct());	
	    
        this.modules.add(new AimAssist());
	    this.modules.add(new AutoClicker());
		this.modules.add(new Wtap());
		this.modules.add(new STap());
        this.modules.add(new TradeHelper());
		this.modules.add(new TimmingHit());
		this.modules.add(new HitSelect());
		this.modules.add(new OneHitSelect());		
		this.modules.add(new LegitHitSelect());
		this.modules.add(new JumpReset());
		this.modules.add(new Reach());
		this.modules.add(new Velocity());
		this.modules.add(new LegitVelocity());
	
	    this.modules.add(new MobNametag());
		this.modules.add(new NameTags());
        this.modules.add(new ESP());
		this.modules.add(new Fullbright());
		this.modules.add(new MidHitSelect());
		this.modules.add(new AutoLeave());
		this.modules.add(new Sprint());
        this.modules.add(new Timer());        
        this.modules.add(new Bhop());
        this.modules.add(new NoJumpDelay());
		this.modules.add(new Parkour());
        this.modules.add(new AutoWeapon());
		this.modules.add(new AutoTool());
		this.modules.add(new FastBridge());
		this.modules.add(new FastPlace());
		
		
		this.modules.add(new FastDisconnect());
		this.modules.add(new AntiAfk());
        this.modules.add(new ReceiveHits());
		this.modules.add(new Delay17());
        this.modules.add(new MiniGames());
	
    }
	
	public Module getModule(String name) {
		for (Module m : this.modules) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}
	
	public ArrayList<Module> getModuleList() {
		return this.modules;
	}
	
	public ArrayList<Module> getModulesInCategory(Category c) {
		ArrayList<Module> mods = new ArrayList<Module>();
		for (Module m : this.modules) {
			if (m.getCategory() == c) {
				mods.add(m);
			}
		}
		return mods;
	}
}
