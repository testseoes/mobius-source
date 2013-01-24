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

import lineage2.gameserver.dao.CharacterDAO;
import lineage2.gameserver.instancemanager.MuseumManager;
import lineage2.gameserver.instancemanager.MuseumManager.Museum;

public class ExLoadStatHotLink extends L2GameServerPacket
{
	private static final String _S__FE_102_EXLOADSTATHOTLINK = "[S] FE:101 ExLoadStatHotLink";
	int category1, category2;
	MuseumManager mm;
	boolean loadWorldRank;
	
	public ExLoadStatHotLink(int _category1, int _category2)
	{
		mm = MuseumManager.getInstance();
		category1 = _category1;
		category2 = _category2;
		loadWorldRank = false;
		for (String[] categories : mm.getLoadingInfo())
		{
			if (categories[2].contains("" + category1))
			{
				loadWorldRank = true;
			}
		}
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x102);
		writeD(category1);
		writeD(category2);
		if (loadWorldRank)
		{
			for (int i = 0; i < 2; i++)
			{
				boolean isTotal = i == 1;
				writeD(!mm.getMuseums(category1, isTotal).isEmpty() ? mm.getMuseums(category1, isTotal).size() : 0x00);
				if (!mm.getMuseums(category1, isTotal).isEmpty())
				{
					for (Museum player : mm.getMuseums(category1, isTotal))
					{
						writeH(0x00);
						writeD(player.getObjectId());
						writeS(CharacterDAO.getInstance().getNameByObjectId(player.getObjectId()));
						writeQ(player.getAcquiredItem());
						writeH(0x00);
						writeD(0x00);
						writeD(0x00);
					}
				}
			}
		}
		else
		{
			writeD(0x00);
			writeD(0x00);
		}
	}
	
	@Override
	public String getType()
	{
		return _S__FE_102_EXLOADSTATHOTLINK;
	}
}
