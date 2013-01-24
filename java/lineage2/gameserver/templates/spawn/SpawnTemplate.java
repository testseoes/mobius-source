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
package lineage2.gameserver.templates.spawn;

import java.util.ArrayList;
import java.util.List;

public class SpawnTemplate
{
	private final PeriodOfDay _periodOfDay;
	private final int _count;
	private final int _respawn;
	private final int _respawnRandom;
	private final List<SpawnNpcInfo> _npcList = new ArrayList<>(1);
	private final List<SpawnRange> _spawnRangeList = new ArrayList<>(1);
	
	public SpawnTemplate(PeriodOfDay periodOfDay, int count, int respawn, int respawnRandom)
	{
		_periodOfDay = periodOfDay;
		_count = count;
		_respawn = respawn;
		_respawnRandom = respawnRandom;
	}
	
	public void addSpawnRange(SpawnRange range)
	{
		_spawnRangeList.add(range);
	}
	
	public SpawnRange getSpawnRange(int index)
	{
		return _spawnRangeList.get(index);
	}
	
	public void addNpc(SpawnNpcInfo info)
	{
		_npcList.add(info);
	}
	
	public SpawnNpcInfo getNpcId(int index)
	{
		return _npcList.get(index);
	}
	
	public int getNpcSize()
	{
		return _npcList.size();
	}
	
	public int getSpawnRangeSize()
	{
		return _spawnRangeList.size();
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public int getRespawn()
	{
		return _respawn;
	}
	
	public int getRespawnRandom()
	{
		return _respawnRandom;
	}
	
	public PeriodOfDay getPeriodOfDay()
	{
		return _periodOfDay;
	}
}
