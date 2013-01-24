/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handler.items;

import lineage2.gameserver.handler.items.IItemHandler;
import lineage2.gameserver.handler.items.ItemHandler;
import lineage2.gameserver.model.Playable;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.items.ItemInstance;
import lineage2.gameserver.scripts.ScriptFile;
import lineage2.gameserver.utils.Location;
import lineage2.gameserver.utils.Log;

public abstract class ScriptItemHandler implements ScriptFile, IItemHandler
{
	@Override
	public void dropItem(Player player, ItemInstance item, long count, Location loc)
	{
		if (item.isEquipped())
		{
			player.getInventory().unEquipItem(item);
			player.sendUserInfo(true);
		}
		item = player.getInventory().removeItemByObjectId(item.getObjectId(), count);
		if (item == null)
		{
			player.sendActionFailed();
			return;
		}
		Log.LogItem(player, Log.Drop, item);
		item.dropToTheGround(player, loc);
		player.disableDrop(1000);
		player.sendChanges();
	}
	
	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}
	
	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}
	
	@Override
	public void onReload()
	{
		ItemHandler.getInstance().unregisterItemHandler(this);
	}
	
	@Override
	public void onShutdown()
	{
	}
}
