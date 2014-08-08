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
package quests;

import lineage2.gameserver.cache.Msg;
import lineage2.gameserver.model.instances.NpcInstance;
import lineage2.gameserver.model.quest.Quest;
import lineage2.gameserver.model.quest.QuestState;
import lineage2.gameserver.scripts.ScriptFile;
import lineage2.gameserver.utils.Location;

public class _10272_LightFragment extends Quest implements ScriptFile
{
	private static final int Orbyu = 32560;
	private static final int Artius = 32559;
	private static final int Lelikia = 32567;
	private static final int Ginby = 32566;
	private static final int Lekon = 32557;
	private static final int DestroyedDarknessFragmentPowder = 13853;
	private static final int DestroyedLightFragmentPowder = 13854;
	private static final int SacredLightFragment = 13855;
	private static final Location LELIKIA_POSITION = new Location(-170936, 247768, 1102);
	private static final Location BASE_POSITION = new Location(-185032, 242824, 1553);
	
	@Override
	public void onLoad()
	{
	}
	
	@Override
	public void onReload()
	{
	}
	
	@Override
	public void onShutdown()
	{
	}
	
	public _10272_LightFragment()
	{
		super(true);
		addStartNpc(Orbyu);
		addTalkId(Orbyu);
		addTalkId(Artius);
		addTalkId(Lelikia);
		addTalkId(Ginby);
		addTalkId(Lekon);
		addKillId(22552, 22541, 22550, 22551, 22596, 22544, 22540, 22547, 22542, 22543, 22539, 22546, 22548, 22536, 22538, 22537);
		addQuestItem(DestroyedDarknessFragmentPowder);
		addQuestItem(DestroyedLightFragmentPowder);
		addQuestItem(SacredLightFragment);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;
		
		if (event.equalsIgnoreCase("orbyu_q10272_2.htm") && (cond == 0))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("artius_q10272_2.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("artius_q10272_4.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("tele_to_lelikia"))
		{
			if (st.getQuestItemsCount(ADENA_ID) >= 10000)
			{
				st.takeItems(ADENA_ID, 10000);
				st.getPlayer().teleToLocation(LELIKIA_POSITION);
				return null;
			}
			
			st.getPlayer().sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return null;
		}
		else if (event.equalsIgnoreCase("lelikia_q10272_2.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("tele_to_base"))
		{
			st.getPlayer().teleToLocation(BASE_POSITION);
			return null;
		}
		else if (event.equalsIgnoreCase("artius_q10272_7.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("artius_q10272_9.htm"))
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("artius_q10272_11.htm"))
		{
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("lekon_q10272_2.htm"))
		{
			if (st.getQuestItemsCount(DestroyedLightFragmentPowder) >= 100)
			{
				st.takeItems(DestroyedLightFragmentPowder, -1);
				st.giveItems(SacredLightFragment, 1);
				st.setCond(8);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "lekon_q10272_1a.htm";
			}
		}
		else if (event.equalsIgnoreCase("artius_q10272_12.htm"))
		{
			st.giveItems(ADENA_ID, 2219330);
			st.addExpAndSp(2458030, 600000);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		QuestState TheEnvelopingDarkness = st.getPlayer().getQuestState(_10271_TheEnvelopingDarkness.class);
		
		if (npcId == Orbyu)
		{
			if (cond == 0)
			{
				if ((st.getPlayer().getLevel() >= 75) && (TheEnvelopingDarkness != null) && TheEnvelopingDarkness.isCompleted())
				{
					htmltext = "orbyu_q10272_1.htm";
				}
				else
				{
					htmltext = "orbyu_q10272_0.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 4)
			{
				htmltext = "orbyu_q10271_4.htm";
			}
		}
		else if (npcId == Artius)
		{
			if (cond == 1)
			{
				htmltext = "artius_q10272_1.htm";
			}
			else if (cond == 2)
			{
				htmltext = "artius_q10272_3.htm";
			}
			else if (cond == 4)
			{
				htmltext = "artius_q10272_5.htm";
			}
			else if (cond == 5)
			{
				if (st.getQuestItemsCount(DestroyedDarknessFragmentPowder) >= 100)
				{
					htmltext = "artius_q10272_8.htm";
				}
				else
				{
					htmltext = "artius_q10272_8a.htm";
				}
			}
			else if (cond == 6)
			{
				if (st.getQuestItemsCount(DestroyedLightFragmentPowder) >= 100)
				{
					htmltext = "artius_q10272_10.htm";
				}
				else
				{
					htmltext = "artius_q10272_10a.htm";
				}
			}
			else if (cond == 8)
			{
				htmltext = "artius_q10272_12.htm";
			}
		}
		else if (npcId == Ginby)
		{
			if (cond == 3)
			{
				htmltext = "ginby_q10272_1.htm";
			}
		}
		else if (npcId == Lelikia)
		{
			if (cond == 3)
			{
				htmltext = "lelikia_q10272_1.htm";
			}
		}
		else if (npcId == Lekon)
		{
			if ((cond == 7) && (st.getQuestItemsCount(DestroyedLightFragmentPowder) >= 100))
			{
				htmltext = "lekon_q10272_1.htm";
			}
			else
			{
				htmltext = "lekon_q10272_1a.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 5)
		{
			if (st.getQuestItemsCount(DestroyedDarknessFragmentPowder) <= 100)
			{
				st.giveItems(DestroyedDarknessFragmentPowder, 1);
				st.playSound(SOUND_ITEMGET);
			}
		}
		
		return null;
	}
}