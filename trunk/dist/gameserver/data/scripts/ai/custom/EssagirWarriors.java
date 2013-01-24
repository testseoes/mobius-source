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
package ai.custom;

import lineage2.commons.util.Rnd;
import lineage2.gameserver.ai.DefaultAI;
import lineage2.gameserver.model.Creature;
import lineage2.gameserver.model.instances.NpcInstance;
import lineage2.gameserver.network.serverpackets.components.ChatType;
import lineage2.gameserver.network.serverpackets.components.NpcString;
import lineage2.gameserver.scripts.Functions;

public class EssagirWarriors extends DefaultAI
{
	private final static int[][] SMP_COORDS =
	{
		{
			-115144,
			237352,
			-3114
		},
		{
			-115320,
			236664,
			-3114
		},
		{
			-115272,
			235928,
			-3114
		},
		{
			-114808,
			235544,
			-3114
		},
		{
			-114296,
			235576,
			-3114
		},
		{
			-113704,
			236296,
			-3068
		},
		{
			-113624,
			236808,
			-3068
		},
		{
			-113592,
			237224,
			-3068
		}
	};
	private static final int SAY_RAFF = 12000;
	private int currentState;
	private long lastSayTimer = 0;
	
	public EssagirWarriors(NpcInstance actor)
	{
		super(actor);
		currentState = 0;
		lastSayTimer = 0;
	}
	
	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		int[][] coords;
		coords = SMP_COORDS;
		actor.setRunning();
		if ((actor.getTarget() == null) || (currentState >= coords.length) || (currentState == 0))
		{
			if (currentState < coords.length)
			{
				actor.moveToLocation(coords[currentState][0], coords[currentState][1], coords[currentState][2], Rnd.get(0, 50), true);
				if (actor.getDestination() == null)
				{
					++currentState;
				}
			}
			else
			{
				actor.doDie(actor);
			}
		}
		if ((lastSayTimer + SAY_RAFF) < System.currentTimeMillis())
		{
			lastSayTimer = System.currentTimeMillis();
			Functions.npcSay(actor, NpcString.IT_S_HERE, ChatType.NPC_SAY, 800);
		}
		return true;
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		return;
	}
}
