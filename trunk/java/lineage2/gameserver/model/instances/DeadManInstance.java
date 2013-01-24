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
package lineage2.gameserver.model.instances;

import lineage2.gameserver.ai.CharacterAI;
import lineage2.gameserver.model.Creature;
import lineage2.gameserver.model.Skill;
import lineage2.gameserver.network.serverpackets.Die;
import lineage2.gameserver.templates.npc.NpcTemplate;

public class DeadManInstance extends NpcInstance
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DeadManInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setAI(new CharacterAI(this));
	}
	
	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		setCurrentHp(0, false);
		broadcastPacket(new Die(this));
		setWalking();
	}
	
	@Override
	public void reduceCurrentHp(double damage, double reflectableDamage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
	}
	
	@Override
	public boolean isInvul()
	{
		return true;
	}
	
	@Override
	public boolean isBlocked()
	{
		return true;
	}
}
