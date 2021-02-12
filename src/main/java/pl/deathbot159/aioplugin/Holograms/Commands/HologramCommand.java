package pl.deathbot159.aioplugin.Holograms.Commands;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.deathbot159.aioplugin.Holograms.Hologram;
import pl.deathbot159.aioplugin.Holograms.Types.HologramUpdateType;

import java.util.*;

import static pl.deathbot159.aioplugin.Main.getHologramManager;
import static pl.deathbot159.aioplugin.Main.getLoggerManager;

public class HologramCommand implements TabExecutor {
    //================================================================================
    // Variables
    //================================================================================
    private List<String> subCommands = Arrays.asList("create", "delete", "list", "update_line", "delete_line", "insert_line", "replace_line", "update_location");

    //================================================================================
    // TabCompleter
    //================================================================================
    @NotNull
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("hologram")){
            if(args.length == 1){
                return args[0].equals("")?subCommands:getSubCommandsByCharsContain(args[0]);
            }else if(args.length >= 2){
                return args[0].equalsIgnoreCase(subCommands.get(0))
                        ? Collections.singletonList("<hologram_content_here>"):
                        (args[0].equalsIgnoreCase(subCommands.get(1))
                                ?(args.length == 2?(getHoloIDs()!=null?getHoloIDs():Collections.singletonList("<no_holograms_available!>")):Collections.singletonList("<too_much_arguments!>")):
                                (args[0].equalsIgnoreCase(subCommands.get(2))) ?Collections.singletonList("<no_arguments_needed!>"):
                                        (args[0].equalsIgnoreCase(subCommands.get(3))) ?((args.length==2)?(getHoloIDs()!=null?getHoloIDs():Collections.singletonList("<no_holograms_available!>")):
                                                (args.length==3)?(!args[1].equals("") ?(getLines(Integer.parseInt(args[1]))!=null?getLines(Integer.parseInt(args[1])):Collections.singletonList("<invalid_holo_id_provided!>")):Collections.singletonList("<invalid_holo_id_provided!>")):
                                                        Collections.singletonList("<hologram_content_here>")):
                                                (args[0].equalsIgnoreCase(subCommands.get(4)))
                                                        ?((args.length==2)?(getHoloIDs()!=null?getHoloIDs():Collections.singletonList("<no_holograms_available!>")):
                                                        ((args.length==3)?(!args[1].equals("") ?(getLines(Integer.parseInt(args[1]))!=null?getLines(Integer.parseInt(args[1])):Collections.singletonList("<invalid_holo_id_provided!>")):Collections.singletonList("<invalid_holo_id_provided!>")):
                                                                Collections.singletonList("<too_much_arguments!>"))):
                                                        (args[0].equalsIgnoreCase(subCommands.get(5)))
                                                                ?((args.length==2)?(getHoloIDs()!=null?getHoloIDs():Collections.singletonList("<no_holograms_available!>")):
                                                                (!args[1].equals("")?Collections.singletonList("<hologram_content_here>"):Collections.singletonList("<invalid_holo_id_provided!>"))):
                                                                (args[0].equalsIgnoreCase(subCommands.get(6)))
                                                                        ?((args.length==2)?(getHoloIDs()!=null?getHoloIDs():Collections.singletonList("<no_holograms_available!>")):
                                                                        ((args.length==3)||(args.length==4))?(!args[1].equals("") ?(getLines(Integer.parseInt(args[1]))!=null?getLines(Integer.parseInt(args[1])):Collections.singletonList("<invalid_holo_id_provided!>")):Collections.singletonList("<invalid_holo_id_provided!>")):
                                                                                Collections.singletonList("<too_much_arguments!>")):
                                                                        (args[0].equalsIgnoreCase(subCommands.get(7)))
                                                                                ?((args.length==2)?(getHoloIDs()!=null?getHoloIDs():Collections.singletonList("<no_holograms_available!>")):
                                                                                (!args[1].equals("")?Collections.singletonList("<too_much_arguments!>"):Collections.singletonList("<invalid_holo_id_provided!>")))
                                                                                :Collections.singletonList("<invalid_subcommand!>"));
            }
        }
        return null;
    }

    //================================================================================
    // Command
    //================================================================================
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("hologram") || command.getName().equalsIgnoreCase("holo")) {
            getLoggerManager().log("HologramCommand:execute()", "Player "+sender.getName()+" executed command with arguments ["+String.join(" ", args)+"].");
            String subCommand = args[0];
            if (!this.subCommands.contains(subCommand)) {
                sender.sendMessage("§c[Holo] Invalid subcommand received.");
                return false;
            }
            if (subCommand.equalsIgnoreCase(this.subCommands.get(0))) {
                if (args.length >= 2) {
                    List<String> content = new ArrayList<>();
                    Player p = (Player) sender;
                    for (int i = 1; i < args.length; i++) {
                        if (args[i].equalsIgnoreCase("")) continue;
                        content.add(args[i]);
                    }
                    if (content.size() == 0) {
                        sender.sendMessage("§c[Holo] Content can't be created from space characters.");
                    } else {
                        getHologramManager().createHologram(new Hologram(UUID.randomUUID(), String.join(" ", content), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getWorld(), false));
                        sender.sendMessage("§a[Holo] Successfully created hologram.");
                    }
                } else {
                    sender.sendMessage("§c[Holo] No arguments received for subcommand " + subCommand);
                }
            } else if (subCommand.equalsIgnoreCase(this.subCommands.get(2))) {
                if (args.length == 1) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("§b------------[Holograms]------------\n");
                    getHologramManager().getHolograms().forEach(holo -> {
                        builder.append("§a[")
                                .append(holo.getId())
                                .append("]: \n    content: ")
                                .append(StringEscapeUtils.escapeJava(holo.getContent()))
                                .append("\n    x: ").append(holo.getX()).append("\n    y: ").append(holo.getY()).append("\n    z: ").append(holo.getZ())
                                .append("\n    world: ").append(holo.getWorld().getUID())
                                .append("\n-----------------------------------\n");
                    });
                    builder.append("§b\n-----------------------------------");
                    sender.sendMessage(builder.toString());
                } else {
                    sender.sendMessage("§c[Holo] Too much arguments received.");
                }
            } else if (subCommand.equalsIgnoreCase(this.subCommands.get(1)) || subCommand.equalsIgnoreCase(this.subCommands.get(7))) {
                if (args.length == 2) {
                    int id;
                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (Exception ex) {
                        sender.sendMessage("§c[Holo] Invalid arguments received.");
                        return false;
                    }
                    if (subCommand.equalsIgnoreCase(this.subCommands.get(1))) {
                        if (getHologramManager().deleteHologram(id))
                            sender.sendMessage("§a[Holo] Hologram with id " + id + " was deleted.");
                        else
                            sender.sendMessage("§c[Holo] Problem occurred while processing subcommand " + subCommand);
                    } else if (subCommand.equalsIgnoreCase(this.subCommands.get(7))) {
                        Player p = (Player) sender;
                        if (getHologramManager().updateHologram(id, HologramUpdateType.UPDATE_LOCATION, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getWorld()))
                            sender.sendMessage("§a[Holo] Hologram with id " + id + " was moved to your location.");
                        else
                            sender.sendMessage("§c[Holo] Problem occurred while processing subcommand " + subCommand);
                    }
                } else {
                    sender.sendMessage("§c[Holo] Invalid arguments received.");
                }
            } else if (subCommand.equalsIgnoreCase(this.subCommands.get(4))) {
                if (args.length == 3) {
                    int id;
                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (Exception ex) {
                        sender.sendMessage("§c[Holo] Invalid arguments received.");
                        return false;
                    }
                    int line;
                    try {
                        line = Integer.parseInt(args[2]);
                    } catch (Exception ex) {
                        sender.sendMessage("§c[Holo] Invalid arguments received.");
                        return false;
                    }
                    if (getHologramManager().updateHologram(id, HologramUpdateType.DELETE_LINE, line))
                        sender.sendMessage("§a[Holo] Successfully removed line " + line + " from id " + id + ".");
                    else
                        sender.sendMessage("§c[Holo] Problem occurred while processing subcommand " + subCommand);
                } else {
                    sender.sendMessage("§c[Holo] Invalid arguments received.");
                }
            } else if (subCommand.equalsIgnoreCase(this.subCommands.get(5))) {
                if (args.length >= 2) {
                    int id;
                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (Exception ex) {
                        sender.sendMessage("§c[Holo] Invalid arguments received.");
                        return false;
                    }
                    List<String> content = new ArrayList<>();
                    for (int i = 2; i < args.length; i++) {
                        if (args[i].equals("")) continue;
                        content.add(args[i]);
                    }
                    if (content.size() != 0) {
                        if (getHologramManager().updateHologram(id, HologramUpdateType.INSERT_LINE, String.join(" ", content)))
                            sender.sendMessage("§a[Holo] Successfully inserted line " + StringEscapeUtils.escapeJava(String.join(" ", content)) + " to id " + id + ".");
                        else
                            sender.sendMessage("§c[Holo] Problem occurred while processing subcommand " + subCommand);
                    }
                } else {
                    sender.sendMessage("§c[Holo] Invalid arguments received.");
                }
            } else if (subCommand.equalsIgnoreCase(this.subCommands.get(3))) {
                if (args.length >= 3) {
                    int id, line;
                    try {
                        id = Integer.parseInt(args[1]);
                        line = Integer.parseInt(args[2]);
                    } catch (Exception ex) {
                        sender.sendMessage("§c[Holo] Invalid arguments received.");
                        return false;
                    }
                    List<String> content = new ArrayList<>();
                    for (int i = 3; i < args.length; i++) {
                        if (args[i].equals("")) continue;
                        content.add(args[i]);
                    }
                    if (content.size() != 0) {
                        if (getHologramManager().updateHologram(id, HologramUpdateType.UPDATE_LINE, line, String.join(" ", content)))
                            sender.sendMessage("§a[Holo] Successfully updated line " + line + " from id " + id + " to " + StringEscapeUtils.escapeJava(String.join(" ", content)) + ".");
                        else
                            sender.sendMessage("§c[Holo] Problem occurred while processing subcommand " + subCommand);
                    }
                } else {
                    sender.sendMessage("§c[Holo] Invalid arguments received.");
                }
            } else if (subCommand.equalsIgnoreCase(this.subCommands.get(6))) {
                if (args.length == 4) {
                    int id, lineFrom, lineTo;
                    try {
                        id = Integer.parseInt(args[1]);
                        lineFrom = Integer.parseInt(args[2]);
                        lineTo = Integer.parseInt(args[3]);
                    } catch (Exception ex) {
                        sender.sendMessage("§c[Holo] Invalid arguments received.");
                        return false;
                    }
                    if (getHologramManager().updateHologram(id, HologramUpdateType.REPLACE_LINE, lineFrom, lineTo))
                        sender.sendMessage("§a[Holo] Successfully replaced line " + lineFrom + " with line " + lineTo + ".");
                    else
                        sender.sendMessage("§c[Holo] Problem occurred while processing subcommand " + subCommand);
                } else {
                    sender.sendMessage("§c[Holo] Invalid arguments received.");
                }
            }
        }
        return false;
    }

    //================================================================================
    // Utils
    //================================================================================
    private List<String> getHoloIDs(){
        List<String> temp = new ArrayList<>();
        if(getHologramManager().getHolograms().size()>0) {
            getHologramManager().getHolograms().forEach(holo -> {
                temp.add(holo.getId()+"");
            });
            return temp;
        }else
            return null;
    }
    private List<String> getLines(int id){
        Hologram holo = getHologramManager().getHologram(id);
        List<String> temp = new ArrayList<>();
        if(holo != null){
            for (int i = 0; i < holo.getContent().split("\n", -1).length; i++) {
                temp.add((i+1)+"");
            }
            return temp;
        }
        return null;
    }
    private List<String> getSubCommandsByCharsContain(String chars){
        List<String> temp = new ArrayList<>();
        this.subCommands.forEach(sub->{
            if(sub.contains(chars)){
                temp.add(sub);
            }
        });
        if(temp.size() != 0) return temp;
        else return null;
    }
}
