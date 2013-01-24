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
package lineage2.gameserver.model.entity.olympiad;

import java.util.Set;

import lineage2.gameserver.instancemanager.ReflectionManager;
import lineage2.gameserver.model.Effect;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.Skill;
import lineage2.gameserver.model.Summon;
import lineage2.gameserver.model.base.TeamType;
import lineage2.gameserver.model.entity.Hero;
import lineage2.gameserver.model.entity.Reflection;
import lineage2.gameserver.model.entity.events.impl.DuelEvent;
import lineage2.gameserver.model.items.ItemInstance;
import lineage2.gameserver.network.serverpackets.ExAutoSoulShot;
import lineage2.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import lineage2.gameserver.network.serverpackets.ExOlympiadMode;
import lineage2.gameserver.network.serverpackets.Revive;
import lineage2.gameserver.network.serverpackets.SkillCoolTime;
import lineage2.gameserver.skills.EffectType;
import lineage2.gameserver.skills.TimeStamp;
import lineage2.gameserver.tables.SkillTable;
import lineage2.gameserver.templates.InstantZone;
import lineage2.gameserver.templates.StatsSet;
import lineage2.gameserver.utils.Location;
import lineage2.gameserver.utils.Log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamMember
{
	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(TeamMember.class);
	private String _name = StringUtils.EMPTY;
	private String _clanName = StringUtils.EMPTY;
	private int _classId;
	private double _damage;
	private boolean _isDead;
	private final int _objId;
	private final OlympiadGame _game;
	private final CompType _type;
	private final int _side;
	private Player _player;
	private Location _returnLoc = null;
	
	public boolean isDead()
	{
		return _isDead;
	}
	
	public void doDie()
	{
		_isDead = true;
	}
	
	public TeamMember(int obj_id, String name, Player player, OlympiadGame game, int side)
	{
		_objId = obj_id;
		_name = name;
		_game = game;
		_type = game.getType();
		_side = side;
		_player = player;
		if (_player == null)
		{
			return;
		}
		_clanName = player.getClan() == null ? StringUtils.EMPTY : player.getClan().getName();
		_classId = player.getActiveClassId();
		player.setOlympiadSide(side);
		player.setOlympiadGame(game);
	}
	
	public StatsSet getStat()
	{
		return Olympiad._nobles.get(_objId);
	}
	
	public void incGameCount()
	{
		StatsSet set = getStat();
		switch (_type)
		{
			case CLASSED:
				set.set(Olympiad.GAME_CLASSES_COUNT, set.getInteger(Olympiad.GAME_CLASSES_COUNT) + 1);
				break;
			case NON_CLASSED:
				set.set(Olympiad.GAME_NOCLASSES_COUNT, set.getInteger(Olympiad.GAME_NOCLASSES_COUNT) + 1);
				break;
		}
	}
	
	public void takePointsForCrash()
	{
		if (!checkPlayer())
		{
			StatsSet stat = getStat();
			int points = stat.getInteger(Olympiad.POINTS);
			int diff = Math.min(OlympiadGame.MAX_POINTS_LOOSE, points / _type.getLooseMult());
			stat.set(Olympiad.POINTS, points - diff);
			Log.add("Olympiad Result: " + _name + " lost " + diff + " points for crash", "olympiad");
			Player player = _player;
			if (player == null)
			{
				Log.add("Olympiad info: " + _name + " crashed coz player == null", "olympiad");
			}
			else
			{
				if (player.isLogoutStarted())
				{
					Log.add("Olympiad info: " + _name + " crashed coz player.isLogoutStarted()", "olympiad");
				}
				if (!player.isConnected())
				{
					Log.add("Olympiad info: " + _name + " crashed coz !player.isOnline()", "olympiad");
				}
				if (player.getOlympiadGame() == null)
				{
					Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadGame() == null", "olympiad");
				}
				if (player.getOlympiadObserveGame() != null)
				{
					Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadObserveGame() != null", "olympiad");
				}
			}
		}
	}
	
	public boolean checkPlayer()
	{
		Player player = _player;
		if ((player == null) || player.isLogoutStarted() || (player.getOlympiadGame() == null) || player.isInObserverMode())
		{
			return false;
		}
		return true;
	}
	
	public void portPlayerToArena()
	{
		Player player = _player;
		if (!checkPlayer() || player.isTeleporting())
		{
			_player = null;
			return;
		}
		DuelEvent duel = player.getEvent(DuelEvent.class);
		if (duel != null)
		{
			duel.abortDuel(player);
		}
		_returnLoc = player._stablePoint == null ? player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc() : player._stablePoint;
		if (player.isDead())
		{
			player.setPendingRevive(true);
		}
		if (player.isSitting())
		{
			player.standUp();
		}
		player.setTarget(null);
		player.setIsInOlympiadMode(true);
		player.leaveParty();
		Reflection ref = _game.getReflection();
		InstantZone instantZone = ref.getInstancedZone();
		Location tele = Location.findPointToStay(instantZone.getTeleportCoords().get(_side - 1), 50, 50, ref.getGeoIndex());
		player._stablePoint = _returnLoc;
		player.teleToLocation(tele, ref);
		if (_type == CompType.TEAM)
		{
			player.setTeam(_side == 1 ? TeamType.BLUE : TeamType.RED);
		}
		player.sendPacket(new ExOlympiadMode(_side));
	}
	
	public void portPlayerBack()
	{
		Player player = _player;
		if (player == null)
		{
			return;
		}
		if (_returnLoc == null)
		{
			return;
		}
		player.setIsInOlympiadMode(false);
		player.setOlympiadSide(-1);
		player.setOlympiadGame(null);
		if (_type == CompType.TEAM)
		{
			player.setTeam(TeamType.NONE);
		}
		for (Effect e : player.getEffectList().getAllEffects())
		{
			if ((e.getEffectType() != EffectType.Cubic) || (player.getSkillLevel(e.getSkill().getId()) <= 0))
			{
				e.exit();
			}
		}
		for (Summon summon : player.getSummonList())
		{
			summon.getEffectList().stopAllEffects();
		}
		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());
		if (player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new Revive(player));
		}
		else
		{
			player.setCurrentHp(player.getMaxHp(), false);
		}
		if ((player.getClan() != null) && (player.getClan().getReputationScore() >= 0))
		{
			player.getClan().enableSkills(player);
		}
		if (player.isHero())
		{
			Hero.addSkills(player);
		}
		player.sendSkillList();
		player.sendPacket(new ExOlympiadMode(0));
		player.sendPacket(new ExOlympiadMatchEnd());
		player._stablePoint = null;
		player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
	}
	
	public void preparePlayer()
	{
		Player player = _player;
		if (player == null)
		{
			return;
		}
		if (player.isInObserverMode())
		{
			if (player.getOlympiadObserveGame() != null)
			{
				player.leaveOlympiadObserverMode(true);
			}
			else
			{
				player.leaveObserverMode();
			}
		}
		if (player.getClan() != null)
		{
			player.getClan().disableSkills(player);
		}
		if (player.isHero())
		{
			Hero.removeSkills(player);
		}
		if (player.isCastingNow())
		{
			player.abortCast(true, true);
		}
		for (Effect e : player.getEffectList().getAllEffects())
		{
			if ((e.getEffectType() != EffectType.Cubic) || (player.getSkillLevel(e.getSkill().getId()) <= 0))
			{
				e.exit();
			}
		}
		for (Summon summon : player.getSummonList())
		{
			if (summon.isPet())
			{
				summon.unSummon();
			}
			else
			{
				summon.getEffectList().stopAllEffects();
			}
		}
		if (player.getAgathionId() > 0)
		{
			player.setAgathion(0);
		}
		for (TimeStamp sts : player.getSkillReuses())
		{
			if (sts == null)
			{
				continue;
			}
			Skill skill = SkillTable.getInstance().getInfo(sts.getId(), sts.getLevel());
			if (skill == null)
			{
				continue;
			}
			if (sts.getReuseBasic() <= (15 * 60000L))
			{
				player.enableSkill(skill);
			}
		}
		player.sendSkillList();
		player.sendPacket(new SkillCoolTime(player));
		ItemInstance wpn = player.getActiveWeaponInstance();
		if ((wpn != null) && wpn.isHeroWeapon())
		{
			player.getInventory().unEquipItem(wpn);
			player.abortAttack(true, true);
		}
		Set<Integer> activeSoulShots = player.getAutoSoulShot();
		for (int itemId : activeSoulShots)
		{
			player.removeAutoSoulShot(itemId);
			player.sendPacket(new ExAutoSoulShot(itemId, false));
		}
		ItemInstance weapon = player.getActiveWeaponInstance();
		if (weapon != null)
		{
			weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
			weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);
		}
		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());
		player.broadcastUserInfo(true);
	}
	
	public void saveNobleData()
	{
		OlympiadDatabase.saveNobleData(_objId);
	}
	
	public void logout()
	{
		_player = null;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void addDamage(double d)
	{
		_damage += d;
	}
	
	public double getDamage()
	{
		return _damage;
	}
	
	public String getClanName()
	{
		return _clanName;
	}
	
	public int getClassId()
	{
		return _classId;
	}
	
	public int getObjectId()
	{
		return _objId;
	}
}
