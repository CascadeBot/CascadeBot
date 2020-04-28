/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.core;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.ICommandCore;
import org.cascadebot.cascadebot.data.Config;
import org.cascadebot.cascadebot.data.objects.donation.AmountFlag;

public class PrefixCommand implements ICommandCore {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length > 0) {
            String newPrefix = context.getArg(0);

            if (context.testForArg("reset")) {
                if (context.hasPermission("prefix.reset")) {
                    context.getCoreSettings().setPrefix(Config.INS.getDefaultPrefix());
                    context.getTypedMessaging().replyInfo(context.i18n("commands.prefix.prefix_reset", Config.INS.getDefaultPrefix()));
                } else {
                    context.getUiMessaging().sendPermissionError("prefix.reset");
                }
                return;
            }

            if (!context.hasPermission("prefix.set")) {
                context.getUiMessaging().sendPermissionError("prefix.set");
                return;
            }

            AmountFlag prefixFlag = (AmountFlag) context.getData().getAllFlags().getFlag("prefix_length");
            if (prefixFlag == null) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.prefix.prefix_too_long"));
                return;
            }
            if (newPrefix.length() > prefixFlag.getAmount()) {
                context.getTypedMessaging().replyDanger(context.i18n("commands.prefix.prefix_too_long"));
                return;
            }
            context.getCoreSettings().setPrefix(newPrefix);
            context.getTypedMessaging().replyInfo(context.i18n("commands.prefix.new_prefix", newPrefix));
        } else {
            context.getTypedMessaging().replyInfo(context.i18n("commands.prefix.current_prefix", context.getCoreSettings().getPrefix()));
        }
    }

    @Override
    public String command() {
        return "prefix";
    }

}
