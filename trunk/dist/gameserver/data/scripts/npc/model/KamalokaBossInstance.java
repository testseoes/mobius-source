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
package npc.model;

import java.util.concurrent.ScheduledFuture;

import lineage2.gameserver.ThreadPoolManager;
import lineage2.gameserver.model.Creature;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.World;
import lineage2.gameserver.network.serverpackets.SystemMessage;
import lineage2.gameserver.templates.npc.NpcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KamalokaBossInstance extends LostCaptainInstance
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final Logger _log = LoggerFactory.getLogger(KamalokaBossInstance.class);
	private ScheduledFuture<?> _manaRegen;
	
	public KamalokaBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_manaRegen = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ManaRegen(), 20000, 20000);
	}
	
	@Override
	public boolean isRaid()
	{
		return false;
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		if (_manaRegen != null)
		{
			_manaRegen.cancel(false);
			_manaRegen = null;
		}
		super.onDeath(killer);
	}
	
	private class ManaRegen implements Runnable
	{
		public ManaRegen()
		{
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void run()
		{
			try
			{
				for (Player p : World.getAroundPlayers(KamalokaBossInstance.this))
				{
					if (p.isHealBlocked())
					{
						continue;
					}
					int addMp = getAddMp();
					if (addMp <= 0)
					{
						return;
					}
					double newMp = Math.min(Math.max(0, p.getMaxMp() - p.getCurrentMp()), addMp);
					if (newMp > 0)
					{
						p.setCurrentMp(newMp + p.getCurrentMp());
					}
					p.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(Math.round(newMp)));
				}
			}
			catch (Exception e)
			{
				_log.error("", e);
			}
		}
		
		private int getAddMp()
		{
			switch (getLevel())
			{
				case 23:
				case 26:
					return 6;
				case 33:
				case 36:
					return 10;
				case 43:
				case 46:
					return 13;
				case 53:
				case 56:
					return 16;
				case 63:
				case 66:
					return 19;
				case 73:
					return 22;
				default:
					return 0;
			}
		}
	}
}
