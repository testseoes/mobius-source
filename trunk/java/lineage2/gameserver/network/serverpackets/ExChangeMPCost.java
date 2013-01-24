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

public class ExChangeMPCost extends L2GameServerPacket
{
	private final int unk1;
	private final double unk2;
	
	public ExChangeMPCost(int unk1, double unk2)
	{
		this.unk1 = unk1;
		this.unk2 = unk2;
	}
	
	@Override
	protected void writeImpl()
	{
		writeEx(0xEA);
		writeD(unk1);
		writeF(unk2);
	}
}
