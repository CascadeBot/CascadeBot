package org.cascadebot.cascadebot.commands.music;

import net.dv8tion.jda.core.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandMain;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.music.CascadePlayer;
import org.cascadebot.cascadebot.permissions.CascadePermission;

public class VolumeCommand implements ICommandMain {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        CascadePlayer player = context.getData().getMusicPlayer();
        if (context.getArgs().length == 0) {
            context.getTypedMessaging().replyInfo("Current volume is %s%%", player.getPlayer().getVolume());
            return;
        }
        Integer Arguments = Integer.valueOf(context.getMessage(0));
        if (Arguments < 0 || Arguments > 100) {
            context.getTypedMessaging().replyWarning("Volume needs to be between 100 and 0");
            return;
        }

        if (Arguments == context.getData().getMusicPlayer().getPlayer().getVolume()) {
            context.getTypedMessaging().replyInfo("Volume is already %s%%", player.getPlayer().getVolume());
        } else {
            player.getPlayer().setVolume(Arguments);
            context.getTypedMessaging().replyInfo("Volume set to %s%%", player.getPlayer().getVolume());
        }

    }


    @Override
    public Module getModule() {
        return Module.MUSIC;
    }

    @Override
    public String command() {
        return "volume";
    }

    @Override
    public CascadePermission getPermission() {
        return CascadePermission.of("Volume", "search", getModule());
    }

    @Override
    public String description() {
        return "Changes the players volume";
    }

}
