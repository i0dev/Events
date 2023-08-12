package com.i0dev.events.cmd;

import com.i0dev.events.EventsPlugin;
import com.i0dev.events.Perm;
import com.i0dev.events.entity.MConf;
import com.massivecraft.massivecore.command.MassiveCommandVersion;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

import java.util.List;

public class CmdEvents extends EventsCommand {

    private static CmdEvents i = new CmdEvents();

    public static CmdEvents get() {
        return i;
    }

    public CmdEventsJoin cmdEventsJoin = new CmdEventsJoin();
    public CmdEventsStart cmdEventsStart = new CmdEventsStart();
    public CmdEventsSpectate cmdEventsSpectate = new CmdEventsSpectate();
    public CmdEventsLeave cmdEventsLeave = new CmdEventsLeave();
    public CmdEventsEnd cmdEventsEnd = new CmdEventsEnd();
    public MassiveCommandVersion cmdFactionsVersion = new MassiveCommandVersion(EventsPlugin.get()).setAliases("v", "version").addRequirements(RequirementHasPerm.get(Perm.VERSION));

    @Override
    public List<String> getAliases() {
        return MConf.get().aliasesEvents;
    }

}
