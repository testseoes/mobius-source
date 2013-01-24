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
package ai;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.Map;

import lineage2.commons.util.Rnd;
import lineage2.gameserver.ai.DefaultAI;
import lineage2.gameserver.model.Creature;
import lineage2.gameserver.model.Skill;
import lineage2.gameserver.model.Summon;
import lineage2.gameserver.model.instances.NpcInstance;
import bosses.BaiumManager;

public class Baium extends DefaultAI
{
	private boolean _firstTimeAttacked = true;
	private final Skill baium_normal_attack, energy_wave, earth_quake, thunderbolt, group_hold;
	
	public Baium(NpcInstance actor)
	{
		super(actor);
		TIntObjectHashMap<Skill> skills = getActor().getTemplate().getSkills();
		baium_normal_attack = skills.get(4127);
		energy_wave = skills.get(4128);
		earth_quake = skills.get(4129);
		thunderbolt = skills.get(4130);
		group_hold = skills.get(4131);
	}
	
	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		BaiumManager.setLastAttackTime();
		if (_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			NpcInstance actor = getActor();
			if (attacker == null)
			{
				return;
			}
			if (attacker.isPlayer())
			{
				for (Summon summon : attacker.getPlayer().getSummonList())
				{
					summon.doDie(actor);
				}
			}
			else if ((attacker.isServitor() || attacker.isPet()) && (attacker.getPlayer() != null))
			{
				attacker.getPlayer().doDie(actor);
			}
			attacker.doDie(actor);
		}
		super.onEvtAttacked(attacker, damage);
	}
	
	@Override
	protected boolean createNewTask()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}
		if (!BaiumManager.getZone().checkIfInZone(actor))
		{
			teleportHome();
			return false;
		}
		clearTasks();
		Creature target;
		if ((target = prepareTarget()) == null)
		{
			return false;
		}
		if (!BaiumManager.getZone().checkIfInZone(target))
		{
			actor.getAggroList().remove(target, false);
			return false;
		}
		int s_energy_wave = 20;
		int s_earth_quake = 20;
		int s_group_hold = actor.getCurrentHpPercents() > 50 ? 0 : 20;
		int s_thunderbolt = actor.getCurrentHpPercents() > 25 ? 0 : 20;
		Skill r_skill = null;
		if (actor.isMovementDisabled())
		{
			r_skill = thunderbolt;
		}
		else if (!Rnd.chance(100 - s_thunderbolt - s_group_hold - s_energy_wave - s_earth_quake))
		{
			Map<Skill, Integer> d_skill = new HashMap<>();
			double distance = actor.getDistance(target);
			addDesiredSkill(d_skill, target, distance, energy_wave);
			addDesiredSkill(d_skill, target, distance, earth_quake);
			if (s_group_hold > 0)
			{
				addDesiredSkill(d_skill, target, distance, group_hold);
			}
			if (s_thunderbolt > 0)
			{
				addDesiredSkill(d_skill, target, distance, thunderbolt);
			}
			r_skill = selectTopSkill(d_skill);
		}
		if (r_skill == null)
		{
			r_skill = baium_normal_attack;
		}
		else if (r_skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF)
		{
			target = actor;
		}
		addTaskCast(target, r_skill);
		r_skill = null;
		return true;
	}
	
	@Override
	protected boolean maybeMoveToHome()
	{
		NpcInstance actor = getActor();
		if ((actor != null) && !BaiumManager.getZone().checkIfInZone(actor))
		{
			teleportHome();
		}
		return false;
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}
