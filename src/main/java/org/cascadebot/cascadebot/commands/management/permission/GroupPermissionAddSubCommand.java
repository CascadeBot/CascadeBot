/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.commands.management.permission;

import net.dv8tion.jda.api.entities.Member;
import org.cascadebot.cascadebot.commandmeta.CommandContext;
import org.cascadebot.cascadebot.commandmeta.Module;
import org.cascadebot.cascadebot.commandmeta.SubCommand;
import org.cascadebot.cascadebot.permissions.CascadePermission;
import org.cascadebot.cascadebot.utils.PermissionCommandUtils;

public class GroupPermissionAddSubCommand extends SubCommand {

    @Override
    public void onCommand(Member sender, CommandContext context) {
        if (context.getArgs().length < 2) {
            context.getUiMessaging().replyUsage();
            return;
        }

        if (!context.getData().getPermissionsManager().isValidPermission(context.getArg(1))) {
            context.getTypedMessaging().replyDanger(context.i18n("responses.permission_not_exist", context.getArg(1)));
            return;
        }

        PermissionCommandUtils.tryGetGroupFromString(context, context.getArg(0), group -> {
            if (group.addPermission(context.getArg(1))) {
                context.getTypedMessaging().replySuccess(context.i18n("commands.groupperms.add.success", context.getArg(1), group.getName() + "(" + group.getId() + ")"));
            } else {
                context.getTypedMessaging().replyWarning(context.i18n("commands.groupperms.add.fail", context.getArg(1), group.getName() + "(" + group.getId() + ")"));
            }
        }, sender.getIdLong());
    }

    @Override
    public String command() {
        return "add";
    }

    @Override
    public String parent() {
        return "groupperms";
    }

    @Override
    public CascadePermission permission() {
        return CascadePermission.of("permissions.group.add", false, Module.MANAGEMENT);
    }

    @Override
    public String description() {
        return null;
    }

}
