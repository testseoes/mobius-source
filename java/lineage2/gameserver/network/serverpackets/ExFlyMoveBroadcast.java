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
package lineage2.gameserver.network.serverpackets;

import lineage2.gameserver.model.Player;
import lineage2.gameserver.utils.Location;

/**
 * @author K1mel
 * @twitter http://twitter.com/k1mel_developer
 */
public class ExFlyMoveBroadcast extends L2GameServerPacket
{
	private final int _objId;
	private final int flyType;
	private final Location _loc;
	private final Location _destLoc;
	
	public ExFlyMoveBroadcast(Player player, int flyType, Location destLoc)
	{
		_objId = player.getObjectId();
		this.flyType = flyType;
		_loc = player.getLoc();
		_destLoc = destLoc;
	}
	
	@Override
	protected void writeImpl()
	{
		writeEx(0x10D);
		writeD(_objId);
		
		writeD(flyType);
		writeD(0x00); // TODO: [K1mel]
		
		writeD(_loc.getX());
		writeD(_loc.getY());
		writeD(_loc.getZ());
		
		writeD(0x00); // TODO: [K1mel]
		
		writeD(_destLoc.getX());
		writeD(_destLoc.getY());
		writeD(_destLoc.getZ());
	}
}