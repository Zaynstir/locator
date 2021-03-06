package io.github.zaynstir.locator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Locator extends JavaPlugin implements Listener{
    boolean locate = true; //default: true
    double errorPercentage = 0.0; //default 0
    List<Player> deserters = new ArrayList<Player>();
    Location setLocation = new Location(null, 0, 0, 0); //default (0,0,0)
    double desertDist = 100.0; //default 100.0

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("locator")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("ERROR: Only Players can use this command.");
                return true;
            }
            Player p = (Player) sender;
            if(p.hasPermission("locator.use")) {
                if(args.length == 0) {
                    p.sendMessage(ChatColor.DARK_GREEN + "Locator is currently " + (locate ? "on." : "off."));
                    p.sendMessage(ChatColor.DARK_RED + "Usage: /locator [on/off/getSD/getMSD/addPlayer/removePlayer/errorPercent/setLoc/setRadius/deserters/help]");
                    return true;
                }
                else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("on")) {
                        p.sendMessage(ChatColor.DARK_GREEN + "Locator has been turned on");
                        locate = true;
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("off")) {
                        p.sendMessage(ChatColor.YELLOW + "Locator has been turned off");
                        locate = false;
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("getSD")) {
                        if(locate) {
                            if(p.getInventory().firstEmpty() == -1) {
                                p.sendMessage(ChatColor.DARK_RED + "Please empty an inventory slot");
                                return true;
                            }
                            else {
                                p.getInventory().addItem(getLocator());
                                p.sendMessage(ChatColor.YELLOW + "You have received the Scouting Device");
                                return true;
                            }
                        }
                    }
                    else if(args[0].equalsIgnoreCase("getMSD")) {
                        if(locate) {
                            if(p.getInventory().firstEmpty() == -1) {
                                p.sendMessage(ChatColor.DARK_RED + "Please empty an inventory slot");
                                return true;
                            }
                            else {
                                p.getInventory().addItem(getMegaLocator());
                                p.sendMessage(ChatColor.YELLOW + "You have received the Major Scouting Device");
                                return true;
                            }
                        }
                    }
                    else if(args[0].equalsIgnoreCase("addPlayer")) {
                        p.sendMessage(ChatColor.DARK_RED + "Usage: /locator addPlayer [playername]");
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("removePlayer")) {
                        p.sendMessage(ChatColor.DARK_RED + "Usage: /locator removePlayer [playername]");
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("errorPercent")) {
                        p.sendMessage(ChatColor.DARK_RED + "Usage: /locator errorPercent [percentage]");
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("setRadius")) {
                        p.sendMessage(ChatColor.DARK_RED + "Usage: /locator setRadius [num]");
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("setLoc")) {
                        setLocation = p.getLocation();
                        p.sendMessage(ChatColor.DARK_GREEN + "Location has been set");
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("deserters")) {
                        String str = "";
                        for(int i = 0; i < deserters.size(); i++){
                            str += deserters.get(i).getDisplayName() + (i == deserters.size()-1 ? "" : ", ");
                        }
                        p.sendMessage(str);
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("help")) {
                        p.sendMessage(ChatColor.DARK_GREEN + "Usage: /locator [on/off/getSD/getMSD/addPlayer/removePlayer/errorPercent/setLoc/setRadius/deserters/help]");
                        p.sendMessage(ChatColor.GREEN + "/locator on - Allows for locator to run (On by default).");
                        p.sendMessage(ChatColor.DARK_GREEN + "/locator off - Disallows for locator to run.");
                        p.sendMessage(ChatColor.GREEN + "/locator getSD - Player recieves the \'Scouting Device\'.");
                        p.sendMessage(ChatColor.DARK_GREEN + "/locator getMSD - Player recieves the \'Major Scouting Device\'.");
                        p.sendMessage(ChatColor.GREEN + "/locator addPlayer [playername] - Manually adds a player to the deserters list.");
                        p.sendMessage(ChatColor.DARK_GREEN + "/locator removePlayer [playername] - Manually removes a player to the deserters list.");
                        p.sendMessage(ChatColor.GREEN + "/locator errorPercent [0.0 - 100.0] - Allows for the compass to not work all the time (0% error by Default).");
                        p.sendMessage(ChatColor.DARK_GREEN + "/locator setLoc - Sets the location to command issuer's postion to see who deserts ((0,0,0) by Default).");
                        p.sendMessage(ChatColor.GREEN + "/locator setRadius [# of Blocks] - Sets the radius around \'setLoc\' before a player is considered a deserter (100 Blocks by Default).");
                        p.sendMessage(ChatColor.DARK_GREEN + "/locator deserters - Lists all current deserters.");
                        p.sendMessage(ChatColor.GREEN + "/locator help - Do I need to tell you what this does?.");
                        return true;
                    }
                    else {
                        p.sendMessage(ChatColor.DARK_RED + "Usage: /locator [on/off/getSD/getMSD/addPlayer/removePlayer/errorPercent/setLoc/setRadius/deserters/help]");
                        return true;
                    }
                }
                else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("setRadius")) {
                        try {
                            double amt = Integer.parseInt(args[1]);
                            desertDist = amt;
                            p.sendMessage(ChatColor.GREEN + "Deserter Distance has been updated to " + desertDist + " blocks.");
                        }catch(Exception e) {
                            p.sendMessage(ChatColor.DARK_RED + "ERROR: Input may not be a number. Usage: /locator setRadius [whole number]");
                        }
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("addPlayer")) {
                        try {
                            Player deserter = Bukkit.getPlayer(args[1]);
                            if(deserter == null) {
                                p.sendMessage(ChatColor.DARK_RED + "ERROR: Player may not exist. Usage: /locator addPlayer [playername]");
                            }
                            else if(deserters.contains(deserter)) {
                                p.sendMessage(ChatColor.RED + "Player " + deserter.getName() + ", is already on the deserter's list.");
                            }
                            else {
                                deserters.add(deserter);
                                p.sendMessage(ChatColor.GREEN + "Player [" + deserter.getName() + "] has been added to the deserters list.");
                            }
                        }catch(Exception e) {
                            p.sendMessage(ChatColor.DARK_RED + "ERROR: Player may not exist. Usage: /locator addPlayer [playername]");
                        }
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("removePlayer")) {
                        try {
                            Player deserter = Bukkit.getPlayer(args[1]);
                            if(deserter == null) {
                                p.sendMessage(ChatColor.DARK_RED + "ERROR: Player may not exist. Usage: /locator removePlayer [playername]");
                            }
                            else if(!deserters.contains(deserter)) {
                                p.sendMessage(ChatColor.RED + "Player " + deserter.getName() + ", does not exist on the deserter's list.");
                            }
                            else {
                                deserters.add(deserter);
                                p.sendMessage(ChatColor.GREEN + "Player [" + deserter.getName() + "] has been removed to the deserters list.");
                            }
                        }catch(Exception e) {
                            p.sendMessage(ChatColor.DARK_RED + "ERROR: Player may not exist. Usage: /locator removePlayer [playername]");
                        }
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("errorPer")) {
                        try {
                            double perc = Double.parseDouble(args[1]);
                            errorPercentage = perc;
                            p.sendMessage(ChatColor.GREEN + "Error Percentage has been updated to " + errorPercentage + "%.");
                        }catch(Exception e) {
                            p.sendMessage(ChatColor.DARK_RED + "ERROR: Input may not be a number. Usage: /locator errorPercent [0.0-100.0]");
                        }
                        return true;
                    }
                }
                else if(args.length > 2) {
                    p.sendMessage(ChatColor.DARK_RED + "Usage: /locator [on/off/getSD/getMSD/addPlayer/removePlayer/errorPercent/setLoc/setRadius/deserters/help]");
                    return true;
                }
            }
            else {
                p.sendMessage(ChatColor.DARK_RED + "ERROR: Only OP'd players can use this command.");
                return true;
            }
        }


        return false;
    }

    public ItemStack getLocator() {
        ItemStack locator = new ItemStack(Material.COMPASS);
        ItemMeta meta = locator.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Scouting Device");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add(ChatColor.GOLD + "" +ChatColor.ITALIC + "Right-click to see the general direction of the deserters. Range: 250");
        meta.setLore(lore);
        locator.setItemMeta(meta);

        return locator;
    }

    public ItemStack getMegaLocator() {
        ItemStack locator = new ItemStack(Material.COMPASS);
        ItemMeta meta = locator.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Major Scouting Device");
        List<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add(ChatColor.GOLD + "" +ChatColor.ITALIC + "Right-click to see the general direction of the deserters. Range: 1000");
        meta.setLore(lore);
        locator.setItemMeta(meta);

        return locator;
    }

    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent e) {
        if(locate) {
            Player p = (Player) e.getPlayer();
            Location loc = p.getLocation();
            int dist = (int) Math.ceil(Math.sqrt(Math.pow(setLocation.getZ() - loc.getZ(),2) + Math.pow(setLocation.getX() - loc.getX(),2)));
            //Bukkit.broadcastMessage(""+dist + " - " + desertDist);
            if(dist > desertDist) {
                if(!deserters.contains(p)) {
                    deserters.add(p);
                    p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING: You are deserting.");
                }
            }
            else if(dist <= desertDist) {
                if(deserters.contains(p)){
                    p.sendMessage(ChatColor.DARK_GREEN + "Welcome back to paradise.");
                    deserters.remove(p);
                }
            }
        }
    }

    @EventHandler()
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if(locate) {
            if(e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) {
                if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Scouting Device") && !(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Major Scouting Device"))) {
                    if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                        Player p = (Player) e.getPlayer();
                        p.getInventory().removeItem(getLocator());
                        Integer[] cardinals = {0,0,0,0,0,0,0,0};
                        Location loc = p.getLocation();
                        boolean flag = false;
                        for(Player deserter: deserters) {
                            if(deserter != p) {
                                Location dloc = deserter.getLocation();
                                int d = (int) Math.ceil(Math.sqrt(Math.pow(dloc.getZ() - loc.getZ(),2) + Math.pow(dloc.getY() - loc.getY(),2) + Math.pow(dloc.getX() - loc.getX(),2)));
                                if(d <= 250) {
                                    flag = true;
                                    int dist = (int) Math.ceil(Math.sqrt(Math.pow(dloc.getZ() - loc.getZ(),2) + Math.pow(dloc.getX() - loc.getX(),2)));
                                    deserter.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "You feel as though you are being watched.");
                                    Random r = new Random();
                                    if(r.nextDouble()*100 < errorPercentage) {
                                        cardinals[r.nextInt(8)] += 1;
                                    }
                                    else {
                                        Location opploc = loc.add(0,0,-250);
                                        int opp = (int) Math.ceil(Math.sqrt(Math.pow(dloc.getZ() - opploc.getZ(),2) + Math.pow(dloc.getX() - opploc.getX(),2)));
                                        double angle = Math.toDegrees(Math.acos((Math.pow(250, 2) + Math.pow(dist, 2) - Math.pow(opp, 2))/(2 * 250 * dist)));
                                        double result = Math.floor(angle/22.5);
                                        cardinals[(int) Math.ceil(result/2)] += 1;
                                    }
                                }
                            }
                        }
                        if(!flag) {
                            p.sendMessage(ChatColor.RED + "No players found within 250 blocks.");
                        }
                        else {
                            for(int i = 0; i < 8; i++) {
                                if(cardinals[i] != 0) {
                                    p.sendMessage(ChatColor.DARK_GREEN + "You detect " + cardinals[i] + " people " + idxToCardinal(i) + " from here.");
                                }
                            }
                        }
                    }
                }
                else if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Major Scouting Device")) {
                    if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                        Player p = (Player) e.getPlayer();
                        //p.getInventory().removeItem(getLocator());
                        Integer[] cardinals = {0,0,0,0,0,0,0,0};
                        Location loc = p.getLocation();
                        boolean flag = false;
                        for(Player deserter: deserters) {
                            if(deserter != p) {
                                Location dloc = deserter.getLocation();
                                int d = (int) Math.ceil(Math.sqrt(Math.pow(dloc.getZ() - loc.getZ(),2) + Math.pow(dloc.getY() - loc.getY(),2) + Math.pow(dloc.getX() - loc.getX(),2)));
                                if(d <= 1000) {
                                    flag = true;
                                    int dist = (int) Math.ceil(Math.sqrt(Math.pow(dloc.getZ() - loc.getZ(),2) + Math.pow(dloc.getX() - loc.getX(),2)));
                                    deserter.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "You feel as though you are being watched.");
                                    Random r = new Random();
                                    if(r.nextDouble()*100 < errorPercentage) {
                                        cardinals[r.nextInt(8)] += 1;
                                    }
                                    else {
                                        Location opploc = loc.add(0,0,-250);
                                        int opp = (int) Math.ceil(Math.sqrt(Math.pow(dloc.getZ() - opploc.getZ(),2) + Math.pow(dloc.getX() - opploc.getX(),2)));
                                        double angle = Math.toDegrees(Math.acos((Math.pow(250, 2) + Math.pow(dist, 2) - Math.pow(opp, 2))/(2 * 250 * dist)));
                                        double result = Math.floor(angle/22.5);
                                        cardinals[(int) Math.ceil(result/2)] += 1;
                                    }
                                }
                            }
                        }
                        if(!flag) {
                            p.sendMessage(ChatColor.RED + "No players found within 1000 blocks.");
                        }
                        else {
                            for(int i = 0; i < 8; i++) {
                                if(cardinals[i] != 0) {
                                    p.sendMessage(ChatColor.DARK_GREEN + "You detect " + cardinals[i] + " people " + idxToCardinal(i) + " from here.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String idxToCardinal(int idx) {
        switch(idx) {
            case 0:
                return "North";
            case 1:
                return "North-East";
            case 2:
                return "East";
            case 3:
                return "South-East";
            case 4:
                return "South";
            case 5:
                return "South-West";
            case 6:
                return "West";
            case 7:
                return "North-West";
        }
        return null;
    }
}
