package reborncore.common.util;

import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Created by modmuss50 on 05/11/2016.
 */
public class RebornPermissions {

	public static final String WRENCH_BLOCK = "block.wrench";
	public static final String TAP_BLOCK = "block.tap";

	public static void init()
	{
		PermissionAPI.registerNode(WRENCH_BLOCK, DefaultPermissionLevel.ALL, "Node for wrenching blocks");
		PermissionAPI.registerNode(TAP_BLOCK, DefaultPermissionLevel.ALL, "Node for taping blocks with the tree tap");
	}

}
