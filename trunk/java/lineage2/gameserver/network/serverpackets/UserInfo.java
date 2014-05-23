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

import javolution.util.FastList;
import lineage2.gameserver.Config;
import lineage2.gameserver.data.xml.holder.NpcHolder;
import lineage2.gameserver.instancemanager.CursedWeaponsManager;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.Skill;
import lineage2.gameserver.model.Zone;
import lineage2.gameserver.model.base.Element;
import lineage2.gameserver.model.base.Experience;
import lineage2.gameserver.model.base.TeamType;
import lineage2.gameserver.model.entity.events.GlobalEvent;
import lineage2.gameserver.model.items.Inventory;
import lineage2.gameserver.model.items.PcInventory;
import lineage2.gameserver.model.matching.MatchingRoom;
import lineage2.gameserver.model.pledge.Alliance;
import lineage2.gameserver.model.pledge.Clan;
import lineage2.gameserver.skills.effects.EffectCubic;
import lineage2.gameserver.utils.Location;

public class UserInfo extends L2GameServerPacket
{
	private boolean can_writeImpl = false;
	private final boolean partyRoom;
	private final int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd;
	private int _flyRunSpd;
	private int _flyWalkSpd;
	private int _relation;
	private final double move_speed, attack_speed, col_radius, col_height;
	private final int[][] _inv;
	private final Location _loc, _fishLoc;
	private final int obj_id, vehicle_obj_id, _race, sex, base_class, level, curCp, maxCp;
	private int _enchant;
	private final int _weaponFlag;
	private final long _exp;
	private final int curHp, maxHp, curMp, maxMp, curLoad, maxLoad, rec_left, rec_have;
	private final int _str, _con, _dex, _int, _wit, _men, _sp, ClanPrivs, InventoryLimit;
	private final int _patk, _patkspd, _pdef, evasion, accuracy, crit, _matk, _matkspd, _mevasion, _maccuracy, _mCritRate;
	private final int _mdef, pvp_flag, karma, hair_style, hair_color, face, gm_commands, fame, vitality;
	private int clan_id, clan_crest_id, ally_id, ally_crest_id, large_clan_crest_id;
	private final int private_store, can_crystalize, pk_kills, pvp_kills, class_id, agathion, _partySubstitute;
	private final int noble, hero;
	private int mount_id;
	private int cw_level;
	private final int name_color, running, pledge_class, pledge_type, title_color, transformation;
	private int fishing;
	private final int defenceFire, defenceWater, defenceWind, defenceEarth, defenceHoly, defenceUnholy;
	private int mount_type;
	private String _name, title;
	private final EffectCubic[] cubics;
	private final Element attackElement;
	private final int attackElementValue;
	private final boolean isFlying, _allowMap;
	private final int talismans;
	private final boolean openCloak;
	private final double _expPercent;
	private final TeamType _team;
	private final FastList<Integer> _aveList;
	private final PcInventory inv;
	
	public UserInfo(Player player)
	{
		if (player.getTransformationName() != null)
		{
			_name = player.getTransformationName();
			title = "";
			clan_crest_id = 0;
			ally_crest_id = 0;
			large_clan_crest_id = 0;
			cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
		}
		else
		{
			_name = player.getName();
			
			Clan clan = player.getClan();
			Alliance alliance = clan == null ? null : clan.getAlliance();
			//
			clan_id = clan == null ? 0 : clan.getClanId();
			clan_crest_id = clan == null ? 0 : clan.getCrestId();
			large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
			//
			ally_id = alliance == null ? 0 : alliance.getAllyId();
			ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();
			
			cw_level = 0;
			title = player.getTitle();
		}
		
		if (player.getPlayerAccess().GodMode && player.isInvisible())
		{
			title += "[Invisible]";
		}
		
		if (player.isPolymorphed())
		{
			if (NpcHolder.getInstance().getTemplate(player.getPolyId()) != null)
			{
				title += " - " + NpcHolder.getInstance().getTemplate(player.getPolyId()).name;
			}
			else
			{
				title += " - Polymorphed";
			}
		}
		
		if (player.isMounted())
		{
			_enchant = 0;
			mount_id = player.getMountNpcId() + 1000000;
			mount_type = player.getMountType();
		}
		else
		{
			_enchant = player.getEnchantEffect();
			mount_id = 0;
			mount_type = 0;
		}
		
		_weaponFlag = player.getActiveWeaponInstance() == null ? 0x14 : 0x28;
		
		move_speed = player.getMovementSpeedMultiplier();
		_runSpd = (int) (player.getRunSpeed() / move_speed);
		_walkSpd = (int) (player.getWalkSpeed() / move_speed);
		
		_flRunSpd = (int) player.getTemplate().getBaseFlyRunSpd();
		_flWalkSpd = (int) player.getTemplate().getBaseFlyWalkSpd();
		
		if (player.isFlying())
		{
			_flyRunSpd = _runSpd;
			_flyWalkSpd = _walkSpd;
		}
		else
		{
			_flyRunSpd = 0;
			_flyWalkSpd = 0;
		}
		
		_swimRunSpd = player.getSwimRunSpeed();
		_swimWalkSpd = player.getSwimWalkSpeed();
		
		_inv = new int[Inventory.PAPERDOLL_MAX][3];
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			_inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][2] = player.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
		}
		
		_relation = player.isClanLeader() ? 0x40 : 0;
		for (GlobalEvent e : player.getEvents())
		{
			_relation = e.getUserRelation(player, _relation);
		}
		
		_loc = player.getLoc();
		obj_id = player.getObjectId();
		vehicle_obj_id = player.isInBoat() ? player.getBoat().getObjectId() : 0x00;
		_race = player.getRace().ordinal();
		sex = player.getSex();
		base_class = player.getBaseClassId();
		level = player.getLevel();
		_exp = player.getExp();
		_expPercent = Experience.getExpPercent(player.getLevel(), player.getExp());
		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
		curHp = (int) player.getCurrentHp();
		maxHp = player.getMaxHp();
		curMp = (int) player.getCurrentMp();
		maxMp = player.getMaxMp();
		curLoad = player.getCurrentLoad();
		maxLoad = player.getMaxLoad();
		_sp = player.getIntSp();
		_patk = player.getPAtk(null);
		_patkspd = player.getPAtkSpd();
		_pdef = player.getPDef(null);
		evasion = player.getEvasionRate(null);
		_mevasion = player.getMEvasionRate(null);
		_maccuracy = player.getMAccuracy();
		_mCritRate = (int) player.getMagicCriticalRate(null, null);
		accuracy = player.getAccuracy();
		crit = player.getCriticalHit(null, null);
		_matk = player.getMAtk(null, null);
		_matkspd = player.getMAtkSpd();
		_mdef = player.getMDef(null, null);
		pvp_flag = player.getPvpFlag(); // 0=white, 1=purple, 2=purpleblink
		karma = player.getKarma();
		attack_speed = player.getAttackSpeedMultiplier();
		col_radius = player.getColRadius();
		col_height = player.getColHeight();
		hair_style = player.getHairStyle();
		hair_color = player.getHairColor();
		face = player.getFace();
		gm_commands = player.isGM() || player.getPlayerAccess().CanUseGMCommand ? 1 : 0;
		// builder level
		clan_id = player.getClanId();
		ally_id = player.getAllyId();
		private_store = player.getPrivateStoreType();
		can_crystalize = player.getSkillLevel(Skill.SKILL_CRYSTALLIZE) > 0 ? 1 : 0;
		pk_kills = player.getPkKills();
		pvp_kills = player.getPvpKills();
		cubics = player.getCubics().toArray(new EffectCubic[player.getCubics().size()]);
		_aveList = player.getAveList();
		ClanPrivs = player.getClanPrivileges();
		rec_left = player.getRecomLeft(); // c2 recommendations remaining
		rec_have = player.getRecomHave(); // c2 recommendations received
		InventoryLimit = player.getInventoryLimit();
		class_id = player.getClassId().getId();
		maxCp = player.getMaxCp();
		curCp = (int) player.getCurrentCp();
		_team = player.getTeam();
		noble = player.isNoble() || (player.isGM() && Config.GM_HERO_AURA) ? 1 : 0; // 0x01:
		// symbol
		// on
		// char
		// menu
		// ctrl+I
		hero = player.isHero() || (player.isGM() && Config.GM_HERO_AURA) ? 1 : 0; // 0x01:
		// Hero
		// Aura
		// and
		// symbol
		fishing = player.isFishing() ? 1 : 0; // Fishing Mode
		_fishLoc = player.getFishLoc();
		name_color = player.getNameColor();
		running = player.isRunning() ? 0x01 : 0x00; // changes the Speed display
		// on Status Window
		pledge_class = player.getPledgeClass();
		pledge_type = player.getPledgeType();
		title_color = player.getTitleColor();
		transformation = player.getTransformation();
		attackElement = player.getAttackElement();
		attackElementValue = player.getAttack(attackElement);
		defenceFire = player.getDefence(Element.FIRE);
		defenceWater = player.getDefence(Element.WATER);
		defenceWind = player.getDefence(Element.WIND);
		defenceEarth = player.getDefence(Element.EARTH);
		defenceHoly = player.getDefence(Element.HOLY);
		defenceUnholy = player.getDefence(Element.UNHOLY);
		agathion = player.getAgathionId();
		fame = player.getFame();
		vitality = player.getVitality();
		partyRoom = (player.getMatchingRoom() != null) && (player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING) && (player.getMatchingRoom().getLeader() == player);
		isFlying = player.isInFlyingTransform();
		talismans = player.getTalismanCount();
		openCloak = player.getOpenCloak();
		_allowMap = player.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP);
		fishing = player.isFishing() ? 1 : 0; // Fishing Mode
		_partySubstitute = 0;
		inv = player.getInventory();
		can_writeImpl = true;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (!can_writeImpl)
		{
			return;
		}
		
		writeC(0x32);
		
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeD(vehicle_obj_id);
		writeD(obj_id);
		writeS(_name);
		writeD(_race);
		writeD(sex);
		writeD(base_class);
		writeD(level);
		writeQ(_exp);
		writeF(_expPercent);
		writeD(_str);
		writeD(_dex);
		writeD(_con);
		writeD(_int);
		writeD(_wit);
		writeD(_men);
		writeD(maxHp);
		writeD(curHp);
		writeD(maxMp);
		writeD(curMp);
		writeD(_sp);
		writeD(curLoad);
		writeD(maxLoad);
		writeD(_weaponFlag);
		
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][0]);
		}
		
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][1]);
		}
		
		for (int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			writeD(_inv[PAPERDOLL_ID][2]);
		}
		
		writeD(talismans);
		writeD(openCloak ? 0x01 : 0x00);
		
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_RHAND)); // Tauti
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LHAND)); // Tauti
		writeD(0); // Tauti
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_GLOVES)); // Tauti
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_CHEST)); // Tauti
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_LEGS)); // Tauti
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_FEET)); // Tauti
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_HAIR)); // Tauti
		writeD(inv.getVisualItemId(Inventory.PAPERDOLL_DHAIR)); // Tauti
		
		writeD(_patk);
		writeD(_patkspd);
		writeD(_pdef);
		writeD(evasion);
		writeD(accuracy);
		writeD(crit);
		writeD(_matk);
		writeD(_matkspd);
		writeD(_patkspd);
		writeD(_mdef);
		writeD(_mevasion);
		writeD(_maccuracy);
		writeD(_mCritRate);
		writeD(pvp_flag);
		writeD(karma);
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd); // swimspeed
		writeD(_swimWalkSpd); // swimspeed
		writeD(_flRunSpd);
		writeD(_flWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(move_speed);
		writeF(attack_speed);
		writeF(col_radius);
		writeF(col_height);
		writeD(hair_style);
		writeD(hair_color);
		writeD(face);
		writeD(gm_commands);
		writeS(title);
		writeD(clan_id);
		writeD(clan_crest_id);
		writeD(ally_id);
		writeD(ally_crest_id);
		// 0x40 leader rights
		// siege flags: attacker - 0x180 sword over name, defender - 0x80
		// shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
		writeD(_relation);
		writeC(mount_type); // mount type
		writeC(private_store);
		writeC(can_crystalize);
		writeD(pk_kills);
		writeD(pvp_kills);
		writeH(cubics.length);
		for (EffectCubic cubic : cubics)
		{
			writeH(cubic == null ? 0 : cubic.getId());
		}
		writeC(partyRoom ? 0x01 : 0x00); // 1-find party members
		writeC(isFlying ? 0x02 : 0x00);
		writeD(ClanPrivs);
		writeH(rec_left);
		writeH(rec_have);
		writeD(mount_id);
		writeH(InventoryLimit);
		writeD(class_id);
		writeD(0x00); // special effects? circles around player...
		writeD(maxCp);
		writeD(curCp);
		writeC(_enchant);
		writeC(_team.ordinal());
		writeD(large_clan_crest_id);
		writeC(noble);
		writeC(hero);
		writeC(fishing);
		writeD(_fishLoc.x);
		writeD(_fishLoc.y);
		writeD(_fishLoc.z);
		writeD(name_color);
		writeC(running);
		writeD(pledge_class);
		writeD(pledge_type);
		writeD(title_color);
		writeD(cw_level);
		writeD(transformation); // Transformation id
		
		// AttackElement (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5
		// - Dark, -2 - None)
		writeH(attackElement.getId());
		writeH(attackElementValue); // AttackElementValue
		writeH(defenceFire); // DefAttrFire
		writeH(defenceWater); // DefAttrWater
		writeH(defenceWind); // DefAttrWind
		writeH(defenceEarth); // DefAttrEarth
		writeH(defenceHoly); // DefAttrHoly
		writeH(defenceUnholy); // DefAttrUnholy
		
		writeD(agathion);
		
		// T2 Starts
		writeD(fame); // Fame
		writeD(_allowMap ? 1 : 0); // Minimap on Hellbound
		writeD(vitality); // Vitality Points
		
		writeD(0x00);// Unknown GOD
		writeD(0x00);// Unknown GOD (1 - Party searching?)
		writeC(_partySubstitute);
		writeD(0x00);// Unknown GOD
		
		if (_aveList != null)
		{
			writeD(_aveList.size());
			for (int i : _aveList)
			{
				writeD(i);
			}
		}
		else
		{
			writeD(0x00);
		}
		writeC(0x00);
	}
}