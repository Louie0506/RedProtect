/*
 *
 * Copyright (c) 2019 - @FabioZumbi12
 * Last Modified: 28/03/19 20:18
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 *
 */

package br.net.fabiozumbi12.RedProtect.Bukkit.commands.SubCommands.RegionHandlers;

import br.net.fabiozumbi12.RedProtect.Bukkit.helpers.RPGui;
import br.net.fabiozumbi12.RedProtect.Bukkit.helpers.RPUtil;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Bukkit.commands.SubCommand;
import br.net.fabiozumbi12.RedProtect.Bukkit.config.RPConfig;
import br.net.fabiozumbi12.RedProtect.Bukkit.config.RPLang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static br.net.fabiozumbi12.RedProtect.Bukkit.commands.CommandHandlers.HandleHelpPage;
import static br.net.fabiozumbi12.RedProtect.Bukkit.commands.CommandHandlers.handleFlag;
import static br.net.fabiozumbi12.RedProtect.Bukkit.commands.CommandHandlers.checkCmd;

public class FlagCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            HandleHelpPage(sender, 1);
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            Region r = RedProtect.get().rm.getTopRegion(player.getLocation());
            if (r != null) {
                if (r.isLeader(player) || r.isAdmin(player)){
                    RPGui gui = new RPGui(RPUtil.getTitleName(r), player, r, false, RPConfig.getGuiMaxSlot());
                    gui.open();
                } else {
                    RPLang.sendMessage(player, "cmdmanager.region.flag.nopermregion");
                }
            } else {
                RPLang.sendMessage(player, "cmdmanager.region.todo.that");
            }
            return true;
        }

        if (args.length == 1) {
            Region r = RedProtect.get().rm.getTopRegion(player.getLocation());
            if (r == null) {
                RPLang.sendMessage(player, "cmdmanager.region.todo.that");
                return true;
            }

            if (args[0].equalsIgnoreCase("gui-edit")) {
                if (RedProtect.get().ph.hasCommandPerm(player, "gui-edit")) {
                    RPGui gui = new RPGui(RPLang.get("gui.editflag"), player, r, true, RPConfig.getGuiMaxSlot());
                    gui.open();
                } else {
                    RPLang.sendMessage(player, "no.permission");
                }
                return true;
            }

            if (RPConfig.getBool("flags-configuration.change-flag-delay.enable")) {
                if (RPConfig.getStringList("flags-configuration.change-flag-delay.flags").contains(args[0])) {
                    if (!RedProtect.get().changeWait.contains(r.getName() + args[0])) {
                        RPUtil.startFlagChanger(r.getName(), args[0], player);
                        handleFlag(player, args[0], "", r);
                        return true;
                    } else {
                        RPLang.sendMessage(player, RPLang.get("gui.needwait.tochange").replace("{seconds}", RPConfig.getString("flags-configuration.change-flag-delay.seconds")));
                        return true;
                    }
                }
            }
            handleFlag(player, args[0], "", r);
            return true;
        }


        Region r = RedProtect.get().rm.getTopRegion(player.getLocation());
        if (r == null) {
            RPLang.sendMessage(player, "cmdmanager.region.todo.that");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("gui-edit")) {
            if (RedProtect.get().ph.hasCommandPerm(player, "gui-edit")) {
                int MaxSlot;
                try {
                    MaxSlot = 9 * Integer.parseInt(args[1]);
                    if (MaxSlot > 54 || MaxSlot < RPConfig.getGuiMaxSlot()) {
                        RPLang.sendMessage(player, "gui.edit.invalid-lines");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    RPLang.sendMessage(player, "cmdmanager.region.invalid.number");
                    return true;
                }
                RPGui gui = new RPGui(RPLang.get("gui.editflag"), player, r, true, MaxSlot);
                gui.open();
            } else {
                RPLang.sendMessage(player, "no.permission");
            }
            return true;
        }

        //if (args.length >= 2)
        StringBuilder text = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            text.append(" ").append(args[i]);
        }
        if (RPConfig.getBool("flags-configuration.change-flag-delay.enable")) {
            if (RPConfig.getStringList("flags-configuration.change-flag-delay.flags").contains(args[1])) {
                if (!RedProtect.get().changeWait.contains(r.getName() + args[1])) {
                    RPUtil.startFlagChanger(r.getName(), args[1], player);
                    handleFlag(player, args[1], text.substring(1), r);
                    return true;
                } else {
                    RPLang.sendMessage(player, RPLang.get("gui.needwait.tochange").replace("{seconds}", RPConfig.getString("flags-configuration.change-flag-delay.seconds")));
                    return true;
                }
            }
        }
        handleFlag(player, args[0], text.substring(1), r);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> SotTab = new ArrayList<>();
        SortedSet<String> tab = new TreeSet<>();
        if (args.length == 1 || args.length == 2) {
            //rp flag <flag>
            if (checkCmd(args[0], "flag")) {
                for (String flag : RPConfig.getDefFlags()) {
                    if (!tab.contains(flag)) {
                        if (flag.equalsIgnoreCase(args[0])) {
                            Region r = RedProtect.get().rm.getTopRegion(((Player) sender).getLocation());
                            if (r != null && r.canBuild(((Player) sender)) && r.getFlags().containsKey(flag)) {
                                return Collections.singletonList(r.getFlags().get(flag).toString());
                            }
                            return SotTab;
                        }
                        if (flag.startsWith(args[0])) {
                            tab.add(flag);
                        }
                    }
                }
                for (String flag : RPConfig.AdminFlags) {
                    if (RedProtect.get().ph.hasFlagPerm((Player)sender, flag) && !tab.contains(flag)) {
                        if (flag.equalsIgnoreCase(args[0])) {
                            Region r = RedProtect.get().rm.getTopRegion(((Player) sender).getLocation());
                            if (r != null && r.canBuild(((Player) sender)) && r.getFlags().containsKey(flag)) {
                                return Collections.singletonList(r.getFlags().get(flag).toString());
                            }
                            return SotTab;
                        }
                        if (flag.startsWith(args[0])) {
                            tab.add(flag);
                        }
                    }
                }
                SotTab.addAll(tab);
                return SotTab;
            }
        }
        return null;
    }
}