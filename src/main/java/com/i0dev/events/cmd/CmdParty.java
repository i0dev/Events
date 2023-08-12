package com.i0dev.events.cmd;

import com.massivecraft.massivecore.util.MUtil;

import java.util.List;

public class CmdParty extends EventsCommand {

    private static final CmdParty i = new CmdParty();

    public static CmdParty get() {
        return i;
    }

    public CmdPartyInfo cmdPartyInfo = new CmdPartyInfo();
    public CmdPartyInvite cmdPartyInvite = new CmdPartyInvite();
    public CmdPartyRename cmdPartyRename = new CmdPartyRename();
    public CmdPartyJoin cmdPartyJoin = new CmdPartyJoin();
    public CmdPartyLeave cmdPartyLeave = new CmdPartyLeave();
    public CmdPartyKick cmdPartyKick = new CmdPartyKick();

    @Override
    public List<String> getAliases() {
        return MUtil.list("party");
    }

}
