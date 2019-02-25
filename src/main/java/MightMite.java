
import Commands.classcommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MightMite extends JavaPlugin implements Listener {
	public static MightMite plugin;

	Map<String,Object> userdata= new HashMap<String, Object>();
	Map<String,String> boom= new HashMap<String, String>();
	public Map<String,Integer> cooltime= new HashMap<String, Integer>();
	Map<String,ItemStack> itemremem = new HashMap<String,ItemStack>();
	Map<String,Integer> mmsec = new HashMap<String,Integer>();
	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("MightyMite").setExecutor(new classcommand());
		loadConfig();
		for(Player p : Bukkit.getOnlinePlayers()){
			LoadUserFile(p);
		}
	}

	private FileConfiguration config;
	private FileConfiguration UserData;
	private File file = new File("plugins/MightMite/TNTMap.yml");
	private void loadConfig() {
		config = YamlConfiguration.loadConfiguration(file);
		List<Location> Map=new ArrayList<>();
		List<String> UUID=new ArrayList<>();
		try {
			if (!file.exists()) {
				config.set("설치.위치",Map);
				config.set("설치.유저",UUID);

				config.save(file);
			}
			config.load(file);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public void PutMapDATA(List<Location> Map,List<String> UUID) {
		config = YamlConfiguration.loadConfiguration(file);
		try {
			//if (file.exists()) {
			config.set("설치.위치",Map);
			config.set("설치.유저",UUID);
			config.save(file);
			//}
			config.load(file);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}


	public void CreateUserFile(Player p) {
		File userfile = new File("plugins/MightMite/UserData/"+p.getUniqueId().toString()+".yml");
		UserData= YamlConfiguration.loadConfiguration(userfile);
		try {
			if (!userfile.exists()) {
				List<Object> DATA = new ArrayList<>();
				DATA.add(p.getName());
				DATA.add(0); //레벨
				DATA.add(0.0); //점수
				DATA.add(0);   //킬수
				DATA.add(0.0); //숨겨진예술 - 0일때 해금안함 이외의 값 데미지
				DATA.add(2.5); //기다림의 미학 초기 데미지
				//DATA.add(e)
				UserData.set("유저.정보", DATA);
				UserData.save(userfile);
			}
			UserData.load(userfile);
			userdata.put(p.getUniqueId().toString(), (List<Object>)UserData.get("유저.정보"));
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	public List<Object> FindPlayer(String UUID) {
		File userfile = new File("plugins/MightMite/UserData/"+UUID+".yml");
		UserData= YamlConfiguration.loadConfiguration(userfile);
		try {
			if (userfile.exists()) {
				UserData.load(userfile);
				//userdata.put(uuid, ((List<Object>)UserData.get("유저.정보")).get(0).toString());
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			//return false;
		}
		return (List<Object>) UserData.get("유저.정보");
	}


	public void LoadUserFile(Player p) {
		File Folder = new File("plugins/MightMite/UserData/");
		File userfile = new File("plugins/MightMite/UserData/"+p.getUniqueId().toString()+".yml");
		UserData= YamlConfiguration.loadConfiguration(userfile);
		try {
			if (Folder.exists()&&userfile.exists()) {

				userdata.put(p.getUniqueId().toString(),(List<Object>)UserData.get("유저.정보"));
				UserData.load(userfile);
			}

		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	public void PutData(List<Object> data) {
		String uuid = Bukkit.getPlayer(data.get(0).toString()).getUniqueId().toString();
		File userfile = new File("plugins/MightMite/UserData/"+uuid+".yml");
		UserData= YamlConfiguration.loadConfiguration(userfile);
		try {
			if (userfile.exists()) {
				UserData.set("유저.정보", data);
				UserData.save(userfile);
				UserData.load(userfile);
				//userdata.put(uuid, ((List<Object>)UserData.get("유저.정보")).get(0).toString());
			}

		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	public List<Object> GetData(Player p){
		String uuid = p.getUniqueId().toString();
		File userfile = new File("plugins/MightMite/UserData/"+uuid+".yml");
		UserData= YamlConfiguration.loadConfiguration(userfile);
		try {
			if (userfile.exists()) {
				UserData.load(userfile);
				//userdata.put(uuid, ((List<Object>)UserData.get("유저.정보")).get(0).toString());
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			//return false;
		}
		return (List<Object>) UserData.get("유저.정보");
	}



	public String FindMap(Location l,String p){
		boolean reset = false;
		String nobamager="null";
		//config= YamlConfiguration.loadConfiguration(file);
		try {
			if (file.exists()) {
				config.load(file);
				//userdata.put(uuid, ((List<Object>)UserData.get("유저.정보")).get(0).toString());
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			//return false;
		}
		List<Location> obj=(List<Location>) config.get("설치.위치");
		List<String> name =  config.getStringList("설치.유저");
		for(int i=0;i<obj.size();i++) {
			Location noboom = obj.get(i);
			if(noboom.getWorld().equals(l.getWorld())) {
				if((noboom.getX()-l.getX())<5&&(noboom.getX()-l.getX())>-5){
					if((noboom.getY()-l.getY())<5&&(noboom.getY()-l.getY())>-5){
						if((noboom.getZ()-l.getZ())<5&&(noboom.getZ()-l.getZ())>-5){
							//if(name.get(i).equalsIgnoreCase(p)) {
							nobamager = name.get(i);
							//}
							//reset = true;
						}
					}
				}
			}
		}
		//Bukkit.broadcastMessage(nobamager);
		return nobamager;
	}

	@EventHandler
	public void PlayerJoin(PlayerJoinEvent event) {
		LoadUserFile(event.getPlayer());
	}
	@EventHandler
	public void onExpl(BlockExplodeEvent e)  {
		for (Block b : e.blockList()) { //goes through all blocks involved with the explosion and does code within for loop
			final BlockState state = b.getState();

			Block bomb = b;
			Location locat = bomb.getLocation();
			List<Location> lo = (List<Location>) config.get("설치.위치");
			List<String> uui = config.getStringList("설치.유저");
			Location locat2= locat;
			locat2.setY(locat2.getY()+1);

			if ((b.getType() == Material.COBBLESTONE) || (b.getType().equals(Material.STONE))) {
				//Bukkit.broadcastMessage("asd");
				//e.setCancelled (true);
				//delay += 1; //sand and gravel generate later
				b.setType(Material.AIR);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						state.update(true, false);
					}
				}, 0);
			}
			for(int i =0;i<lo.size();i++) {
				if(lo.get(i).equals(b.getLocation())||lo.get(i).equals(locat2)) {
					//Bukkit.broadcastMessage("asasd");
					//if(uui.get(i).equalsIgnoreCase(e.getPlayer().getUniqueId().toString()))
					b.setType(Material.AIR);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							state.update(true, false);
						}
					}, 0);
				}
			}

			b.setType(Material.AIR);
			//stop item drops

			//int delay = 0; //10 second delay





		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		for (Block b : e.blockList()) { //goes through all blocks involved with the explosion and does code within for loop
			final BlockState state = b.getState();

			Block bomb = b;
			Location locat = bomb.getLocation();
			List<Location> lo = (List<Location>) config.get("설치.위치");
			List<String> uui = config.getStringList("설치.유저");
			Location locat2= locat;
			locat2.setY(locat2.getY()+1);

			if ((b.getType() == Material.COBBLESTONE) || (b.getType().equals(Material.STONE))) {
				//Bukkit.broadcastMessage("asd");
				//e.setCancelled (true);
				//delay += 1; //sand and gravel generate later
				b.setType(Material.AIR);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						state.update(true, false);
					}
				}, 0);
			}
			for(int i =0;i<lo.size();i++) {
				if(lo.get(i).equals(b.getLocation())||lo.get(i).equals(locat2)) {
					//Bukkit.broadcastMessage("asasd");
					//if(uui.get(i).equalsIgnoreCase(e.getPlayer().getUniqueId().toString()))
					b.setType(Material.AIR);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							state.update(true, false);
						}
					}, 0);
				}
			}

			b.setType(Material.AIR);
			//stop item drops

			//int delay = 0; //10 second delay





		}
	}
	@EventHandler
	public void GetItem(PlayerPickupItemEvent e) {
		ItemStack b = new ItemStack(Material.WOOL);
		if(e.getItem().getItemStack().getType()==Material.WOOL) {
			if(e.getItem().getItemStack().hasItemMeta()&&e.getItem().getItemStack().getItemMeta().hasDisplayName()&&e.getItem().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§a§l5초 후 폭발합니다. §8§l⇒ §c§l우클릭 : 던지기"))
				//e.getPlayer().getInventory().remove(b);
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void dethPlayer(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();

			if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5) {
				if(!cooltime.containsKey(p.getName()+"shoot")) {
					//p.sendMessage("쥬금");

					TNTPrimed tnt = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
					boom.put(((TNTPrimed)tnt).getUniqueId().toString(), p.getUniqueId().toString());
					Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> { boom.remove(((TNTPrimed)tnt).getUniqueId().toString()); }, 20*10);
					Bukkit.getScheduler().cancelTask(cooltime.get(p.getName()+"_task2"));
					((TNTPrimed)tnt).setFuseTicks(20*5-mmsec.get(p.getName()));

					cooltime.put((p.getName()),4);
				}
				cooltime.remove(p.getName()+"shoot");
				itemremem.remove(p.getName());
			}


		/*if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) {
        	p.setItemInHand(itemremem.get(p.getName()));
		}*/
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {


		if(e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
			/*if(e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				if(p.getEquipment().getBoots()!=null&&p.getEquipment().getBoots().hasItemMeta()) {
					if(p.getEquipment().getBoots().getItemMeta().hasDisplayName()) {
						if(p.getEquipment().getBoots().getItemMeta().getDisplayName().equalsIgnoreCase("§6[ 마이티마이트 ]")) {
							e.setDamage(10);
						} else
							e.setDamage(40);
					} else e.setDamage(40);
				} else e.setDamage(40);
			}

		}
		Bukkit.broadcastMessage(e.getDamager().getUniqueId().toString());
	}*/
			if(boom.containsKey(e.getDamager().getUniqueId().toString())) {
				if(e.getEntity() instanceof Player) {
					Player p = (Player) e.getEntity();
					if(p.getUniqueId().toString().equalsIgnoreCase(boom.get(e.getDamager().getUniqueId().toString()))) {
						List<Object> mighty = FindPlayer(e.getEntity().getUniqueId().toString());
						Player mightyp=Bukkit.getPlayer((String)mighty.get(0));
						Double damage = (Double)mighty.get(4);
						e.setDamage(damage/2);
						//e.setDamage(0);
						//e.setCancelled(true);
						return;
					}
					else {
						List<Object>mighty = FindPlayer(boom.get(e.getDamager().getUniqueId().toString()));
						String Damager = (String) mighty.get(0);
						Double damage = (Double)mighty.get(5);
						e.setDamage(damage);
						Double Grade = damage / 10.0;
						if(Bukkit.getPlayer(Damager)!=null) {
							Player mightyp = Bukkit.getPlayer(Damager);
							//mightyp.sendMessage(p.getName()+"님이 [기다림의 미학]으로 인해 "+Double.toString(damage)+"의 피해를 입었습니다! [경험치 : +"+Double.toString(Grade)+"]");
							mightyp.sendMessage("§4"+p.getName()+"§f님이 §2[기다림의 미학]§f으로 인해 §9"+Double.toString(damage)+"§f의 피해를 입었습니다! §6[경험치 : +"+Double.toString(Grade)+"]");
						}
						p.sendMessage("§6§l[§6마이티마이트§6§l] §4"+Damager+"§f의 §2[기다림의 미학]§f에 의해 §4"+Double.toString(damage)+"§f의 피해를 입었습니다!");

						//p.sendMessage("[마이티마이트] "+Damager+"의 [기다림의 미학]에 의해 "+Double.toString(damage)+"의 피해를 입었습니다!");
						Grade+=(Double)mighty.get(2);
						mighty.set(2, Grade);
						PutData(mighty);
					}
				}
			}

		}
	}

	@EventHandler//(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageByBlockEvent event) {
		//Player p=(Player)event.getEntity();

		if(event.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {

			if(event.getEntity() instanceof Player) {
				if(!FindMap(event.getEntity().getLocation(),event.getEntity().getUniqueId().toString()).equalsIgnoreCase("null")) {
					if(FindMap(event.getEntity().getLocation(),event.getEntity().getUniqueId().toString()).equalsIgnoreCase(event.getEntity().getUniqueId().toString())) {
						//event.setCancelled(true);
						List<Object> mighty = FindPlayer(event.getEntity().getUniqueId().toString());
						Player mightyp=Bukkit.getPlayer((String)mighty.get(0));
						Double damage = (Double)mighty.get(4);
						event.setDamage(damage/2);
						return;
					}
				}
				//String uuid = boom.get(((Player)event.getEntity()).getName());

				Player p = (Player) event.getEntity();

				List<Object> mighty = FindPlayer(FindMap(event.getEntity().getLocation(),event.getEntity().getUniqueId().toString()));
				Player mightyp=Bukkit.getPlayer((String)mighty.get(0));
				Double damage = (Double)mighty.get(4);
				//event.setCancelled(true);
				event.setDamage(damage);
				Double Grade = damage / 10.0;

				p.sendMessage("§6§l[§6마이티마이트§6§l] §4"+(String)mighty.get(0)+"§f의 §2[숨겨진 예술]§f에 의해 §4"+Double.toString(damage)+"§f의 피해를 입었습니다!");
				mightyp.sendMessage("§4"+p.getName()+"§f님이 §2[숨겨진 예술]§f으로 인해 §9"+Double.toString(damage)+"§f의 피해를 입었습니다! §6[경험치 : +"+Double.toString(Grade)+"]");
				Grade+=(Double)mighty.get(2);
				mighty.set(2, Grade);
				//Bukkit.broadcastMessage(Double.toString(Grade));
				PutData(mighty);
				boom.remove(((Player)event.getEntity()).getName());
				if(boom.containsKey(mightyp.getName()+"_nodamage")) {
					boom.remove(mightyp.getName()+"_nodamage");

				}
				boom.remove(event.getEntity().getName()+"_nodamage");
			}
		}
	}


        	/*if(event.getEntity() instanceof Player) {
        		if(boom.containsKey(event.getEntity().getName()+"_nodamage")) {

        			event.setCancelled(true);

        			return;
        		} else {
        		String uuid = boom.get(((Player)event.getEntity()).getName());

        		Player p = (Player) event.getEntity();

        		List<Object> mighty = FindPlayer(uuid);
        		Player mightyp=Bukkit.getPlayer((String)mighty.get(0));
        		Double damage = (Double)mighty.get(4);
        		//event.setCancelled(true);
        		event.setDamage(damage);
        		Double Grade = damage / 5.0;

        		p.sendMessage("[마이티마이트] "+(String)mighty.get(0)+"의 [숨겨진 예술]에 의해 "+Double.toString(damage)+"의 피해를 입었습니다!");
        		mightyp.sendMessage(p.getName()+"님이 당신의 폭탄으로 인해 "+Double.toString(damage)+"의 피해를 입었습니다! [경험치 : +"+Double.toString(Grade)+"]");
        		Grade+=(Double)mighty.get(2);
        		mighty.set(2, Grade);
        		//Bukkit.broadcastMessage(Double.toString(Grade));
        		PutData(mighty);
        		boom.remove(((Player)event.getEntity()).getName());
        		if(boom.containsKey(mightyp.getName()+"_nodamage")) {
        		boom.remove(mightyp.getName()+"_nodamage");
        		}
        	}
        		boom.remove(event.getEntity().getName()+"_nodamage");
        	}
        	}
            /*if(event.getDamager()==null)
                p.setNoDamageTicks(10);
            	event.getEntity().getLastDamageCause().setCancelled(true);
        }}*/



	@EventHandler
	public void PlayerJoin(PlayerMoveEvent event) {
		List<Location> l = (List<Location>) config.get("설치.위치");
		List<String> uuid = config.getStringList("설치.유저");
		Player p = event.getPlayer();
		Block b = event.getPlayer().getLocation().getBlock();

		//int bomnum=-1;
		if(b.getType().equals(Material.CARPET)) {
			for(int i=0;i<l.size();i++) {
				if(l.get(i).equals(b.getLocation())&&!(uuid.get(i).equalsIgnoreCase(p.getUniqueId().toString()))) {
					//p.sendMessage("펑");
					//p.getWorld().createExplosion(b.getLocation(), 7, false,false);
					boom.put(p.getName(), uuid.get(i));
					String Name = (String) FindPlayer(uuid.get(i)).get(0);
					boom.put(Name+"_nodamage", "true");
					p.getWorld().createExplosion(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 3.0F, false, true);
					//p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.ENTITY_EXPLOSION,5000));
					//createExplosion(p.getLocation());
					//p.getWorld().createExplosion(b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ(), 13, false, true);
					//p.getPlayer().damage(5);
					//bomnum=i;
					//List<Object> userdd=FindPlayer(uuid.get(i));
					//String target = (String)userdd.get(0);


					uuid.remove(i);
					l.remove(i);
					PutMapDATA(l,uuid);
					//if(Bukkit.getPlayer(target)!=null){
					//Bukkit.getPlayer(target).sendMessage(p.getName()+"이 당신의 폭탄에 피해를 입었습니다.");
					//}
				}
			}
			//if(bomnum!=-1) {

			//}

		}
	}
	@EventHandler
	public void anotherEvent(PlayerChatEvent e) {
		Player p = e.getPlayer();
		String name = p.getName();
		if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void anotherEvent(PlayerInteractEvent e) { Player p = e.getPlayer(); if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) { e.setCancelled(true); }}
	@EventHandler
	public void anotherEvent(PlayerDropItemEvent e) { Player p = e.getPlayer(); if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) { e.setCancelled(true); }}
	@EventHandler
	public void anotherEvent(PlayerTeleportEvent e) { Player p = e.getPlayer(); if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) { e.setCancelled(true); }}
	@EventHandler
	public void anotherEvent2(PlayerSwapHandItemsEvent e) { Player p = e.getPlayer(); if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) { e.setCancelled(true); }}
	@EventHandler
	public void anotherEvent2(InventoryClickEvent e) { Player p = (Player) e.getWhoClicked(); if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) { e.setCancelled(true); }}
	@EventHandler
	public void anotherEvent(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void anotherEvent3(PlayerBedEnterEvent e) {
		Player p = e.getPlayer();
		if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5&&!cooltime.containsKey(p.getName()+"shoot")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void BlockBreak(BlockBreakEvent e) {
		Block bomb = e.getBlock();
		Location locat = bomb.getLocation();
		List<Location> lo = (List<Location>) config.get("설치.위치");
		List<String> uui = config.getStringList("설치.유저");
		Location locat2= locat;
		locat2.setY(locat2.getY()+1);
		for(int i =0;i<lo.size();i++) {
			if(lo.get(i).equals(e.getBlock().getLocation())||lo.get(i).equals(locat2)) {
				//e.getPlayer().sendMessage(e.getPlayer().getName()+uui.get(i));
				//if(uui.get(i).equalsIgnoreCase(e.getPlayer().getUniqueId().toString()))
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void ClickEvent(PlayerInteractEvent e) {
		Action a = e.getAction();
		Player p = e.getPlayer();
		World world = e.getPlayer().getWorld();
		Material m = p.getItemInHand().getType();
		//if(a.equals(Action.RIGHT_CLICK_AIR)||a.equals(Action.RIGHT_CLICK_BLOCK)) {
		//ItemStack item = new ItemStack(Material.AIR);
		//if(p.getItemInHand().getTypeId()==0) {

		if(((a==Action.RIGHT_CLICK_AIR||a.equals(Action.RIGHT_CLICK_BLOCK))&&m.getId()==35&&p.isSneaking())||(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5)&&m.getId()==35&&(a==Action.RIGHT_CLICK_AIR||a.equals(Action.RIGHT_CLICK_BLOCK))&&!(cooltime.containsKey(p.getName()+"shoot"))) {



			if(cooltime.containsKey(p.getName()+"_task")) {
				if (cooltime.get(p.getName())>5&&!(cooltime.containsKey(p.getName()+"shoot"))) {
					e.setCancelled(true);
					p.setItemInHand(itemremem.get(p.getName()));
					itemremem.remove(p.getName());
					int v = 1;
					if(cooltime.containsKey(p.getName())&&cooltime.get(p.getName())>5) {
						final Vector direction = p.getEyeLocation().getDirection().multiply(v);
						TNTPrimed tnt = p.getWorld().spawn(p.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), TNTPrimed.class);
						tnt.setVelocity(direction);
						boom.put(((TNTPrimed)tnt).getUniqueId().toString(), p.getUniqueId().toString());
						Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> { boom.remove(((TNTPrimed)tnt).getUniqueId().toString()); }, 20*10);
						//((TNTPrimed)tnt).setFuseTicks((cooltime.get(p.getName())-5)*20);
						Bukkit.getScheduler().cancelTask(cooltime.get(p.getName()+"_task2"));
						((TNTPrimed)tnt).setFuseTicks(20*5-mmsec.get(p.getName()));
						cooltime.put(p.getName()+"shoot", 1);
						//mmsec.remove(p.getName());

						return;
					}
				} else {
					e.setCancelled(true);
					Integer coolti=cooltime.get(p.getName());
					//				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+p.getName()+" title "+Integer.toString(coolti));
//					Bukkit.getConsoleSender(/title Kuoo_ title 1
					p.sendMessage("§6§l[§a쿨타임 §7"+Integer.toString(coolti)+"§6§l]");
					return;
				}
			}
			//p.sendMessage("asd");
			Block b = e.getClickedBlock();
			//Location l = b.getLocation();
			int powder_cnt=0;
			ItemStack powder = new ItemStack(Material.SULPHUR);
			//p.getInventory().setItemInHand(powder);
			//폰탄 들고있기 조건문
			if(e.getPlayer().getInventory().containsAtLeast(powder, 1)&&userdata.containsKey(p.getUniqueId().toString())) {
				e.setCancelled(true);
				p.getInventory().removeItem(powder);
				for (int i=0;i<p.getInventory().getMaxStackSize();i++) {
					if(p.getInventory().getItem(i)==null) {
						;
					}
					else if (p.getInventory().getItem(i).getTypeId()==289) {
						powder_cnt+=p.getInventory().getItem(i).getAmount();
					}
				}
				p.sendMessage("§8§l⇔ §6남은 화약 [§f"+Integer.toString(powder_cnt)+"§6]");
				//p.launchProjectile(TNT.class);
				//TNTPrimed tnt = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
	               /* int loop = 1;
	                while (loop> 0)
	                { */
				//Entity tnt = p.getWorld () .spawn (p.getTargetBlock (null, 50) .getLocation (). add (0, 1, 0), TNTPrimed.class);
				//((TNTPrimed) tnt) .setFuseTicks (20);



				final Player nep = p;

				itemremem.put(p.getName(), p.getItemInHand());
				ItemStack item = new MaterialData(35, (byte)14).toItemStack(5);
				ItemMeta itemdi = item.getItemMeta();
				itemdi.setDisplayName("§a§l5초 후 폭발합니다. §8§l⇒ §c§l우클릭 : 던지기");
				item.setItemMeta(itemdi);
				p.setItemInHand(item);

				cooltime.put(p.getName(), 11);
				mmsec.put(p.getName(), 0);
				cooltime.put(p.getName()+"_task2",Bukkit.getScheduler().runTaskTimer(plugin, () -> {
					Integer count = mmsec.get(nep.getName());
					count++;
					//Bukkit.broadcastMessage(Integer.toString(count));
					mmsec.put(nep.getName(),count);
				},1,1).getTaskId());
				cooltime.put(p.getName()+"_task",Bukkit.getScheduler().runTaskTimer(plugin, () -> {
					Integer cool = cooltime.get(nep.getName());
					String Name = nep.getName();
					cooltime.put(Name, cool-1);

					if(cooltime.get(nep.getName())>5&&cooltime.get(nep.getName())<11&&!cooltime.containsKey(nep.getName()+"shoot")) {
						ItemStack playercoolview = nep.getItemInHand();
						String floatnum = Integer.toString(cooltime.get(nep.getName()))+"f";
						nep.playSound(nep.getLocation(), Sound.BLOCK_NOTE_HAT, Float.parseFloat(floatnum), Float.parseFloat(floatnum));
						playercoolview.setAmount(cooltime.get(nep.getName())-5);
						nep.setItemInHand(playercoolview);
					}
					if(cooltime.get(nep.getName())==5) {
						if(!cooltime.containsKey(nep.getName()+"shoot")) {
							nep.setItemInHand(itemremem.get(nep.getName()));
							TNTPrimed tnt = nep.getWorld().spawn(nep.getLocation(), TNTPrimed.class);
							boom.put(((TNTPrimed)tnt).getUniqueId().toString(), nep.getUniqueId().toString());
							Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> { boom.remove(((TNTPrimed)tnt).getUniqueId().toString()); }, 20*10);
							((TNTPrimed)tnt).setFuseTicks(0);
							Bukkit.getScheduler().cancelTask(cooltime.get(nep.getName()+"_task2"));

						}

						cooltime.remove(nep.getName()+"shoot");
						itemremem.remove(nep.getName());
					}
					if(cooltime.containsKey(nep.getName()+"isOP")&&cooltime.containsKey(nep.getName()+"shoot")) {
						cooltime.put(nep.getName(), 0);
						cooltime.remove(nep.getName()+"shoot");
						itemremem.remove(nep.getName());
					}
					if(cooltime.get(nep.getName())==0) {
						nep.sendMessage("§6§l[§6스킬§6§l] §2기다림의 미학§f 준비완료!");
						Bukkit.getScheduler().cancelTask(cooltime.get(Name+"_task"));
						cooltime.remove(Name+"_task");
					}

				},0, 20).getTaskId());



				//p.sendMessage(p + "Boom!");
	                  /*  loop--;
	                } */

				//}

			}
		}

		if(a.equals(Action.LEFT_CLICK_BLOCK)||a.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block bomb = e.getClickedBlock();
			Location locat = bomb.getLocation();
			List<Location> lo = (List<Location>) config.get("설치.위치");
			List<String> uui = config.getStringList("설치.유저");
			//String nametmp;
			//int bomnum=-1;
			/*for(int i=0;i<lo.size();i++) {
				if(locat.equals(lo.get(i))&&!uui.get(i).equalsIgnoreCase(p.getUniqueId().toString())){
					//List<Object> mighty = GetData(p);
					p.sendMessage("펑");
					p.getWorld().createExplosion(bomb.getLocation().getX(), bomb.getLocation().getY(), bomb.getLocation().getZ(), 3F, false, true);
					//nametmp = uui.get(i);
					List<Object> mighty=FindPlayer(uui.get(i));
					String target = (String)mighty.get(0);
					if(Bukkit.getPlayer(target)!=null){
						Bukkit.getPlayer(target).sendMessage(p.getName()+"이 당신의 폭탄에 피해를 입었습니다.");
					}
					bomnum=i;
				}
			}*/
			Location lo2=bomb.getLocation();
			lo2.setY(lo2.getY()+1);
			for(int i=0;i<lo.size();i++) {
				if((lo.get(i).equals(lo2)||lo.get(i).equals(bomb.getLocation()))&&!(uui.get(i).equalsIgnoreCase(p.getUniqueId().toString()))) {
					p = e.getPlayer();
					boom.put(p.getName(), uui.get(i));
					p.getWorld().createExplosion(bomb.getLocation().getX(), bomb.getLocation().getY(), bomb.getLocation().getZ(), 3.0F, false, true);
					//p.getPlayer().damage(5);

					uui.remove(i);
					lo.remove(i);
					PutMapDATA(lo,uui);
				}
			}

		}
		ItemStack item = new ItemStack(Material.AIR);
		if(a.equals(Action.LEFT_CLICK_BLOCK)&&m.getId()==0&&p.isSneaking()) {
			Material click = e.getClickedBlock().getType();
			Block b = e.getClickedBlock();
			Location l = b.getLocation();
			int powder_cnt=0;
			ItemStack powder = new ItemStack(Material.SULPHUR);

			l.setY(l.getY()+1);
			ItemStack redwool = new ItemStack(Material.CARPET, 1, DyeColor.RED.getDyeData());



			//폭탄 설치 조건문
			if((click.getId()==2||click.getId()==3||click.getId()==13)&&l.getBlock().getType().getId()==0&&e.getPlayer().getInventory().containsAtLeast(powder, 1)&&userdata.containsKey(p.getUniqueId().toString())) {
				List<Location> mapsave = (List<Location>) config.get("설치.위치");
				List<String> usersave = (List<String>) config.get("설치.유저");
				Boolean reset = false;
				/*for(int i=0;i<mapsave.size();i++) {
					Location noboom = mapsave.get(i);
					if(noboom.getWorld().equals(l.getWorld())) {
					if((noboom.getX()-l.getX())<5&&(noboom.getX()-l.getX())>-5){

							if((noboom.getZ()-l.getZ())<5&&(noboom.getZ()-l.getZ())>-5){
								if((noboom.getY()-l.getY())<5&&(noboom.getY()-l.getY())>-5){

								reset = true;
							}
						}
					}
					}
					if((noboom.getY()-l.getY())>5&&(noboom.getY()-l.getY())<-5){
						reset = false;
					}
				}
				if(reset) {
					p.sendMessage("§a§l[주의] §6주변에 이미 폭탄이 있습니다. §f5*5§6칸 이상 떨어져서 설치하세요!");
					e.setCancelled(true);
				}*/
				if (!reset) {
					List<Object> mighty=GetData(p);
					Double damage = (Double)mighty.get(4);
					if(damage!=0) {
						e.setCancelled(true);
						p.getInventory().removeItem(powder);
						for (int i=0;i<p.getInventory().getMaxStackSize();i++) {
							if(p.getInventory().getItem(i)==null) {
								;
							}
							else if (p.getInventory().getItem(i).getTypeId()==289) {
								powder_cnt+=p.getInventory().getItem(i).getAmount();
							}
						}
						p.sendMessage("§8§l⇔ §6남은 화약 [§f"+Integer.toString(powder_cnt)+"§6]");

						Bukkit.getWorld(world.getName()).getBlockAt(l).setType(Material.CARPET);
						Bukkit.getWorld(world.getName()).getBlockAt(l).setData((byte)13);


						mapsave.add(l);
						usersave.add(p.getUniqueId().toString());
						PutMapDATA(mapsave,usersave);
					}
				} else if((click.getId()==2||click.getId()==3||click.getId()==13)&&l.getBlock().getType().getId()==0&&!(e.getPlayer().getInventory().containsAtLeast(powder, 1))&&userdata.containsKey(p.getUniqueId().toString())) {

					p.sendMessage("§a§l[주의] §4화약이 부족합니다");

				}
				List<Object> mighty=GetData(p);
				Double damage = (Double)mighty.get(4);
				if(damage!=0) {
					e.setCancelled(true);

				}
				//return;
			}
		}
	}
	private Double valueof(Object asda) {
		// TODO Auto-generated method stub
		return null;
	}
}
